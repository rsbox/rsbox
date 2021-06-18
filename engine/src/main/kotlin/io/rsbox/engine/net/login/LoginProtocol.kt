package io.rsbox.engine.net.login

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.ServerResponseType
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.core.Message
import io.rsbox.engine.net.core.MessageCodec
import io.rsbox.engine.net.core.MessageCodecRegistry
import io.rsbox.engine.net.core.Protocol

class LoginProtocol(override val session: Session) : Protocol {

    override val inbound = MessageCodecRegistry(this)
    override val outbound = MessageCodecRegistry(this)

    init {
        /*
         * Inbound
         */
        inbound[-255] = LoginRequest

        /*
         * Outbound
         */
        outbound[-255] = ServerResponseType
    }

    override fun ingress(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        val codec = inbound[-255]
        val msg = codec.decode(session, buf) ?: return
        out.add(msg)
    }

    @Suppress("UNCHECKED_CAST")
    override fun egress(session: Session, msg: Message, out: ByteBuf) {
        val codec = outbound[-255] as MessageCodec<ServerResponseType>
        codec.encode(session, out, msg as ServerResponseType)
    }
}