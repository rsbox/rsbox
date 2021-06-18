package io.rsbox.engine.net

import org.koin.dsl.module

val NetModule = module {
    single { NetworkServer() }
}