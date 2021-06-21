package io.rsbox.engine.net.game.model

object PacketRegistry {

    private val inbound = PacketCodecRegistry<Packet>()
    private val outbound = PacketCodecRegistry<Packet>()

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


    }


}