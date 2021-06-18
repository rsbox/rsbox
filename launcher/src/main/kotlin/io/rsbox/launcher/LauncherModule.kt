package io.rsbox.launcher

import io.rsbox.common.rsa.RSA
import org.koin.dsl.module
import java.io.File

val LauncherModule = module {
    single { Launcher() }
    single { RSA(File("data/rsa/")) }
}