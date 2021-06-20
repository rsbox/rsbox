package io.rsbox.engine.module

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.json.toJson
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.model.Tile
import io.rsbox.engine.model.entity.Client
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.net.login.LoginRequest
import org.tinylog.kotlin.Logger
import java.io.File
import java.nio.file.Paths
import java.security.MessageDigest

object PlayerSerializer {

    private val config: RSBoxConfig by inject()

    private val playerSaveDirectory = Paths.get("data/saves/")

    private val digest = MessageDigest.getInstance("SHA-256")
    private val defaultHomeTile = Tile(config.homeLocationX, config.homeLocationY, config.homeLocationLevel)

    /**
     * Creates a new player from a login request.
     *
     * @param request LoginRequest
     * @return Player
     */
    fun createNewPlayer(request: LoginRequest): Player {
        Logger.info("Creating new player save for [username: ${request.username.sanitize()}].")

        val client = Client()
        val player = Player(client)

        client.player = player
        player.username = request.username.sanitize()
        player.password = request.password!!.sha256()
        player.displayName = request.username.sanitize()
        player.homeTile = defaultHomeTile
        player.tile = defaultHomeTile

        /*
         * Create the player save file.
         */
        val playerSaveFileName = player.username.replace(" ", "_") + ".json"
        val playerJsonFile = playerSaveDirectory.resolve(playerSaveFileName)
        write(playerJsonFile.toFile(), player)

        return player
    }

    fun write(file: File, player: Player) {
        val config = Config { addSpec(PlayerSpec) }

        config[PlayerSpec.username] = player.username
        config[PlayerSpec.password] = player.password
        config[PlayerSpec.displayName] = player.displayName
        config[PlayerSpec.HomeTile.x] = player.homeTile.position.x
        config[PlayerSpec.HomeTile.y] = player.homeTile.position.y
        config[PlayerSpec.HomeTile.level] = player.homeTile.position.level
        config[PlayerSpec.Tile.x] = player.tile.position.x
        config[PlayerSpec.Tile.y] = player.tile.position.y
        config[PlayerSpec.Tile.level] = player.tile.position.level

        config.toJson.toFile(file)
    }

    fun read(username: String): Player {
        val saveFileName = username.replace(" ","_") + ".json"
        val file = playerSaveDirectory.resolve(saveFileName).toFile()
        val config = Config { addSpec(PlayerSpec) }
            .from
            .json
            .file(file)

        val client = Client()
        val player = Player(client)
        client.player = player
        player.username = config[PlayerSpec.username]
        player.password = config[PlayerSpec.password]
        player.displayName = config[PlayerSpec.displayName]
        player.tile = Tile(config[PlayerSpec.Tile.x], config[PlayerSpec.Tile.y], config[PlayerSpec.Tile.level])
        player.homeTile = Tile(config[PlayerSpec.HomeTile.x], config[PlayerSpec.HomeTile.y], config[PlayerSpec.HomeTile.level])

        return player
    }

    fun hasSave(player: Player): Boolean {
        val saveFileName = player.username.sanitize() + ".json"
        val saveFile = playerSaveDirectory.resolve(saveFileName)
        return saveFile.toFile().exists()
    }

    private fun String.sanitize(): String {
        var result = this.substring(12)
        val regex = Regex("[^A-Za-z0-9 ]")
        result = regex.replace(result, "")
        return result
    }

    private fun String.sha256(): String {
        val encodedHash = digest.digest(this.toByteArray(Charsets.UTF_8))
        return encodedHash.toHexString()
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString { "%02x".format(it) }
    }

    private object PlayerSpec : ConfigSpec("player") {
        val username by required<String>("username")
        val password by required<String>("password")
        val displayName by required<String>("display-name")

        object Tile : ConfigSpec("tile") {
            val x by required<Int>("x")
            val y by required<Int>("y")
            val level by required<Int>("level")
        }

        object HomeTile : ConfigSpec("home") {
            val x by required<Int>("x")
            val y by required<Int>("y")
            val level by required<Int>("level")
        }
    }
}