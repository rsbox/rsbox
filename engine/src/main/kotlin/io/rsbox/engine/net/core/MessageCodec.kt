package io.rsbox.engine.net.core

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

interface MessageCodec<M : Message> {

    fun encode(session: Session, out: ByteBuf, msg: @UnsafeVariance M) { throw UnsupportedOperationException() }

    fun decode(session: Session, buf: ByteBuf): M? { throw UnsupportedOperationException() }

    fun handle(session: Session, msg: M) { throw UnsupportedOperationException() }

}