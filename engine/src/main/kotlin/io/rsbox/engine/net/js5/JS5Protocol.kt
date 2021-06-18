package io.rsbox.engine.net.js5

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.ServerResponseType
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.core.Message
import io.rsbox.engine.net.core.MessageCodecRegistry
import io.rsbox.engine.net.core.Protocol

class JS5Protocol(override val session: Session) : Protocol {

    override val inbound = MessageCodecRegistry(this)
    override val outbound = MessageCodecRegistry(this)

    init {
        inbound[-255] = JS5Request

        outbound[-255] = JS5Response
        outbound[-256] = ServerResponseType
    }

    override fun ingress(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        val codec = inbound[-255]
        val msg = codec.decode(session, buf) ?: return
        out.add(msg)
    }

    override fun egress(session: Session, msg: Message, out: ByteBuf) {
        val codec = if(msg is ServerResponseType) outbound[-256] else outbound[-255]
        codec.encode(session, out, msg)
    }
}