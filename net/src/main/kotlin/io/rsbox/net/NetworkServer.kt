package io.rsbox.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.net.pipeline.GameChannelInitializer
import org.tinylog.kotlin.Logger
import java.net.InetSocketAddress
import kotlin.system.exitProcess

/**
 * The networking server component which deals with the TCP input/output for the RSBox private server.
 */
class NetworkServer {

    private val rsboxConfig: RSBoxConfig by inject()

    private val bootstrap = ServerBootstrap()
    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup(2)
    private val channelInitializer = GameChannelInitializer()

    init {
        /*
         * Setup the server bootstrap
         */
        bootstrap
            .group(bossGroup, workerGroup)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(channelInitializer)
    }

    /**
     * Starts the game networking server.
     */
    fun start() {
        Logger.info("Preparing game networking server.")

        /**
         * The address that we should attempt to bind the networking socket to.
         * This is read from the configured values in rsbox.yml configuration file.
         */
        val bindAddress = InetSocketAddress(rsboxConfig.listenAddress, rsboxConfig.listenPort)

        this.bind(bindAddress).addListener { result ->
            if(result.isSuccess) {
                this.onBindSuccess(bindAddress)
            } else {
                this.onBindFailure(bindAddress, result.cause())
            }
        }
    }

    /**
     * Shutsdown the game networking server.
     */
    fun shutdown() {
        Logger.info("Shutting down game networking server.")

        /*
         * Terminate the boss and worker threads
         */
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    /**
     * Attempts to bind the networking server to a given address.
     *
     * @param address InetSocketAddress
     */
    private fun bind(address: InetSocketAddress): ChannelFuture {
        return bootstrap.bind(address.address, address.port)
    }

    private fun onBindSuccess(address: InetSocketAddress) {
        Logger.info("Listening for incoming connections on ${address.hostString}:${address.port}...")
    }

    private fun onBindFailure(address: InetSocketAddress, cause: Throwable) {
        Logger.error("Failed to bind network server to ${address.hostString}:${address.port}. Terminating process.")
        cause.printStackTrace()
        exitProcess(0)
    }
}