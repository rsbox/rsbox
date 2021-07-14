package io.rsbox.api.plugin

import io.github.classgraph.ClassGraph
import org.tinylog.kotlin.Logger

class PluginManager {

    private val plugins = mutableListOf<PluginScript>()

    fun loadAllPlugins() {
        Logger.info("Loading all plugins...")

        val scan = ClassGraph().enableAllInfo().scan()
        val results = scan.getSubclasses(PluginScript::class.java.name)
        results.forEach { result ->
            val cls = result.loadClass(PluginScript::class.java)
            val inst = cls.getDeclaredConstructor().newInstance()
            plugins.add(inst)

            /*
             * If the plugin has enable logic,
             * run it.
             */
            if(inst.hasEnableAction) {
                inst.invokeEnable(inst)
            }
        }

        Logger.info("Loaded ${plugins.size} content plugin scripts.")
    }

}