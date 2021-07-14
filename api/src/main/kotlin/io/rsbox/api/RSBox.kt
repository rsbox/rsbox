package io.rsbox.api

import io.rsbox.api.plugin.PluginManager
import io.rsbox.common.di.inject
import io.rsbox.engine.Engine
import io.rsbox.engine.model.World

object RSBox {

    val pluginManager: PluginManager by inject()

    val engine: Engine by inject()

    val world: World by inject()

}