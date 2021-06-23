package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session

class GamePacketEncoder(private val session: Session) {

    fun encode(packet: Packet, out: ByteBuf) {
        val opcode = PacketRegistry.outbound.getOpcode(packet::class)
        val type = PacketRegistry.outbound.getPacketType(opcode)
        val codec = PacketRegistry.outbound.getCodec(opcode)

        val payload = session.buffer()
        codec.encode(session, packet, payload)

        val length = payload.readableBytes()

        /*
         * Write the packet opcode with Isaac random applied.
         */
        out.writeByte((opcode + session.encodeIsaac.opcodeModifier()) and 0xFF)

        /*
         * If the packet is variable short or variable byte, write the payload length.
         */
        when(type) {
            PacketType.VARIABLE_BYTE -> out.writeByte(length)
            PacketType.VARIABLE_SHORT -> out.writeShort(length)
            else -> {}
        }

        out.writeBytes(payload)
    }

}