package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Protocol
import io.rsbox.engine.net.Session

class GameProtocol(override val session: Session) : Protocol {

    override fun decode(buf: ByteBuf, out: MutableList<Any>) {

    }

    override fun encode(message: Message, out: ByteBuf) {

    }

    override fun handle(session: Session, message: Message) {

    }
}