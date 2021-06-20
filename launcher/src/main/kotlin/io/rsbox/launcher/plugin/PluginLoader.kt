package io.rsbox.launcher.plugin

import io.rsbox.common.di.inject
import io.rsbox.engine.event.EngineStartupEvent
import io.rsbox.event.on_event
import org.tinylog.kotlin.Logger

object PluginLoader {

    private val pluginManager: PluginManager by inject()

    fun init() {
        Logger.info("Prepared plugin and extension loaders.")

        /*
         * Listen for the engine start to finish.
         */
        on_event<EngineStartupEvent> {
            Logger.info("Engine startup complete. Preparing to load content plugins.")

            /*
             * Load plugin from the plugin manager.
             */
            pluginManager.loadPlugins()

            Logger.info("Preparing to start all content plugins.")
            pluginManager.startPlugins()
            Logger.info("Successfully started ${pluginManager.startedPlugins.size} content plugins.")
        }
    }
}