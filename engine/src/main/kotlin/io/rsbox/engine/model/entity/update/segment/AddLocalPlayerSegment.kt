package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.model.entity.update.UpdateSegment

class AddLocalPlayerSegment(private val player: Player, private val tileHashSegment: PlayerTileHashSegment?) : UpdateSegment {

    override fun encode(buf: ByteBuf): ByteBuf {
        var bitBuf = buf.toBitMode()

        bitBuf.writeBits(1, 1)

        bitBuf.writeBits(0, 2)

        bitBuf.writeBits(if(tileHashSegment != null) 1 else 0, 1)

        val encodedBuf = tileHashSegment?.encode(bitBuf.toByteMode())
        if(encodedBuf != null) {
            bitBuf = encodedBuf.toBitMode()
        }

        bitBuf.writeBits(player.tile.x and 0x1FFF, 13)
        bitBuf.writeBits(player.tile.y and 0x1FFF, 13)
        bitBuf.writeBits(1, 1)

        return bitBuf.toByteMode()
    }
}