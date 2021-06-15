package io.rsbox.launcher

import io.rsbox.common.di.get
import org.koin.core.context.startKoin
import org.tinylog.kotlin.Logger

class Launcher {

    fun launch() {
        Logger.info("Launching RSBox server...")

    }

    companion object {

        private fun initDi() {
            startKoin {
                modules(
                    LauncherModule
                )
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Logger.info("Initializing...")

            /*
             * Start the dependency injector.
             */
            this.initDi()

            /*
             * Launch
             */
            get<Launcher>().launch()
        }
    }
}