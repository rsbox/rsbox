package io.rsbox.cache

import org.koin.dsl.module

val CacheModule = module {
    single { GameCache() }
}