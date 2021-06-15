package io.rsbox.engine

import io.rsbox.common.di.inject
import io.rsbox.net.NetworkServer
import io.vertx.core.Vertx
import org.tinylog.kotlin.Logger

/**
 * Represents the core game engine of the server.
 */
class Engine  {

    private val networkServer: NetworkServer by inject()

    /**
     * The Vert.x singleton instance used for deploying vertex clusters
     */
    private val vertx = Vertx.vertx()

    var state: EngineState = EngineState.SHUTDOWN
        private set

    fun start() {
        if(state == EngineState.RUNNING) {
            throw IllegalStateException("Game engine is already running.")
        }

        Logger.info("Starting RSBox game engine...")
        state = EngineState.RUNNING

        /*
         * Initialize vert.x clusters
         */
        this.initVertx()
    }

    fun shutdown() {
        if(state == EngineState.SHUTDOWN) {
            throw IllegalStateException("Game engine is not running and cannot be shutdown.")
        }

        Logger.info("Shutting down RSBox game engine...")
        state = EngineState.SHUTDOWN
    }

    private fun initVertx() {
        Logger.info("Preparing vertx cluster manager.")

        /*
         * Start the network server vertx cluster.
         */
        vertx.deployVerticle(networkServer)
    }
}