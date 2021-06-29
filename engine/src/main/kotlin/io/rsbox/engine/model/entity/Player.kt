package io.rsbox.engine.model.entity

import io.rsbox.engine.event.EventPriority
import io.rsbox.engine.event.impl.PlayerLoginEvent
import io.rsbox.engine.event.on_event
import io.rsbox.engine.model.Tile
import io.rsbox.engine.model.`interface`.DisplayMode
import io.rsbox.engine.model.`interface`.InterfaceManager
import io.rsbox.engine.net.packet.outbound.IfOpenTop
import io.rsbox.engine.net.packet.outbound.RebuildRegionNormal

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

    fun isOnline(): Boolean = world.players.contains(this)

    fun isOffline(): Boolean = !isOnline()

    val interfaces = InterfaceManager(client)
}