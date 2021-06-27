package io.rsbox.plugin

import org.pf4j.JarPluginManager
import org.tinylog.kotlin.Logger

class PluginManager {

    private val manager = JarPluginManager()

    fun startAll() {
        Logger.info("Loading all RSBox content plugins...")
        manager.loadPlugins()

        Logger.info("Starting all loaded plugins.")
        manager.startPlugins()
    }

    fun startPlugin(pluginId: String) {
        manager.startPlugin(pluginId)
    }

    fun stopPlugin(pluginId: String) {
        manager.stopPlugin(pluginId)
    }

    fun reloadPlugin(pluginId: String) {
        this.startPlugin(pluginId)
        this.stopPlugin(pluginId)
    }

}