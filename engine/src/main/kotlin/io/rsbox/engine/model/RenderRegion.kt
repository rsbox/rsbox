package io.rsbox.engine.model

class RenderRegion(private val packed: Int) {

    val x: Int get() = packed and 0xFFFF

    val y: Int get() = (packed shr 16) and 0xFFFF

    constructor(x: Int, y: Int) : this(
        (x and 0xFFF) or ((y and 0xFFF) shl 16)
    )

    fun translate(xOffset: Int, yOffset: Int) = RenderRegion(
        x + xOffset,
        y + yOffset
    )

    fun translateX(offset: Int) = translate(offset, 0)

    fun translateY(offset: Int) = translate(0, offset)

    fun tile(level: Int) = Tile(
        x * SIZE,
        y * SIZE,
        level
    )

    fun chunk(level: Int) = Chunk(
        x * (SIZE / Chunk.SIZE),
        y * (SIZE / Chunk.SIZE),
        level
    )

    val region: Region get() = Region(
        x * (SIZE / Region.SIZE),
        y * (SIZE / Region.SIZE)
    )

    companion object {
        const val SIZE = 104
        const val REBUILD_DISTANCE = 16

        /**
         * Rendering settings
         */

        const val RENDER_PLAYERS_DISTANCE = 15
    }
}