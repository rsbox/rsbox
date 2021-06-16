package io.rsbox.net.core

import io.rsbox.net.Session

/**
 * A message sent to or from the network socket.
 */
interface Message {

    fun handle(session: Session) { throw UnsupportedOperationException() }

}