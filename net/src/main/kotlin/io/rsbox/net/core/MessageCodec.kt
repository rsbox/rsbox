package io.rsbox.net.core

import io.netty.buffer.ByteBuf
import io.rsbox.net.Session

interface MessageCodec<M : Message> {

    fun encode(out: ByteBuf, msg: @UnsafeVariance M) { throw UnsupportedOperationException() }

    fun decode(buf: ByteBuf): M { throw UnsupportedOperationException() }

    fun handle(session: Session, msg: M) { throw UnsupportedOperationException() }

}