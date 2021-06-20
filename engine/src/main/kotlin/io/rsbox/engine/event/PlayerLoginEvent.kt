package io.rsbox.engine.event

import io.rsbox.engine.model.entity.Player
import io.rsbox.event.Event
import org.tinylog.kotlin.Logger

/**
 * Invoked when a player login request is accepted and is ready to be logged into the game.
 */
class PlayerLoginEvent(val player: Player) : Event(isCancellable = true) {

    override fun onCancel() {
        Logger.info("Login request [username: ${player.username}] has been terminated by a listener.")
    }

}