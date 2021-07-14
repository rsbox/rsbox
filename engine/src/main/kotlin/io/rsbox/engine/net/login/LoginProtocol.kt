package io.rsbox.engine.net.login

import io.guthix.buffer.readString0CP1252
import io.guthix.buffer.readStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.cache.old.GameCache
import io.rsbox.common.di.inject
import io.rsbox.common.rsa.RSA
import io.rsbox.common.util.Xtea
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Protocol
import io.rsbox.engine.net.ServerStatus
import io.rsbox.engine.net.Session
import io.rsbox.engine.service.ServiceManager
import io.rsbox.engine.service.login.LoginService
import java.math.BigInteger

class LoginProtocol(override val session: Session) : Protocol {

    private val config: RSBoxConfig by inject()
    private val rsa: RSA by inject()
    private val gameCache: GameCache by inject()
    private val serviceManager: ServiceManager by inject()

    private val loginService = serviceManager[LoginService::class]

    enum class LoginStage {
        HANDSHAKE,
        HEADER,
        PAYLOAD
    }

    enum class LoginType(val opcode: Int) {
        NORMAL(opcode = 16),
        RECONNECT(opcode = 18),
        UNKNOWN(opcode = Int.MIN_VALUE);

        companion object {
            val values = enumValues<LoginType>()
            fun fromOpcode(opcode: Int): LoginType = values.firstOrNull { it.opcode == opcode } ?: UNKNOWN
        }
    }

    private var loginStage = LoginStage.HANDSHAKE
    private var loginType = LoginType.UNKNOWN
    private var payloadLength: Int = 0
    private var readRetries = 0

    override fun decode(buf: ByteBuf, out: MutableList<Any>) {
        try {
            when(loginStage) {
                LoginStage.HANDSHAKE -> readHandshake(buf, out)
                LoginStage.HEADER -> readHeader(buf, out)
                LoginStage.PAYLOAD -> readPayload(buf, out)
            }
        } catch(e : LoginError) {
            buf.skipBytes(buf.readableBytes())
            buf.resetReaderIndex()
            out.add(e.error)
        }
    }

    private fun readHandshake(buf: ByteBuf, out: MutableList<Any>) {
        val opcode = buf.readUnsignedByte().toInt()
        loginType = LoginType.fromOpcode(opcode)

        if(loginType == LoginType.UNKNOWN) {
            buf.resetReaderIndex()
            throw LoginError(ServerStatus.MALFORMED_PACKET)
        }

        loginStage = LoginStage.HEADER
    }

    private fun readHeader(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() < Short.SIZE_BYTES) {
            this.retryRead()
            return
        }

        payloadLength = buf.readUnsignedShort()
        if(payloadLength == 0) {
            throw LoginError(ServerStatus.MALFORMED_PACKET)
        }

        this.resetRetries()
        loginStage = LoginStage.PAYLOAD
    }

    private fun readPayload(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() < payloadLength) {
            this.retryRead()
            return
        }

        buf.markReaderIndex()

        val revision = buf.readInt()
        val serverRevision = config.revision

        if(revision != serverRevision) {
            throw LoginError(ServerStatus.REVISION_MISMATCH)
        }

        buf.skipBytes(Int.SIZE_BYTES)
        val clientType = buf.readUnsignedByte()
        buf.skipBytes(Byte.SIZE_BYTES)

        val data = ByteArray(buf.readUnsignedShort())
        buf.readBytes(data)
        val decrypt = BigInteger(data).modPow(rsa.exponent, rsa.modulus)

        val secureBuf = Unpooled.wrappedBuffer(decrypt.toByteArray())

        val xteaPayload = ByteArray(buf.readableBytes())
        buf.readBytes(xteaPayload)

        /*
         * ==========================================
         * = SECURE BUFFER READ
         * ==========================================
         */

        val decryptionCheck = secureBuf.readUnsignedByte().toInt()
        if(decryptionCheck != 1) {
            throw LoginError(ServerStatus.MALFORMED_PACKET)
        }

        val xteas = IntArray(4) { secureBuf.readInt() }
        val seed = secureBuf.readLong()

        if(session.seed != seed) {
            throw LoginError(ServerStatus.INVALID_CREDENTIALS)
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
                    secureBuf.readBytes(Int.SIZE_BYTES)
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

            /*
             * ==========================================
             * = XTEA BUFFER READ
             * ==========================================
             */

            val deciphered = Xtea.decipher(xteaPayload, xteas)
            val xteaBuf = Unpooled.wrappedBuffer(deciphered)

            val username =xteaBuf.readStringCP1252()
            if(username.isBlank()) {
                throw LoginError(ServerStatus.INVALID_CREDENTIALS)
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
                throw LoginError(ServerStatus.INVALID_CREDENTIALS)
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
             */

            val serverCrcs = gameCache.crcs.toIntArray()
            val clientCrcs = IntArray(21) { xteaBuf.readInt() }

            /*
             * Verify client crcs match the server crcs
             */
            for(i in clientCrcs.indices) {
                val clientCrc = clientCrcs[i]
                val serverCrc = serverCrcs[i]

                if(clientCrc != serverCrc) {
                    /*
                     * Skip mismatch for archive 16 as it is not sent to the client.
                     */
                    if(i == 16) {
                        continue
                    }

                    throw LoginError(ServerStatus.REVISION_MISMATCH)
                }
            }

            val request = LoginRequest(
                session,
                username,
                password,
                xteas,
                authCode,
                isResizableMode,
                clientWidth,
                clientHeight
            )

            out.add(request)
        }
    }

    private fun retryRead() {
        if(++readRetries >= 5) {
            throw LoginError(ServerStatus.COULD_NOT_COMPLETE_LOGIN)
        }
    }

    private fun resetRetries() {
        readRetries = 0
    }

    override fun encode(message: Message, out: ByteBuf) {
        when(message) {
            is ServerStatus -> {
                out.writeByte(message.id)
            }

            is LoginResponse -> {
                out.writeByte(2) // login opcode
                out.writeByte(13) // length
                out.writeBoolean(false) // isTrustedDevice
                out.writeInt(0) // Always sent if isTrustedDevice = false
                out.writeByte(2) // Privilege Level
                out.writeBoolean(true) // isModerator
                out.writeShort(message.player.index) // Player index
                out.writeBoolean(true) // isMember
            }
        }
    }

    override fun handle(session: Session, message: Message) {
        when(message) {
            is ServerStatus -> {
                session.writeAndClose(message)
            }

            is LoginRequest -> {
                loginService.queue(message)
            }
        }
    }
}