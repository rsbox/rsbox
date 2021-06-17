package io.rsbox.engine.event.impl

import io.rsbox.engine.Engine
import io.rsbox.engine.event.Event
import org.tinylog.kotlin.Logger

class EngineStartEvent(val engine: Engine) : Event(isCancellable = true) {

    override fun onCancel() {
        Logger.info("Engine startup cancelled. Shutting down engine.")
        engine.shutdown()
    }

}