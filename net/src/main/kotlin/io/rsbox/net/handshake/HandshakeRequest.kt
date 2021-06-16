package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodec
import io.rsbox.net.js5.JS5Protocol

class HandshakeRequest(val type: HandshakeType, val revision: Int) : Message {

    private val rsboxConfig: RSBoxConfig by inject()

    override fun handle(session: Session) {
        val serverRevision = rsboxConfig.revision

        if(revision != serverRevision && revision != Int.MAX_VALUE) {
            session.writeAndClose(ServerResponseType.REVISION_MISMATCH)
            return
        }

        when(type){
            /*
             * When the handshake is signaling JS5 protocol
             */
            HandshakeType.JS5 -> {
                session.protocol = JS5Protocol(session)
                session.writeAndFlush(ServerResponseType.ACCEPTABLE)
            }

            /*
             * When the handshake is signaling LOGIN protocol
             */
            HandshakeType.LOGIN -> {
                println("Switch to LOGIN protocol")
                session.writeAndFlush(ServerResponseType.ACCEPTABLE)
            }
        }
    }

    companion object : MessageCodec<HandshakeRequest> {
        override fun decode(buf: ByteBuf): HandshakeRequest {
            val opcode = buf.readUnsignedByte().toInt()
            val handshakeType = HandshakeType.fromOpcode(opcode)

            val revision = when(handshakeType) {
                HandshakeType.JS5 -> buf.readInt()
                HandshakeType.LOGIN -> Int.MAX_VALUE
            }

            return HandshakeRequest(handshakeType, revision)
        }
    }
}