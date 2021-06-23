package io.rsbox.engine.net.game.packet.outbound

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.Packet
import io.rsbox.engine.net.game.model.PacketCodec

class LoadRegionNormal(val x: Int, val y: Int) : Packet {

    companion object : PacketCodec<LoadRegionNormal> {
        override fun encode(session: Session, packet: LoadRegionNormal, out: ByteBuf) {
            /*
             * Random invalid crap for now
             */
            val x = 100 shr 3
            val y = 100 shr 3

            out.writeShortLE(x)
            out.writeShort(y)
            out.writeShort(5)
        }
    }
}