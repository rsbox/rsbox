package io.rsbox.net.core

interface Protocol<out P : Packet> {

    /**
     * A packet codec registry of the inbound packets for a given protocol.
     */
    val inbound: PacketCodecRegistry<@UnsafeVariance P>

    /**
     * A packet codec registry of the outbound packets for a given protocol.
     */
    val outbound: PacketCodecRegistry<@UnsafeVariance P>

}