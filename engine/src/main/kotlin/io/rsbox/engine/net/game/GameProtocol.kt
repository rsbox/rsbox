package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Protocol
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.Packet

class GameProtocol(override val session: Session) : Protocol {

    private val decoder = GamePacketDecoder(session)
    private val encoder = GamePacketEncoder(session)

    override fun decode(buf: ByteBuf, out: MutableList<Any>) = decoder.decode(buf, out)

    override fun encode(message: Message, out: ByteBuf) {
        if(message !is Packet) return
        encoder.encode(message, out)
    }

    override fun handle(session: Session, message: Message) {

    }

}