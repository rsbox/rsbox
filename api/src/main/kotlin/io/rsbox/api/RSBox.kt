package io.rsbox.api

import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.Engine
import io.rsbox.engine.model.world.World

object RSBox {

    /**
     * The RSBox configuration
     */
    val config: RSBoxConfig by inject()

    /**
     * The RSBox game engine.
     */
    val engine: Engine by inject()

    /**
     * The RSBox game world.
     */
    val world: World by inject()
}