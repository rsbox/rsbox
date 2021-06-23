package io.rsbox.engine.net.game.model

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

interface PacketCodec<P : Packet> {

    fun encode(session: Session, packet: P, out: ByteBuf) {
        throw UnsupportedOperationException()
    }

    fun decode(session: Session, buf: ByteBuf): P {
        throw UnsupportedOperationException()
    }

    fun handle(session: Session, packet: @UnsafeVariance P) {
        throw UnsupportedOperationException()
    }

}