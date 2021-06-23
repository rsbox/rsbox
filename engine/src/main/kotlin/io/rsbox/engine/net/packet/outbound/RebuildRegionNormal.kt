package io.rsbox.engine.net.packet.outbound

import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 54, type = PacketType.VARIABLE_SHORT)
class RebuildRegionNormal(val player: Player, val gpi: Boolean = false) : Packet {
    companion object : Codec<RebuildRegionNormal> {
        override fun encode(session: Session, packet: RebuildRegionNormal, out: ByteBuf) {
            val chunkX = packet.player.tile.x shr 3
            val chunkY = packet.player.tile.y shr 3

            out.writeShortLE(chunkX)
            out.writeShort(chunkY)
            out.writeShort(0)
        }
    }
}