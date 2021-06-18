package io.rsbox.engine

import io.rsbox.engine.net.service.ServiceManager
import org.koin.dsl.module

val EngineModule = module {
    single { Engine() }
    single { ServiceManager() }
}