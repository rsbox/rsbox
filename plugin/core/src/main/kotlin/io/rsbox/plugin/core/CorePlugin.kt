package io.rsbox.plugin.core

import org.pf4j.Plugin
import org.pf4j.PluginWrapper
import org.tinylog.kotlin.Logger

class CorePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    override fun start() {
        Logger.info("starting core plugin")
    }

    override fun stop() {
        Logger.info("stopping core plugin")
    }
}