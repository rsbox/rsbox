package io.rsbox.engine.event

import io.rsbox.engine.Engine
import io.rsbox.event.Event

class EngineShutdownEvent(val engine: Engine) : Event(isCancellable = false)