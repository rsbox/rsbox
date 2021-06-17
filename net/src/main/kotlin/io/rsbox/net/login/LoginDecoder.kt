package io.rsbox.net.login

import io.netty.buffer.ByteBuf
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session

class LoginDecoder(private val session: Session) {

    private val rsboxConfig: RSBoxConfig by inject()

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

        if(out is LoginRequest.Error) {
            buf.resetReaderIndex()
            buf.skipBytes(payloadLength)
            return
        }
    }

    private fun retry() {
        if(++readAttempts >= 5) {
            out = LoginRequest.Error(ServerResponseType.COULD_NOT_COMPLETE_LOGIN)
        }
    }

    private fun resetAttempts() {
        readAttempts = 0
    }
}