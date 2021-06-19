package io.rsbox.engine.net.pipeline

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler

class GameChannelInitializer : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        val timeoutHandler = IdleStateHandler(TIMEOUT_SECONDS, TIMEOUT_SECONDS, 0)

        val channelHandler = GameChannelHandler()

        /*
         * Build the pipeline
         */
        val p = ch.pipeline()

        p.addLast("timeout", timeoutHandler)
        p.addLast("handler", channelHandler)
    }

    companion object {
        private const val TIMEOUT_SECONDS = 30
    }
}