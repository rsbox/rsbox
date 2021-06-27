package io.rsbox.engine

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.common.di.inject
import io.rsbox.engine.coroutine.GameCoroutineScope
import io.rsbox.engine.model.world.World
import io.rsbox.engine.module.XteaProvider
import io.rsbox.engine.net.NetworkServer
import io.rsbox.engine.service.ServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.tinylog.kotlin.Logger
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.system.measureTimeMillis

/**
 * Represents the core game engine of the server.
 */
class Engine  {

    private val serviceManager: ServiceManager by inject()
    private val world: World by inject()

    /**
     * Game engine executor thread.
     */
    private val gameExecutor = Executors.newSingleThreadExecutor(ThreadFactoryBuilder()
        .setNameFormat("game-thread")
        .setDaemon(false)
        .build()
    )

    /**
     * The game coroutine scope.
     */
    private val gameCoroutineScope = GameCoroutineScope(gameExecutor.asCoroutineDispatcher())

    var state: EngineState = EngineState.SHUTDOWN
        private set

    fun start() {
        if(state == EngineState.RUNNING) {
            throw IllegalStateException("Game engine is already running.")
        }

        Logger.info("Starting RSBox game engine...")

        state = EngineState.RUNNING

        /*
         * Load region xtea keys from file
         */
        XteaProvider.load(File("data/xteas/xteas.json"))

        /*
         * Start all engine services
         */
        serviceManager.startServices()

        /*
         * Start the game loop.
         */
        gameCoroutineScope.start(CYCLE_MILLIS)
    }

    fun shutdown() {
        if(state == EngineState.SHUTDOWN) {
            throw IllegalStateException("Game engine is not running and cannot be shutdown.")
        }

        Logger.info("Shutting down RSBox game engine...")

        state = EngineState.SHUTDOWN

        /*
         * Stop all engine services
         */
        serviceManager.stopServices()

        Logger.info("Engine has completed shutdown procedure successfully.")
    }

    private fun CoroutineScope.start(interval: Long) = launch {
        while(state != EngineState.SHUTDOWN) {
            /*
             * Execute the game cycle.
             */
            val elapsedTime = measureTimeMillis { cycle() }

            /*
             * Calculate the delta.
             */
            val remainingTime = interval - elapsedTime

            if(remainingTime < 0) {
                Logger.warn("Game cycle took ${abs(elapsedTime) + interval}ms / 600ms. Server is overloaded.")
            }
        }
    }

    private suspend fun preCycle() {
        world.players.forEach { player ->
            player.client.cycle()
        }
    }

    /**
     * Executes all of the logic for each cycle of the game engine.
     */
    private suspend fun cycle() {
        this.preCycle()

        world.cycle()

        this.postCycle()
    }

    private suspend fun postCycle() {
        world.players.forEach { player ->
            player.client.flush()
        }
    }

    companion object {

        /**
         * The number of milliseconds a game cycle is for the game engine.
         */
        private const val CYCLE_MILLIS = 600L
    }
}
