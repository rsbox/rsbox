package io.rsbox.engine.net.packet.outbound

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.engine.model.Chunk
import io.rsbox.engine.model.Region
import io.rsbox.engine.model.RenderRegion
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
                val gpiBytes = packet.player.client.viewport.encodeGpi(packet, session.buffer())
                out.writeBytes(gpiBytes)
            }

            val tile = packet.player.tile

            val chunkX = tile.x shr 3
            val chunkY = tile.y shr 3

            out.writeShortLE(chunkX)
            out.writeShort(chunkY)

            val lx = (chunkX - 6) / Chunk.SIZE
            val rx = (chunkX + 6) / Chunk.SIZE
            val ly = (chunkY - 6) / Chunk.SIZE
            val ry = (chunkY + 6) / Chunk.SIZE

            val regions = mutableListOf<Region>()
            for(x in lx..rx) {
                for(y in ly..ry) {
                    regions.add(Region(x, y))
                }
            }

            out.writeShort(regions.size)
            regions.forEach { region ->
                region.xteas.forEach { out.writeInt(it) }
            }
        }

        private fun Viewport.encodeGpi(packet: RebuildRegionNormal, out: ByteBuf): ByteArray {
            val viewport = packet.player.client.viewport

            val tileHashes = IntArray(2048) { viewport.gpiLocalPlayers[it]?.tile?.as18BitInteger ?: 0 }

            val bitBuf = out.toBitMode()
            bitBuf.writeBits(player.tile.as30bitInteger, 30)

            for(i in 1 until 2048) {
                if(i != player.index) {
                    bitBuf.writeBits(tileHashes[i], 18)
                }
            }

            val buf = bitBuf.toByteMode()
            val gpi = ByteArray(buf.readableBytes())
            buf.readBytes(gpi)

            return gpi
        }
    }
}