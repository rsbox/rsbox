package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

interface Codec<P : Packet> {

    fun encode(session: Session, packet: P, out: ByteBuf) {
        throw UnsupportedOperationException()
    }

    fun decode(session: Session, buf: ByteBuf): P {
        throw UnsupportedOperationException()
    }

}