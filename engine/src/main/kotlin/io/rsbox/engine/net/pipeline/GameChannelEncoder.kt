package io.rsbox.engine.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Session

@ChannelHandler.Sharable
class GameChannelEncoder(private val session: Session) : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        session.protocol.get().encode(msg, out)
    }

}