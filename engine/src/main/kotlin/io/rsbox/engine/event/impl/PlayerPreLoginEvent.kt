package io.rsbox.engine.event.impl

import io.rsbox.engine.event.Event
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.net.ServerStatus

class PlayerPreLoginEvent(val player: Player) : Event(isCancellable = true) {

    var errorStatus: ServerStatus = ServerStatus.ACCEPTABLE

    override fun onCancel() {
        player.client.session.writeAndFlush(errorStatus)
    }
}