package io.rsbox.engine.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.core.Message

class GameChannelEncoder(private val session: Session) : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        session.protocol.egress(session, msg, out)
    }

}