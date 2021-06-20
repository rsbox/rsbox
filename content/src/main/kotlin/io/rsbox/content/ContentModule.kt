package io.rsbox.content

import org.koin.dsl.module

val ContentModule = module {
    single { ContentManager() }
}