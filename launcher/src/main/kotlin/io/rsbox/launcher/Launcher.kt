package io.rsbox.launcher

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.rsbox.cache.CacheModule
import io.rsbox.common.di.get
import io.rsbox.common.di.inject
import io.rsbox.config.ConfigModule
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.Engine
import io.rsbox.engine.EngineModule
import io.rsbox.net.NetModule
import org.koin.core.context.startKoin
import org.tinylog.kotlin.Logger
import java.io.File

class Launcher {

    private val rsboxConfig: RSBoxConfig by inject()
    private val cacheStore: Js5DiskStore by inject()
    private val cache: Js5Cache by inject()
    private val engine: Engine by inject()

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
        val cacheDir = File("data/cache/")

        if(cacheDir.listFiles()!!.isEmpty()) {
            throw IllegalStateException("No game cache files found. Copy OSRS cache files to 'data/cache/'.")
        }

        Logger.info("Found ${cacheStore.archiveCount} game cache archives.")

        /*
         * Validate the game cache.
         */
        Logger.info("Validating game cache files.")

        val validator = cache.generateValidator(
            includeWhirlpool = false,
            includeSizes = false
        )

        val container = Js5Container(validator.encode())
        cacheStore.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, data = container.encode())
    }

    companion object {

        private fun initDi() {
            startKoin {
                modules(
                    LauncherModule,
                    CacheModule,
                    ConfigModule,
                    EngineModule,
                    NetModule
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