package io.rsbox.engine.net.core

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

interface Protocol {

    val session: Session

    fun ingress(session: Session, buf: ByteBuf, out: MutableList<Any>) { throw UnsupportedOperationException() }

    fun egress(session: Session, msg: Message, out: ByteBuf) { throw UnsupportedOperationException() }

    val inbound: MessageCodecRegistry

    val outbound: MessageCodecRegistry

}