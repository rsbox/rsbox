package io.rsbox.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Item
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import org.tinylog.kotlin.Logger
import java.io.File

class RSBoxConfig {

    private val file = File("data/rsbox.yml")
    private var config = Config { addSpec(Companion) }

    fun load() {
        Logger.info("Loading RSBox configurations from: 'data/rsbox.yml'.")

        if(!file.exists()) {
            Logger.warn("No configuration file found at 'data/rsbox.yml'. Creating default configuration file.")
            this.save()
        }

        config = Config { addSpec(Companion) }
            .from
            .yaml
            .file(file)
    }

    fun save() {
        Logger.info("Saving RSBox configurations to: 'data/rsbox.yml'.")
        config.toYaml.toFile(file)
    }

    operator fun <T> get(item: Item<T>): T = config[item]

    operator fun <T> set(item: Item<T>, value: T) = config.set(item, value)

    /**
     * Configuration value properties.
     */

    val serverName get() = this[Companion.server_name]
    val revision get() = this[Companion.revision]
    val developmentMode get() = this[Companion.dev_mode]
    val autoCreateNewPlayerSaves get() = this[Companion.auto_create_new_player_saves]
    val listenAddress get() = this[Companion.net_listen_address]
    val listenPort get() = this[Companion.net_listen_port]
    val homeLocationX get() = this[Companion.loc_home_x]
    val homeLocationY get() = this[Companion.loc_home_y]
    val homeLocationLevel get() = this[Companion.loc_home_level]

    companion object : ConfigSpec("rsbox") {
        /**
         * General Options
         */

        val server_name by optional("RSBox Private Server", "server-name")
        val revision by optional(196, "revision")
        val dev_mode by optional(true, "development-mode")
        val auto_create_new_player_saves by optional(true, "auto-create-new-player-saves")

        /**
         * Networking
         */
        val net_listen_address by optional("0.0.0.0", "network.listen-address")
        val net_listen_port by optional(43594, "network.listen-port")

        /**
         * Default locations
         */
        val loc_home_x by optional(3218, "locations.home.x")
        val loc_home_y by optional(3218, "locations.home.y")
        val loc_home_level by optional(0, "locations.home.level")
    }
}