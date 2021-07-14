import io.rsbox.engine.event.impl.PlayerLoginEvent
import io.rsbox.engine.event.on_event
import io.rsbox.plugins.game.onLogin

on_enable {
    /*
     * Nothing to do
     */
}

on_disable {
    /*
     * Nothing to do.
     */
}

/*
 * Listen to player login event.
 */
on_event<PlayerLoginEvent> { event ->
    event.player.onLogin()
}