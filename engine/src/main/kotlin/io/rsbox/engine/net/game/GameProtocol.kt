package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Protocol
import io.rsbox.engine.net.Session

class GameProtocol(override val session: Session) : Protocol {

    private val encoder = GamePacketEncoder(session)
    private val decoder = GamePacketDecoder(session)

    override fun decode(buf: ByteBuf, out: MutableList<Any>) = decoder.decode(buf, out)

    override fun encode(message: Message, out: ByteBuf) {
        if(message !is Packet) return
        encoder.encode(message, out)
    }

    override fun handle(session: Session, message: Message) {
        if(message !is Packet) return
        message.handle(session)
    }
}