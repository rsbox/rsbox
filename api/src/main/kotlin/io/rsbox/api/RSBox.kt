package io.rsbox.api

import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig

object RSBox {

    /**
     * The RSBox configuration
     */
    val config: RSBoxConfig by inject()
}