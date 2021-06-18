package io.rsbox.engine.net.core

import io.rsbox.engine.net.Session

/**
 * A message sent to or from the network socket.
 */
interface Message {

    fun handle(session: Session) { throw UnsupportedOperationException() }

}