package io.rsbox.net

import io.vertx.core.net.NetServer
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * The networking server component which deals with the TCP input/output for the RSBox private server.
 *
 * @property server NetServer
 */
class NetworkServer : CoroutineVerticle() {

    lateinit var server: NetServer
        private set

    override suspend fun start() {

    }

    override suspend fun stop() {

    }
}