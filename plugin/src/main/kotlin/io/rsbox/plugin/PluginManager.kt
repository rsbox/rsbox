package io.rsbox.plugin

import io.github.classgraph.ClassGraph
import org.pf4j.DefaultPluginManager
import org.tinylog.kotlin.Logger
import java.nio.file.Files

class PluginManager {

    private val manager = DefaultPluginManager()
    private val pluginScripts = mutableListOf<RsboxPluginScript>()

    fun loadAllPlugins() {
        Logger.info("Loading all RSBox content plugins...")

        manager.loadPlugins()
        manager.startPlugins()

        /*
         * Run all scripts for each RSbox plugin.
         */
        this.loadPluginScripts()

        Logger.info("Successfully loaded ${manager.plugins.size} RSBox plugins.")
    }

    private fun loadPluginScripts() {
        val scan = ClassGraph()
            .enableAllInfo()
            .scan()
            .getSubclasses(RsboxPluginScript::class.java.name)

        scan.forEach { classInfo ->
            val klass = classInfo.loadClass(RsboxPluginScript::class.java)
            val inst = klass.getDeclaredConstructor().newInstance()
            pluginScripts.add(inst)
        }
    }
}