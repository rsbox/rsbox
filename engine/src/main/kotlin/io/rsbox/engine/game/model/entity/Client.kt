package io.rsbox.engine.game.model.entity

import io.rsbox.engine.net.Session

/**
 * Represents a player's client connection.
 *
 * @property session Session
 * @property player Player
 * @constructor
 */
class Client(val session: Session, val player: Player) {

    /**
     * Whether the player client is in resizable mode or not.
     */
    var isResizable: Boolean = false
        internal set

    /**
     * The width in pixels of the client window.
     */
    var width: Int = 0
        internal set

    /**
     * The height in pixels of the client window.
     */
    var height: Int = 0
        internal set
}