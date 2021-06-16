package io.rsbox.engine

import io.rsbox.engine.coroutine.GameCoroutineScope
import org.koin.dsl.module

val EngineModule = module {
    single { Engine() }
}