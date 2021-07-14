package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.toBitMode
import io.netty.buffer.ByteBuf
import io.rsbox.engine.model.entity.update.UpdateSegment
import kotlin.math.abs

class PlayerTileHashSegment(private val last: Int, private val current: Int) : UpdateSegment {

    override fun encode(buf: ByteBuf): ByteBuf {
        val bitBuf = buf.toBitMode()

        val lastX = (last shr 8) and 0xFF
        val lastY = last and 0xFF
        val lastLvl = last shr 16

        val currentX = (current shr 8) and 0xFF
        val currentY = current and 0xFF
        val currentLvl = current shr 16

        val dx = currentX - lastX
        val dy = currentY - lastY
        val dl = (currentLvl - lastLvl) and 0x3

        if(lastX == currentX && lastY == currentY) {
            bitBuf.writeBits(1, 2)
            bitBuf.writeBits(dl, 2)
        }
        else if(abs(dx) <= 1 && abs(dy) <= 1) {
            val dir: Int
            val nx = currentX - lastX
            val ny = currentY - lastY

            if(nx == -1 && ny == -1) {
                dir = 0
            }
            else if(nx == 1 && ny == -1) {
                dir = 2
            }
            else if(nx == -1 && ny == 1) {
                dir = 5
            }
            else if(nx == 1 && ny == 1) {
                dir = 7
            }
            else if(ny == -1) {
                dir = 1
            }
            else if(nx == -1) {
                dir = 3
            }
            else if(nx == 1) {
                dir = 4
            }
            else {
                dir = 6
            }

            bitBuf.writeBits(2, 2)
            bitBuf.writeBits(dl, 2)
            bitBuf.writeBits(dir, 3)
        } else {
            bitBuf.writeBits(3, 2)
            bitBuf.writeBits(dl, 2)
            bitBuf.writeBits(dx and 0xFF, 8)
            bitBuf.writeBits(dy and 0xFF, 8)
        }

        return bitBuf.toByteMode()
    }
}