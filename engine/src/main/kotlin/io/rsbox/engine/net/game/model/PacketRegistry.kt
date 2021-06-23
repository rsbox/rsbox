package io.rsbox.engine.net.game.model

import io.rsbox.engine.net.game.packet.outbound.LoadRegionNormal

object PacketRegistry {

    /**
     * Packets which come inbound from the clients. (Server packets which get decoded)
     */
    val inbound = PacketCodecRegistry<Packet>()

    /**
     * Packets which go outbound to the connected clients. (Client Packets which get encoded)
     */
    val outbound = PacketCodecRegistry<Packet>()

    /**
     * Define the packet protocols to store in this packet registry.
     */
    init {
        /*
         * INBOUND PACKETS
         */


        /*
         * OUTBOUND PACKETS
         */
        outbound[54, PacketType.VARIABLE_SHORT] = LoadRegionNormal
    }

    @Suppress("UNCHECKED_CAST")
    private inline operator fun <reified P : Packet> PacketCodecRegistry<P>.set(opcode: Int, packetType: PacketType, codec: PacketCodec<out P>) {
        this[opcode, packetType.length, P::class] = codec as PacketCodec<P>
    }
}