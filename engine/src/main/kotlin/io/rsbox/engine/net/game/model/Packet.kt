package io.rsbox.engine.net.game.model

import io.rsbox.engine.net.Session

interface Packet {

    fun handle(session: Session) { throw UnsupportedOperationException() }

}