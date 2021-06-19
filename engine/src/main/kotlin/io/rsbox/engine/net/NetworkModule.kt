package io.rsbox.engine.net

import org.koin.dsl.module

val NetworkModule = module {
    single { NetworkServer() }
}