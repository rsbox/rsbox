package io.rsbox.engine.model

import io.rsbox.engine.module.XteaProvider

class Region(val id: Int) {

    val x: Int get() = id shr 8

    val y: Int get() = id and 0xFF

    constructor(x: Int, y: Int) : this((x shl 8) or y)

    fun translate(xOffset: Int, yOffset: Int) = Region(
        x + xOffset,
        y + yOffset
    )

    fun translateX(offset: Int) = translate(offset, 0)

    fun translateY(offset: Int) = translate(0, offset)

    val xteas: IntArray get() = XteaProvider[id]

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

    companion object {
        const val SIZE = 64
        val ZERO = Region(0)
    }
}