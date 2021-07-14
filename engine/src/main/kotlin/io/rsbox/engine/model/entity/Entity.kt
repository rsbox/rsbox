package io.rsbox.engine.model.entity

import io.rsbox.common.di.inject
import io.rsbox.engine.Engine
import io.rsbox.engine.model.Tile
import io.rsbox.engine.model.World

abstract class Entity {

    /**
     * The game engine instance
     */
    val engine: Engine by inject()

    /**
     * The game world instance
     */
    val world: World by inject()

    /**
     * The index value of this entity in the game world
     */
    var index: Int = Int.MIN_VALUE

    /**
     * The current tile location of the entity.
     */
    var tile: Tile = Tile(0, 0, 0)

}