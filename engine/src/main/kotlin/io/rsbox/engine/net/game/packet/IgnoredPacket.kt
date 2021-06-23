package io.rsbox.engine.net.game.packet

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.Packet
import io.rsbox.engine.net.game.model.PacketCodec

class IgnoredPacket : Packet {
    companion object : PacketCodec<IgnoredPacket> {
        override fun decode(session: Session, buf: ByteBuf): IgnoredPacket {
            return IgnoredPacket()
        }
    }
}