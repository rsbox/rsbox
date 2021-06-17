package io.rsbox.net.pipeline

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.rsbox.net.Session
import java.util.concurrent.atomic.AtomicReference

@ChannelHandler.Sharable
class GameChannelHandler : ChannelInboundHandlerAdapter() {

    private val session = AtomicReference<Session>(null)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val newSession = Session(ctx)
        if(!session.compareAndSet(null, newSession)) {
            return
        }

        newSession.onConnect()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) = session.get().close()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        if(msg == null) {
            return
        }
        session.get().receive(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = session.get().onError(cause)

}