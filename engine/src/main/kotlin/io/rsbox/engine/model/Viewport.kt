package io.rsbox.engine.model

/**
 * Represents a client's viewport within the game world.
 */
class Viewport(
    val center: Tile,
    val regions: MutableList<Region>
) {
    companion object {
        val ZERO = Viewport(Tile.ZERO, mutableListOf())
    }
}