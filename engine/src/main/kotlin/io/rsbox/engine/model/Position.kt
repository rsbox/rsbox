package io.rsbox.engine.model

class Position(val packed: Int) {

    constructor(x: Int, y: Int, level: Int = 0) : this((x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (level shl 30))

    val x: Int get() = packed and 0x7FFF

    val y: Int get() = (packed shr 15) and 0x7FFF

    val level: Int get() = (packed shr 30) and 0x3

    val as30bitInteger: Int get() =
        (y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((level and 0x3) shl 28)
}