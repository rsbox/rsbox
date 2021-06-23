package io.rsbox.engine.service.login

import io.rsbox.common.di.inject
import io.rsbox.common.hash.SHA256
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.event.PlayerLoginEvent
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.module.PlayerSerializer
import io.rsbox.engine.net.ServerStatus
import io.rsbox.engine.net.game.GameProtocol
import io.rsbox.engine.net.game.packet.outbound.LoadRegionNormal
import io.rsbox.engine.net.login.LoginRequest
import io.rsbox.engine.net.login.LoginResponse
import io.rsbox.event.fire_event
import org.tinylog.kotlin.Logger

object LoginProcessor {

    private val config: RSBoxConfig by inject()

    fun processLoginRequest(request: LoginRequest) {
        Logger.info("Received login request for [username: ${request.username}] with ip [address: ${request.session.remoteAddress}].")

        val session = request.session
        val username = request.username
        val password = SHA256.hash(request.password!!)

        if(!PlayerSerializer.hasSave(username)) {
            if(config.autoCreateNewPlayerSaves) {
                Logger.info("Player save for [username: $username] does not exist. Creating new player save as server config enables auto-registration.")
                PlayerSerializer.createNewPlayer(request)
            } else {
                session.writeAndFlush(ServerStatus.INVALID_CREDENTIALS)
                return
            }
        }

        /*
         * Load the player save
         */
        val player = PlayerSerializer.read(username)
        player.client.session = request.session

        if(username == player.username && password == player.password) {
            /*
             * Login successful
             */
            player.client.session.xteas = request.xteas
            player.login()
        } else {
            /*
             * Login failed due to invalid credentials.
             */
            Logger.warn("Login request failed for [username: ${request.username}] with ip [address: ${request.session.remoteAddress}] with error [INVALID_CREDENTIALS].")
            session.writeAndFlush(ServerStatus.INVALID_CREDENTIALS)
            return
        }
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") { String.format("%02X", it) }
    }

    private fun Player.login() {
        Logger.info("Login request successful for [username: $username] with ip [address: ${client.session.remoteAddress}]")

        val session = this.client.session

        /*
         * Set the isaac random number generators.
         */
        session.decodeIsaac.init(session.xteas)
        session.encodeIsaac.init(IntArray(session.xteas.size) { session.xteas[it] + 50 })

        /*
         * Check if the player is already online or not and register with the game world.
         */
        if(this.isOnline()) {
            session.writeAndClose(ServerStatus.ALREADY_ONLINE)
            return
        }

        val registered = world.players.add(this)
        if(!registered) {
            session.writeAndClose(ServerStatus.WORLD_FULL)
            return
        }

        val response = LoginResponse(this)
        session.writeAndFlush(response)
    }
}