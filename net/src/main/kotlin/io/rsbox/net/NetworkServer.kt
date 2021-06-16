package io.rsbox.net

import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.vertx.core.net.NetServer
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.servicediscovery.serviceDiscoveryOptionsOf
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.MessageSource
import org.tinylog.kotlin.Logger
import java.net.InetSocketAddress

/**
 * The networking server component which deals with the TCP input/output for the RSBox private server.
 *
 * @property server NetServer
 */
class NetworkServer : CoroutineVerticle() {

    private val rsboxConfig: RSBoxConfig by inject()

    lateinit var server: NetServer
        private set

    val id: String get() = deploymentID

    val sessions = mutableSetOf<NetworkSession>()

    val discovery by lazy {
        ServiceDiscovery.create(vertx, serviceDiscoveryOptionsOf(name = "network-server"))
    }

    override suspend fun start() {
        this.server = vertx.createNetServer()
            .connectHandler { socket ->
                socket.writeHandlerID()
                val session = NetworkSession(vertx, this, socket)
                Logger.info("Connection established from remote: ${socket.remoteAddress()}.")

                val eventSource =
                    MessageSource.createRecord("network-session-source", "network.event.${socket.writeHandlerID()}")

                socket.handler(session::receive)
                    .closeHandler {
                        Logger.info("Connection closed from remote: ${socket.remoteAddress()}")
                        session.close()
                        this.sessions -= session
                    }
                    .exceptionHandler {
                        it.printStackTrace()
                        socket.close()
                    }

                this.sessions += session
            }

        this.server.listen(rsboxConfig.listenPort, rsboxConfig.listenAddress)
        Logger.info("Listening for client connections on ${rsboxConfig.listenAddress}:${rsboxConfig.listenPort}")
    }

    override suspend fun stop() {
        this.server.close()
        this.sessions.clear()
    }
}