package io.rsbox.engine.net

import io.netty.buffer.ByteBuf

interface Protocol {

    val session: Session

    fun encode(message: Message, out: ByteBuf) { throw UnsupportedOperationException() }

    fun decode(buf: ByteBuf, out: MutableList<Any>) { throw UnsupportedOperationException() }

    fun handle(session: Session, message: Message) { throw UnsupportedOperationException() }

}