package io.rsbox.engine.game

/**
 * Represents a coordinate within the game.
 * Holds x, z, and y values.
 *
 * x is east/west
 * y is north/south
 * level is up/down
 *
 * @property x the east-west axis location.
 * @property y the north-south axis locatio
 * @property level the plane or up-down  of the position.
 * @constructor
 */
class Position(val x: Int, val y: Int, val level: Int = 0, ) {

    /**
     * The position as a 30 bit packed integer.
     */
    val packed: Int = (x and 0x7FFF) or ((y and 0x7FFF) shl 15) or (level shl 30)

    /**
     * Create a position from a packed int.
     *
     * @param packed Int
     * @constructor
     */
    constructor(packed: Int) : this((packed and 0x7FFF), ((packed shr 15) and 0x7FFF), (packed ushr 30))
}