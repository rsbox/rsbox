package io.rsbox.engine.net.packet.outbound

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.model.entity.Viewport
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.Codec
import io.rsbox.engine.net.game.OutboundPacket
import io.rsbox.engine.net.game.Packet
import io.rsbox.engine.net.game.PacketType

@OutboundPacket(opcode = 54, type = PacketType.VARIABLE_SHORT)
class RebuildRegionNormal(val player: Player, val gpi: Boolean = false) : Packet {
    companion object : Codec<RebuildRegionNormal> {
        override fun encode(session: Session, packet: RebuildRegionNormal, out: ByteBuf) {
            if(packet.gpi) {
                packet.player.client.viewport.encodeGpi(packet, out)
            }
        }

        private fun Viewport.encodeGpi(packet: RebuildRegionNormal, out: ByteBuf) {
            val viewport = packet.player.client.viewport

            val tileHashes = IntArray(2048) { viewport.gpiLocalPlayers[it]?.tile?.as18BitInteger ?: 0 }

            val bitBuf = out.toBitMode()
            bitBuf.writeBits(player.tile.as30bitInteger, 30)

            for(i in 1 until 2048) {
                if(i != player.index) {
                    bitBuf.writeBits(tileHashes[i], 18)
                }
            }

            val gpi = bitBuf.toByteMode()
            out.writeBytes(gpi)
        }
    }
}