package io.rsbox.engine.event

import io.rsbox.engine.model.entity.Player
import io.rsbox.event.Event

class PlayerLoginEvent(val player: Player) : Event(isCancellable = true)