package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

class GamePacketDecoder(private val session: Session) {

    fun decode(buf: ByteBuf, out: MutableList<Any>) {

    }

}