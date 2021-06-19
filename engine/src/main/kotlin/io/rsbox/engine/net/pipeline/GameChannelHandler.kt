package io.rsbox.engine.net.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.rsbox.common.di.inject
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.NetworkServer
import io.rsbox.engine.net.Session
import java.util.concurrent.atomic.AtomicReference

class GameChannelHandler : ChannelInboundHandlerAdapter() {

    private val networkServer: NetworkServer by inject()

    private val session = AtomicReference<Session>(null)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val newSession = Session(ctx)
        if(!session.compareAndSet(null, newSession)) {
            return
        }

        networkServer.sessions.add(newSession)
        newSession.connect()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) = session.get().disconnect()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
         if(msg !is Message) return
        session.get().receive(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = session.get().error(cause)

}