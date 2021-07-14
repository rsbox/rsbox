package io.rsbox.api

import io.rsbox.api.plugin.PluginManager
import org.koin.dsl.module

val ApiModule = module {
    single { PluginManager() }
}