package io.rsbox.engine.model.entity

import io.rsbox.engine.model.Tile
import io.rsbox.engine.model.`interface`.InterfaceManager

/**
 * Represents a real human or bot player which is registered in the game world.
 */
class Player(val client: Client) : LivingEntity() {

    /**
     * The username of the player.
     */
    var username: String = ""

    /**
     * The password of the player hashed with SHA-256
     */
    var password: String = ""

    /**
     * The name that others see for the player.
     */
    var displayName: String = ""

    /**
     * The home tile of the player.
     */
    var homeTile: Tile = Tile(0, 0, 0)

    var skullIcon = -1

    var prayerIcon = -1

    var combatLevel = 3

    fun isOnline(): Boolean = world.players.contains(this)

    fun isOffline(): Boolean = !isOnline()

    val interfaces = InterfaceManager(client)

    /**
     * The appearance data of the player.
     */
    var appearance = Appearance.DEFAULT

    override fun preCycle() {
        client.session.cycle()
    }

    override fun cycle() {
        /*
         * Update the players for this player's viewport.
         */
        client.viewport.updatePlayer()
    }

    override fun postCycle() {
        client.session.flush()
    }
}