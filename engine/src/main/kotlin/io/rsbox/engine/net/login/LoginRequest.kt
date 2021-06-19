package io.rsbox.engine.net.login

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.ServerResponseType
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.core.Message
import io.rsbox.engine.net.core.MessageCodec

sealed class LoginRequest : Message {

    class Error(val response: ServerResponseType) : LoginRequest()

    class Normal(
        val session: Session,
        val username: String,
        val password: String?,
        val authCode: Int?,
        val isResizableMode: Boolean,
        val clientWidth: Int,
        val clientHeight: Int,
        val operatingSystem: Int,
        val x64: Boolean,
        val osVersion: Int,
        val javaVendor: Int,
        val javaMajorVersion: Int,
        val javaMinorVersion: Int,
        val javaPatchVersion: Int,
        val maxMemory: Int,
        val cpuCount: Int
    ) : LoginRequest()

    class Reconnect(
        val session: Session,
        val username: String,
    ) : LoginRequest()

    override fun handle(session: Session) = LoginHandler.handle(session, this)

    companion object : MessageCodec<LoginRequest> {
        override fun decode(session: Session, buf: ByteBuf): LoginRequest? = session.loginDecoder?.decode(buf)
    }
}