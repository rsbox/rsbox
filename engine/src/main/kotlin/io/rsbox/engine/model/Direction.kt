package io.rsbox.engine.model

enum class Direction(
    val orientation: Int,
    val playerValue: Int,
    val npcValue: Int
) {

    NONE(-1, -1, -1),

    NORTH_WEST(0, 5, 0),

    NORTH(1, 6, 1),

    NORTH_EAST(2, 7, 2),

    WEST(3, 3, 3),

    EAST(4, 4, 4),

    SOUTH_WEST(5, 0, 5),

    SOUTH(6, 1, 6),

    SOUTH_EAST(7, 2, 7);

    val deltaX: Int get() = when(this) {
        SOUTH_EAST, NORTH_EAST, EAST -> 1
        SOUTH_WEST, NORTH_WEST, WEST -> -1
        else -> 0
    }

    val deltaY: Int get() = when(this) {
        NORTH_WEST, NORTH_EAST, NORTH -> 1
        SOUTH_WEST, SOUTH_EAST, SOUTH -> -1
        else -> 0
    }
}