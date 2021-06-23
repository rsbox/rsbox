package io.rsbox.engine.net.game

import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Session

interface Packet : Message {

    fun handle(session: Session) {
        throw UnsupportedOperationException()
    }

}