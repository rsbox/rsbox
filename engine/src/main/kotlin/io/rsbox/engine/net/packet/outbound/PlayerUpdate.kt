package io.rsbox.engine.net.packet.outbound

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 61, type = PacketType.VARIABLE_SHORT)
class PlayerUpdate(val payload: ByteBuf) : Packet {
    companion object : Codec<PlayerUpdate> {
        override fun encode(session: Session, packet: PlayerUpdate, out: ByteBuf) {
            val bytes = ByteArray(packet.payload.readableBytes())
            packet.payload.readBytes(bytes)
            out.writeBytes(bytes)
        }
    }
}