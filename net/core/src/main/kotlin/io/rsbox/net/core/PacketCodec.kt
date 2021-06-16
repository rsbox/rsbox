package io.rsbox.net.core

import io.netty.buffer.ByteBuf

interface PacketCodec<P : Packet> {

    fun decode(buf: ByteBuf): P { throw UnsupportedOperationException("Packet decoding not implemented.") }

    fun encode(out: ByteBuf, packet: P) { throw UnsupportedOperationException("Packet encoding not implemented.") }

    fun handle(session: Session, packet: P) { throw UnsupportedOperationException("Packet handling not implemented.") }

}