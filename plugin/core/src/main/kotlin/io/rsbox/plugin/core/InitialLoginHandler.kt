package io.rsbox.plugin.core

import io.rsbox.engine.event.EventPriority
import io.rsbox.engine.event.impl.PlayerLoginEvent
import io.rsbox.engine.event.on_event
import io.rsbox.engine.model.`interface`.RootInterface

import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.net.packet.outbound.IfOpenTop
import io.rsbox.engine.net.packet.outbound.RebuildRegionNormal

object InitialLoginHandler {

    fun init() {
        /*
         * Listen for the initial player login.
         */
        on_event<PlayerLoginEvent>(priority = EventPriority.HIGHEST) { event ->
            this.onInitialPlayerLogin(event.player)
        }
    }

    private fun onInitialPlayerLogin(player: Player) {
        /*
         * Send the rebuild region and interface packets.
         */
        player.client.write(RebuildRegionNormal(player, gpi = true))

        /*
         * Send the root interface types.
         */
        player.interfaces.openTopInterface(player.client.displayMode)

        /*
         * Open each other root component interface.
         */
        RootInterface.values.filter { root -> root.interfaceId != -1 }.forEach { root ->
            if(root == RootInterface.XP_COUNTER || root == RootInterface.MINI_MAP) {
                return@forEach
            }
            player.interfaces.openInterface(root.interfaceId, root)
        }
    }
}