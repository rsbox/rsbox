package io.rsbox.engine.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.ServerResponseType
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.core.Message
import io.rsbox.engine.net.core.MessageCodec
import io.rsbox.engine.net.core.MessageCodecRegistry
import io.rsbox.engine.net.core.Protocol

class HandshakeProtocol(override val session: Session) : Protocol {

    override val inbound = MessageCodecRegistry(this)
    override val outbound = MessageCodecRegistry(this)

    init {
        /*
         * Inbound
         */
        inbound[14] = HandshakeRequest.Login
        inbound[15] = HandshakeRequest.JS5


        /*
         * Outbound
         */
        outbound[-255] = ServerResponseType
    }

    override fun ingress(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        val opcode = buf.readUnsignedByte().toInt()
        val codec = inbound[opcode]
        val msg = codec.decode(session, buf)!!
        out.add(msg)
    }

    @Suppress("UNCHECKED_CAST")
    override fun egress(session: Session, msg: Message, out: ByteBuf) {
        val codec: MessageCodec<HandshakeRequest> = outbound[-255] as MessageCodec<HandshakeRequest>
        codec.encode(session, out, msg as HandshakeRequest)
    }
}