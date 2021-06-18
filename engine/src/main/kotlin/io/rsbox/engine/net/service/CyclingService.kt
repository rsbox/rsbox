package io.rsbox.engine.net.service

interface CyclingService : Service {

    /**
     * Executed on each game tick.
     */
    fun tick()
}