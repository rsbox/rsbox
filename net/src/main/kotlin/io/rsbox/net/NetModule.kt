package io.rsbox.net

import org.koin.dsl.module

val NetModule = module {
    single { NetworkServer() }
}