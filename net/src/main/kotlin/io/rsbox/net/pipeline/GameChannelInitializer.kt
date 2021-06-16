package io.rsbox.net.pipeline

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import java.util.concurrent.Executors

class GameChannelInitializer : ChannelInitializer<SocketChannel>() {

    private val globalTrafficShaper = GlobalTrafficShapingHandler(
        Executors.newSingleThreadScheduledExecutor(
            ThreadFactoryBuilder()
                .setNameFormat("global-traffic")
                .setDaemon(false)
                .build()
        ),
        GLOBAL_TRAFFIC_WRITE_LIMIT,
        GLOBAL_TRAFFIC_READ_LIMIT,
        1000
    )

    override fun initChannel(ch: SocketChannel) {
        val channelTrafficShaper = ChannelTrafficShapingHandler(
            CHANNEL_TRAFFIC_WRITE_LIMIT,
            CHANNEL_TRAFFIC_READ_LIMIT,
            1000
        )

        val timeoutHandler = IdleStateHandler(TIMEOUT_SECONDS, TIMEOUT_SECONDS, 0)
        val handler = GameChannelHandler()

        /*
         * Build the initial channel pipeline
         */
        val p = ch.pipeline()

        p.addLast("global_traffic", globalTrafficShaper)
        p.addLast("channel_traffic", channelTrafficShaper)
        p.addLast("timeout", timeoutHandler)
        p.addLast("handler", handler)
    }

    companion object {

        private const val GLOBAL_TRAFFIC_WRITE_LIMIT = 0L
        private const val GLOBAL_TRAFFIC_READ_LIMIT = 0L
        private const val CHANNEL_TRAFFIC_WRITE_LIMIT = 0L
        private const val CHANNEL_TRAFFIC_READ_LIMIT = 0L
        private const val TIMEOUT_SECONDS = 30
    }
}