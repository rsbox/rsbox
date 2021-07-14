package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.update.UpdateSegment

class PlayerSkipCountSegment(private val count: Int) : UpdateSegment {

    override fun encode(buf: ByteBuf): ByteBuf {
        val bitBuf = buf.toBitMode()

        bitBuf.writeBits(0, 1)

        when {
            count == 0 -> {
                bitBuf.writeBits(0, 2)
            }

            count < 32 -> {
                bitBuf.writeBits(1, 2)
                bitBuf.writeBits(count, 5)
            }

            count < 256 -> {
                bitBuf.writeBits(2, 2)
                bitBuf.writeBits(count, 8)
            }

            count < 2048 -> {
                bitBuf.writeBits(3, 2)
                bitBuf.writeBits(count, 11)
            }
        }

        return bitBuf.toByteMode()
    }
}