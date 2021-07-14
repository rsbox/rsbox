package io.rsbox.engine.model

import io.rsbox.engine.model.entity.Player

/**
 * Represents the game world.
 */
class World {

    val players = EntityList<Player>(MAX_PLAYER_COUNT)


    /**
     * Invoked every game cycle
     */
    fun cycle() {

    }

    companion object {
        private const val MAX_PLAYER_COUNT = 2047
        private const val MAX_NPC_COUNT = 32767
    }
}