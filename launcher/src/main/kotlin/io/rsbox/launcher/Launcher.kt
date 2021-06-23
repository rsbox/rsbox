package io.rsbox.launcher

import io.rsbox.cache.CacheModule
import io.rsbox.cache.GameCache
import io.rsbox.common.CommonModule
import io.rsbox.common.di.get
import io.rsbox.common.di.inject
import io.rsbox.common.rsa.RSA
import io.rsbox.config.ConfigModule
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.Engine
import io.rsbox.engine.EngineModule
import io.rsbox.engine.net.NetworkModule
import io.rsbox.event.EventBus
import io.rsbox.launcher.plugin.PluginLoader
import org.koin.core.context.startKoin
import org.tinylog.kotlin.Logger
import java.io.File

class Launcher {

    private val rsboxConfig: RSBoxConfig by inject()
    private val gameCache: GameCache by inject()
    private val engine: Engine by inject()
    private val rsa: RSA by inject()

    fun launch() {
        /*
         * Launch RSBox
         */
        Logger.info("Launching RSBox server...")

        /*
         * Check directories.
         */
        this.initDirs()

        /*
         * Load configs
         */
        this.loadConfigs()

        /*
         * Init game cache files.
         */
        this.initGameCache()

        /*
         * Init RSA key files
         */
        this.initRSA()

        /*
         * Register event listeners
         */
        EventBus.register()

        /*
         * Init plugin manager
         */
        PluginLoader.init()

        /*
         * Start the game engine.
         */
        engine.start()
    }

    private fun initDirs() {
        Logger.info("Checking directories.")

        listOf(
            "data/",
            "data/rsa/",
            "data/saves/",
            "data/cache/",
            "data/logs/",
            "data/xteas/",
            "data/configs/",
            "data/content/"
        ).map { File(it) }.forEach { dir ->
            if(!dir.exists()) {
                Logger.info("Creating missing directory: ${dir.path}")
                dir.mkdirs()
            }
        }
    }

    private fun loadConfigs() {
        Logger.info("Loading configuration files.")

        /*
         * Load server settings config.
         */
        rsboxConfig.load()

        /*
         * Verify that RSBox configuration is working.
         */
        Logger.info("RSBox running revision: ${rsboxConfig.revision}")
    }

    private fun initGameCache() {
        Logger.info("Preparing to load game cache files...")
        gameCache.open(File("data/cache/"))
    }

    private fun initRSA() {
        Logger.info("Loading RSA public and private keys.")
        rsa.init()
    }

    companion object {

        private fun initDi() {
            startKoin {
                modules(
                    LauncherModule,
                    CacheModule,
                    ConfigModule,
                    EngineModule,
                    NetworkModule,
                    CommonModule
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