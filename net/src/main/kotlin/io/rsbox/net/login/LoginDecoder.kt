package io.rsbox.net.login

import io.guthix.buffer.readIntIME
import io.guthix.buffer.readIntME
import io.guthix.buffer.readString0CP1252
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.cache.GameCache
import io.rsbox.common.di.inject
import io.rsbox.common.rsa.RSA
import io.rsbox.common.util.Xtea
import io.rsbox.config.RSBoxConfig
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session
import java.math.BigInteger

class LoginDecoder(private val session: Session) {

    private val rsboxConfig: RSBoxConfig by inject()
    private val RSA: RSA by inject()
    private val gameCache: GameCache by inject()

    enum class LoginState {
        HANDSHAKE,
        HEADER,
        PAYLOAD
    }

    enum class LoginType(val opcode: Int) {
        NORMAL(16),
        RECONNECT(18),
        INVALID(Int.MAX_VALUE);

        companion object {
            val values = enumValues<LoginType>()
            fun fromOpcode(opcode: Int): LoginType = values.firstOrNull { it.opcode == opcode } ?: INVALID
        }
    }

    private var loginState = LoginState.HANDSHAKE
    private var loginType = LoginType.INVALID
    private var payloadLength: Int = -1
    private var readAttempts = 0
    private var out: LoginRequest? = null

    /**
     * Login data fields
     */

    private var clientType: Int = 0

    fun decode(buf: ByteBuf): LoginRequest? {
        when(loginState) {
            LoginState.HANDSHAKE -> this.readHandshake(buf)
            LoginState.HEADER -> this.readHeader(buf)
            LoginState.PAYLOAD -> this.readPayload(buf)
        }

        val result = out
        out = null

        return result
    }

    /**
     * Reads the login handshake to determine what kind of login it is.
     * Either a normal login or a reconnection login.
     *
     * @param buf ByteBuf
     */
    private fun readHandshake(buf: ByteBuf) {
        val opcode = buf.readUnsignedByte().toInt()
        loginType = LoginType.fromOpcode(opcode)

        if(loginType == LoginType.INVALID) {
            this.out = LoginRequest.Error(ServerResponseType.INVALID_LOGINSERVER)
            buf.resetReaderIndex()

            return
        }

        loginState = LoginState.HEADER
    }

    private fun readHeader(buf: ByteBuf) {
        if(buf.readableBytes() < Short.SIZE_BYTES) {
            this.retry()
            return
        }

        payloadLength = buf.readUnsignedShort()
        if(payloadLength == 0) {
            out = LoginRequest.Error(ServerResponseType.MALFORMED_PACKET)
            return
        }

        this.resetAttempts()
        loginState = LoginState.PAYLOAD
    }

