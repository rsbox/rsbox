package io.rsbox.net.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodec

class JS5Request(val archive: Int, val group: Int, val priority: Boolean) : Message {

    override fun handle(session: Session) {
        JS5Processor.handle(session, this)
    }

    companion object : MessageCodec<JS5Request> {

        private const val REQUEST_PRIORITY = 0
        private const val REQUEST_NORMAL = 1
        private const val GAME_INIT = 2
        private const val GAME_LOADING = 3
        private const val GAME_READY = 6

        override fun decode(buf: ByteBuf): JS5Request? {
            buf.markReaderIndex()

            when(val requestType = buf.readByte().toInt()) {
                GAME_INIT, GAME_LOADING, GAME_READY -> {
                    buf.skipBytes(3)
                }

                REQUEST_NORMAL, REQUEST_PRIORITY -> {
                    if(buf.readableBytes() >= 3) {
                        val archive = buf.readUnsignedByte().toInt()
                        val group = buf.readUnsignedShort()
                        val priority = (requestType == REQUEST_PRIORITY)
                        return JS5Request(archive, group, priority)
                    } else {
                        buf.resetReaderIndex()
                    }
                }
            }

            return null
        }
    }
}