package io.rsbox.net.login

import io.netty.buffer.ByteBuf
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodec

sealed class LoginRequest : Message {

    class Error(val response: ServerResponseType) : LoginRequest()

    class Valid : LoginRequest()

    override fun handle(session: Session) = LoginHandler.handle(session, this)

    companion object : MessageCodec<LoginRequest> {
        override fun decode(session: Session, buf: ByteBuf): LoginRequest? = session.loginDecoder?.decode(buf)
    }
}