package io.rsbox.launcher

import io.rsbox.common.rsa.RSA
import io.rsbox.launcher.plugin.PluginLoader
import io.rsbox.launcher.plugin.PluginManager
import org.koin.dsl.module
import java.io.File

val LauncherModule = module {
    single { Launcher() }
    single { RSA(File("data/rsa/")) }
    single { PluginManager() }
}