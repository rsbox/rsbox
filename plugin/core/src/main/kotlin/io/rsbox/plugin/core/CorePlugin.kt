package io.rsbox.plugin.core

import org.pf4j.Plugin
import org.pf4j.PluginWrapper

class CorePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    override fun start() {
        /*
         * Initialize all components
         */
        InitialLoginHandler.init()
    }

    override fun stop() { }
}