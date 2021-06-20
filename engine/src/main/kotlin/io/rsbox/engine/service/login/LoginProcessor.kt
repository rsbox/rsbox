package io.rsbox.engine.service.login

import io.rsbox.common.di.inject
import io.rsbox.common.hash.SHA256
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.module.PlayerSerializer
import io.rsbox.engine.net.ServerStatus
import io.rsbox.engine.net.login.LoginRequest
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

        if(username == player.username && password == player.password) {
            /*
             * Login successful.
             */
            Logger.info("Login request successful for [username: ${request.username}] with ip [address: ${request.session.remoteAddress}].")
            session.writeAndFlush(ServerStatus.SERVER_UPDATE)
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
}