package io.rsbox.engine.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.engine.net.*
import io.rsbox.engine.net.js5.JS5Protocol

class HandshakeProtocol(override val session: Session) : Protocol {

    private val config: RSBoxConfig by inject()

    private val serverRevision = config.revision

    override fun decode(buf: ByteBuf, out: MutableList<Any>) {
        val opcode = buf.readUnsignedByte().toInt()
        when(HandshakeType.fromOpcode(opcode)) {
            HandshakeType.JS5 -> {
                val revision = buf.readInt()
                if(serverRevision != revision) {
                    session.writeAndClose(ServerStatus.REVISION_MISMATCH)
                    return
                }

                out.add(HandshakeRequest.JS5())
            }

            HandshakeType.LOGIN -> out.add(HandshakeRequest.Login())
        }
    }

    override fun encode(message: Message, out: ByteBuf) {
        if(message !is ServerStatus) return
        out.writeByte(message.id)
    }

    override fun handle(session: Session, message: Message) {
        if(message !is HandshakeRequest) return
        when(message) {
            is HandshakeRequest.JS5 -> {
                session.writeAndFlush(ServerStatus.ACCEPTABLE)
                session.protocol.set(JS5Protocol(session))
            }
        }
    }
}