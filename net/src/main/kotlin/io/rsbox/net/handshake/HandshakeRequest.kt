package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.net.NetworkPacket
import io.rsbox.net.NetworkSession
import io.rsbox.net.core.PacketCodec
import org.tinylog.kotlin.Logger

data class HandshakeRequest(
    val type: HandshakeType,
    val revision: Int
) : NetworkPacket.Handshake() {

    override fun handle(session: NetworkSession) {
        Logger.info("Got handshake: $type")
    }

    companion object : PacketCodec<HandshakeRequest> {

        override fun decode(buf: ByteBuf): HandshakeRequest {
            buf.resetReaderIndex()

            val opcode = buf.readUnsignedByte().toInt()
            val revision = buf.readInt()
            val handshakeType = HandshakeType.fromId(opcode)

            return HandshakeRequest(handshakeType, revision)
        }
    }
}