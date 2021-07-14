package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.update.UpdateSegment

class SignalPlayerUpdateSegment : UpdateSegment {

    override fun encode(buf: ByteBuf): ByteBuf {
        val bitBuf = buf.toBitMode()

        bitBuf.writeBits(1, 1)

        bitBuf.writeBits(1, 1)

        bitBuf.writeBits(0, 2)

        return bitBuf.toByteMode()
    }
}