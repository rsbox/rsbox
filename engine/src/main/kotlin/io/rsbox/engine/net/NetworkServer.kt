package io.rsbox.engine.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.net.pipeline.GameChannelInitializer
import org.tinylog.kotlin.Logger
import java.net.InetSocketAddress
import kotlin.system.exitProcess

class NetworkServer {

    private val config: RSBoxConfig by inject()

    private val bootstrap = ServerBootstrap()
    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup(2)
    private val channelInitializer = GameChannelInitializer()

    val sessions = mutableListOf<Session>()

    init {
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(channelInitializer)
    }

    fun start() {
        Logger.info("Starting engine networking server...")

        /*
         * Bind the network server to the configured listen address and port.
         */
        val address = InetSocketAddress(config.listenAddress, config.listenPort)
        this.bind(address)
    }

    fun shutdown() {
        Logger.info("Shutting down engine networking server...")
    }

    private fun bind(address: InetSocketAddress) {
        bootstrap.bind(address).addListener { result ->
            if(result.isSuccess) {
                this.onBindSuccess(address)
            } else {
                this.onBindFailure(address, result.cause())
            }
        }
    }

    private fun onBindSuccess(address: InetSocketAddress) {
        Logger.info("Listening for incoming connections on ${address.hostString}:${address.port}...")
    }

    private fun onBindFailure(address: InetSocketAddress, cause: Throwable) {
        Logger.error(cause) { "Failed to bind socket to ${address.hostString}:${address.port}" }
        exitProcess(0)
    }
}