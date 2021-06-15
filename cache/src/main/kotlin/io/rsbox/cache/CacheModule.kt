package io.rsbox.cache

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Paths

val CacheModule = module {
    single { Js5DiskStore.open(Paths.get("data/cache/")) } bind Js5Store::class
    single { Js5Cache(get()) }
}