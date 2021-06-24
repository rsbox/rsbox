package io.rsbox.engine.net.packet.outbound

import io.guthix.buffer.writeByteSub
import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 51, type = PacketType.FIXED)
class IfOpenTop(val interfaceId: Int) : Packet {
    companion object : Codec<IfOpenTop> {
        override fun encode(session: Session, packet: IfOpenTop, out: ByteBuf) {
            out.writeByteSub(packet.interfaceId shr 16)
            out.writeByteSub(packet.interfaceId and 0xFFFF)
        }
    }
}