    private fun readPayload(buf: ByteBuf) {
        if(buf.readableBytes() < payloadLength) {
            this.retry()
            return
        }

        buf.markReaderIndex()

        val revision = buf.readInt()
        val serverRevision = rsboxConfig.revision

        if(revision != serverRevision) {
            out = LoginRequest.Error(ServerResponseType.REVISION_MISMATCH)
            return
        }

        /*
         * Skip minor revision
         */
        buf.skipBytes(Int.SIZE_BYTES)

        clientType = buf.readUnsignedByte().toInt()

        /*
         * Skip unknown byte
         */
        buf.skipBytes(Byte.SIZE_BYTES)

        /*
         * Decrypt the RSA encrypted secure buffer.
         * This is used byte the client to send sensitive data during login such
         * as the username and password fields.
         *
         * Doing this prevents packet sniffing of credentials during login should a client
         * be on a compromised network.
         */
        val secureBufLength = buf.readUnsignedShort()
        val data = ByteArray(secureBufLength)
        buf.readBytes(data)
        val decryption = BigInteger(data).modPow(RSA.exponent, RSA.modulus)

        /*
         * The decrypted secure buffer
         */
        val secureBuf = Unpooled.wrappedBuffer(decryption.toByteArray())

        val xteaPayload = ByteArray(buf.readableBytes())
        buf.readBytes(xteaPayload)

        /*
         * ==========================================
         * = SECURE BUFFER READ
         * ==========================================
         */

        val decryptionCheck = secureBuf.readUnsignedByte().toInt()
        if(decryptionCheck != 1) {
            out = LoginRequest.Error(ServerResponseType.BAD_SESSION_ID)
            return
        }

        val xteas = IntArray(4) { secureBuf.readInt() }
        val seed = secureBuf.readLong()

        if(session.seed != seed) {
            out = LoginRequest.Error(ServerResponseType.COULD_NOT_COMPLETE_LOGIN)
            return
        }

        val authCode: Int?
        val password: String?
        var reconnectXteas: IntArray? = null

        if(loginType == LoginType.RECONNECT) {
            reconnectXteas = IntArray(4) { secureBuf.readInt() }
            authCode = null
            password = null
        } else {
            authCode = when(secureBuf.readByte().toInt()) {
                2 -> {
                    secureBuf.skipBytes(Int.SIZE_BYTES)
                    -1
                }

                1, 3 -> {
                    val auth = secureBuf.readUnsignedMedium()
                    secureBuf.skipBytes(Byte.SIZE_BYTES)
                    auth
                }

                else -> secureBuf.readInt()
            }

            secureBuf.skipBytes(Byte.SIZE_BYTES)
            password = secureBuf.readStringCP1252()
        }

        /*
         * ==========================================
         * = XTEA BUFFER READ
         * ==========================================
         */

        val deciphered = Xtea.decipher(xteaPayload, xteas)
        val xteaBuf = Unpooled.wrappedBuffer(deciphered)

        val username = xteaBuf.readStringCP1252()
        if(username.isBlank()) {
            out = LoginRequest.Error(ServerResponseType.INVALID_CREDENTIALS)
            return
        }

        /*
         * Read client settings information
         */

        val flags = xteaBuf.readByte().toInt()
        val isResizableMode = (flags shr 1) == 1
        val clientWidth = xteaBuf.readUnsignedShort()
        val clientHeight = xteaBuf.readUnsignedShort()

        /*
         * Read the random client bytes which come from the client's generated random.dat file.
         * This is used to identify client machines as it is stored persistently on their filesystem.
         */
        val uniqueBytes = ByteArray(24) { xteaBuf.readByte() }

        /*
         * Skip site settings
         */
        xteaBuf.readStringCP1252()
        xteaBuf.skipBytes(Int.SIZE_BYTES)

        /*
         * Read client platform / machine information
         */
        val platformInfoHeader = xteaBuf.readUnsignedByte().toInt()
        if(platformInfoHeader != 8) {
            out = LoginRequest.Error(ServerResponseType.INVALID_CREDENTIALS)
            return
        }

        val operatingSystem = xteaBuf.readUnsignedByte().toInt()
        val is64Bit = xteaBuf.readUnsignedByte().toInt()
        val osVersion = xteaBuf.readUnsignedShort()
        val javaVendor = xteaBuf.readUnsignedByte().toInt()
        val javaMajorVersion = xteaBuf.readUnsignedByte().toInt()
        val javaMinorVersion = xteaBuf.readUnsignedByte().toInt()
        val javaPatchVersion = xteaBuf.readUnsignedByte().toInt()
        xteaBuf.skipBytes(Byte.SIZE_BYTES)
        val maxMemory = xteaBuf.readUnsignedShort()
        val cpuCount = xteaBuf.readUnsignedByte().toInt()

        /*
         * Skip the rest of the platform information
         */
        xteaBuf.skipBytes(3)
        xteaBuf.skipBytes(Short.SIZE_BYTES)
        xteaBuf.readString0CP1252()
        xteaBuf.readString0CP1252()
        xteaBuf.readString0CP1252()
        xteaBuf.readString0CP1252()
        xteaBuf.skipBytes(Byte.SIZE_BYTES)
        xteaBuf.skipBytes(Short.SIZE_BYTES)
        xteaBuf.readString0CP1252()
        xteaBuf.readString0CP1252()
        xteaBuf.skipBytes(Byte.SIZE_BYTES)
        xteaBuf.skipBytes(Byte.SIZE_BYTES)
        repeat(3) { xteaBuf.skipBytes(Int.SIZE_BYTES) }
        xteaBuf.skipBytes(Int.SIZE_BYTES)
        xteaBuf.readString0CP1252()

        xteaBuf.skipBytes(Byte.SIZE_BYTES)
        xteaBuf.skipBytes(Int.SIZE_BYTES)

        /*
         * ==========================================
         * = CACHE ARCHIVE CRC READ
         * ==========================================
         *
         * FUCK YOU Jagex! They now appear to randomize the order and the
         * protocol of the archive crcs each revision.
         */

        val clientCrcs = IntArray(21)
        val serverCrcs = gameCache.crcs

        /*
         * The order of the archives is specific to each client revision and must be changed
         * when upgrade the server's OSRS revision.
         */
        clientCrcs[1] = xteaBuf.readIntIME()
        clientCrcs[12] = xteaBuf.readIntME()
        clientCrcs[5] = xteaBuf.readInt()
        clientCrcs[16] = xteaBuf.readIntME()
        clientCrcs[4] = xteaBuf.readIntLE()
        clientCrcs[7] = xteaBuf.readIntME()
        clientCrcs[11] = xteaBuf.readIntME()
        clientCrcs[3] = xteaBuf.readIntLE()
        clientCrcs[10] = xteaBuf.readIntIME()
        clientCrcs[0] = xteaBuf.readInt()
        clientCrcs[14] = xteaBuf.readIntME()
        clientCrcs[6] = xteaBuf.readIntIME()
        clientCrcs[2] = xteaBuf.readInt()
        clientCrcs[19] = xteaBuf.readInt()
        clientCrcs[18] = xteaBuf.readInt()
        clientCrcs[17] = xteaBuf.readIntIME()
        clientCrcs[13] = xteaBuf.readIntLE()
        clientCrcs[9] = xteaBuf.readIntME()
        clientCrcs[15] = xteaBuf.readIntLE()
        clientCrcs[8] = xteaBuf.readInt()
        clientCrcs[20] = xteaBuf.readIntIME()

        /*
         * Verify the client CRC's match the server's archive crc's
         */
        for(i in clientCrcs.indices) {
            val clientCrc = clientCrcs[i]
            val serverCrc = serverCrcs[i]
            if(clientCrc > 0 && clientCrc != serverCrc) {
                out = LoginRequest.Error(ServerResponseType.REVISION_MISMATCH)
                return
            }
        }

        if(out is LoginRequest.Error) {
            buf.resetReaderIndex()
            buf.skipBytes(payloadLength)
            return
        }

        println("LOGIN DECODER FINISHED SUCCESSFUL")
    }

    private fun retry() {
        if(++readAttempts >= 5) {
            out = LoginRequest.Error(ServerResponseType.COULD_NOT_COMPLETE_LOGIN)
        }
    }

    private fun resetAttempts() {
        readAttempts = 0
    }

    companion object {

    }
}