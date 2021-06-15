package io.rsbox.launcher

import org.koin.dsl.module

val LauncherModule = module {
    single { Launcher() }
}