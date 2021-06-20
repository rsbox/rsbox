package io.rsbox.content.core

import io.rsbox.content.core.login.Login
import org.pf4j.Plugin
import org.pf4j.PluginWrapper

class CorePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    override fun start() {
        Login.init()
    }

    override fun stop() {
        println("Stopped core content plugin.")
    }
}