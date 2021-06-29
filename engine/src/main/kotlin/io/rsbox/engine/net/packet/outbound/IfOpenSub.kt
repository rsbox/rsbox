package io.rsbox.engine.net.packet.outbound

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 48, type = PacketType.FIXED)
class IfOpenSub(val parent: Int, val child: Int, val component: Int, val type: Int) : Packet {
    companion object : Codec<IfOpenSub> {
        override fun encode(session: Session, packet: IfOpenSub, out: ByteBuf) {
            out.writeByte(packet.type)
            out.writeInt((packet.parent shl 16) or packet.child)
            out.writeShort(packet.component)
        }
    }
}