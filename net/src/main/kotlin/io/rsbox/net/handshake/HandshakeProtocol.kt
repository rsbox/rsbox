package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodecRegistry
import io.rsbox.net.core.Protocol

class HandshakeProtocol(override val session: Session) : Protocol {

    override val inbound = MessageCodecRegistry(this)
    override val outbound = MessageCodecRegistry(this)

    init {
        inbound[-255] = HandshakeRequest
        outbound[-255] = ServerResponseType
    }

    override fun ingress(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        val codec = inbound[-255]
        val msg = codec.decode(buf)
        out.add(msg)
    }

    override fun egress(session: Session, msg: Message, out: ByteBuf) {
        val codec = outbound[-255]
        codec.encode(out, msg)
    }
}