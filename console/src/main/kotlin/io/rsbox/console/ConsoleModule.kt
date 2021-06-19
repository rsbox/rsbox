package io.rsbox.console

import org.koin.dsl.module

val ConsoleModule = module {
    single { ConsoleApp() }
}