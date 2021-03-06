package io.rsbox.engine

import io.rsbox.engine.model.World
import io.rsbox.engine.service.ServiceManager
import org.koin.dsl.module

val EngineModule = module {
    single { Engine() }
    single { ServiceManager() }
    single { World() }
}