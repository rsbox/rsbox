package io.rsbox.config

import org.koin.dsl.module

val ConfigModule = module {
    single { RSBoxConfig() }
}