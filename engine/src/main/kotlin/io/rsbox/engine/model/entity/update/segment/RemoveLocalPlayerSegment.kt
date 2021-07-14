package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.update.UpdateSegment

class RemoveLocalPlayerSegment(private val updateTileHash: Boolean) : UpdateSegment {

    override fun encode(buf: ByteBuf): ByteBuf {
        val bitBuf = buf.toBitMode()

        bitBuf.writeBits(1, 1)

        bitBuf.writeBits(0, 1)

        bitBuf.writeBits(0, 2)

        bitBuf.writeBits(if(updateTileHash) 1 else 0, 1)

        return bitBuf.toByteMode()
    }
}