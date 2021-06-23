package io.rsbox.engine.event

import io.rsbox.engine.Engine
import io.rsbox.event.Event

class EngineStartEvent(val engine: Engine) : Event(isCancellable = false)