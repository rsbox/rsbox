package io.rsbox.plugins.game

import io.rsbox.engine.model.`interface`.TopInterfaceType
import io.rsbox.engine.model.entity.Player

/**
 * Invoked when the player has a successful login to the world.
 *
 * @receiver Player
 */
fun Player.onLogin() {
   /*
    * Open the root interface for the current display mode of
    * the player.
    */
    this.interfaces.openTopInterface(this.client.displayMode)

    /*
     * Open each top / root component interface for the player.
     */
    TopInterfaceType.values.filter { it.interfaceId != -1 }.forEach { top ->
        this.interfaces.openInterface(top.interfaceId, top)
    }
}