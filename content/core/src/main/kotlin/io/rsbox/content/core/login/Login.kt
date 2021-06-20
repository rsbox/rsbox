package io.rsbox.content.core.login

import io.rsbox.engine.event.PlayerLoginEvent
import io.rsbox.engine.net.ServerStatus
import io.rsbox.event.EventPriority
import io.rsbox.event.on_event
import org.tinylog.kotlin.Logger

object Login {
    fun init() {

        /**
         * Lets ban all players
         */
        on_event<PlayerLoginEvent>(EventPriority.HIGH) { event ->
            event.cancel()

            Logger.info("Login request [username: ${event.player.username}] rejected with cause: [ACCOUNT_BANNED].")

            /*
             * Respond back to the player.
             */
            event.player.client.session.writeAndClose(ServerStatus.ACCOUNT_BANNED)
        }

    }
}