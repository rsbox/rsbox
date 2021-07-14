package io.rsbox.engine.event.impl

import io.rsbox.engine.event.Event
import io.rsbox.engine.model.entity.Player

class PlayerLoginEvent(val player: Player) : Event(isCancellable = false)