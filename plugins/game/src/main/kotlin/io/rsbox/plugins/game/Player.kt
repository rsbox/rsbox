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
     * Open the root interface for the player's current display mode.
     */
    this.interfaces.openTopInterface()

    /*
     * Open each other top interface type for the player.
     */
    TopInterfaceType.values.filter { it.interfaceId != -1 }.forEach { top ->
        if(top == TopInterfaceType.XP_COUNTER) {
            return@forEach
        } else if(top == TopInterfaceType.MINI_MAP) {
            return@forEach
        }

        this.interfaces.openInterface(top.interfaceId, top)
    }
}