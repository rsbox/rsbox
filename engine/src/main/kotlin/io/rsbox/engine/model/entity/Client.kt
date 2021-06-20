package io.rsbox.engine.model.entity

import io.rsbox.engine.net.Session

/**
 * Represents a player's connect client and holds the open network session associated
 * with a world player.
 *
 * @property session Session
 * @property player Player
 * @constructor
 */
class Client {

    /**
     * The player associated with this client instance.
     */
    lateinit var player: Player internal set

    /**
     * The network session associated with this client instance.
     */
    lateinit var session: Session internal set

    var isResizableMode: Boolean = false

    var width: Int = 0

    var height: Int = 0

    /**
     * Processed ever game server tick.
     */
    fun cycle() {
        session.cycle()
    }


}