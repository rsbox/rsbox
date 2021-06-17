package io.rsbox.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.rsbox.net.Session

class GameChannelDecoder(private val session: Session) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) return
        session.protocol.ingress(session, buf, out)
    }

}