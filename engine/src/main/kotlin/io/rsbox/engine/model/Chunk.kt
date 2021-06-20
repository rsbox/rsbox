package io.rsbox.engine.model

/**
 * Represents a 8x8 tile square within the game world.
 *
 * @property packed The bit-packed coordinate value of this chunk.
 */
class Chunk(private val packed: Int) {

    val x: Int get() = packed and 0x7FFF

    val y: Int get() = (packed shr 15) and 0x7FFF

    val level: Int get() = (packed shr 30) and 0x3

    constructor(x: Int, y: Int, level: Int = 0) : this(
        (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (level shl 30)
    )

    companion object {
        const val SIZE = 8
    }
}