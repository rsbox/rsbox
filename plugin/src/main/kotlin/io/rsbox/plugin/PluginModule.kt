package io.rsbox.plugin

import org.koin.dsl.module

val PluginModule = module {
    single { PluginManager() }
}