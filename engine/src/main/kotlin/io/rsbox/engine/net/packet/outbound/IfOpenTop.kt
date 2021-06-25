package io.rsbox.engine.net.packet.outbound

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 38, type = PacketType.FIXED)
class IfOpenTop(val interfaceId: Int) : Packet {
    companion object : Codec<IfOpenTop> {
        override fun encode(session: Session, packet: IfOpenTop, out: ByteBuf) {
            out.writeShortLE(packet.interfaceId)
        }
    }
}