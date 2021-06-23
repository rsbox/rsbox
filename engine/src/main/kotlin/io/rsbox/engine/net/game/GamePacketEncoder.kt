package io.rsbox.engine.net.game

import io.netty.buffer.ByteBuf
import io.rsbox.engine.net.Session
import io.rsbox.engine.net.game.model.Packet
import io.rsbox.engine.net.game.model.PacketRegistry
import io.rsbox.engine.net.game.model.PacketType

class GamePacketEncoder(private val session: Session) {

    private val isaac get() = session.encodeIsaac

    fun encode(packet: Packet, out: ByteBuf) {
        val opcode = PacketRegistry.outbound.getOpcode(packet::class)
        val type = PacketRegistry.outbound.getPacketType(opcode)
        val codec = PacketRegistry.outbound.getCodec(opcode)

        var headerLength = 0

        out.writeByte((opcode + isaac.nextInt()) and 0xFF).apply { headerLength += Byte.SIZE_BYTES }

        /*
         * Write the length as zero for now, after writing the payload
         * we know the variable length of the packet.
         */
        when(type) {
            PacketType.VARIABLE_BYTE -> out.writeByte(0).apply { headerLength += Byte.SIZE_BYTES }
            PacketType.VARIABLE_SHORT -> out.writeShort(0).apply { headerLength += Short.SIZE_BYTES }
            else -> {}
        }

        /*
         * Write the packet payload to the buffer.
         */
        codec.encode(session, packet, out)

        /*
         * Update the length values for variable length packet types.
         */
        val length = out.readableBytes() - headerLength

        when(type) {
            PacketType.VARIABLE_BYTE -> out.setByte(1, length)
            PacketType.VARIABLE_SHORT -> out.setShort(1, length)
            else -> {}
        }
    }

}