package io.rsbox.engine

import org.koin.dsl.module

val EngineModule = module {
    single { Engine() }
}