package io.rsbox.net.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.rsbox.net.Session
import io.rsbox.net.core.Message

class GameChannelEncoder(private val session: Session) : MessageToByteEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message, out: ByteBuf) {
        session.protocol.egress(session, msg, out)
    }

}