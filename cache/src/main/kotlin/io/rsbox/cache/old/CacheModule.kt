package io.rsbox.cache.old

import org.koin.dsl.module

val CacheModule = module {
    single { GameCache() }
}