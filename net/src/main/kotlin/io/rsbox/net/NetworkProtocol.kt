package io.rsbox.net

import io.rsbox.net.core.PacketCodecRegistry
import io.rsbox.net.core.Protocol
import io.rsbox.net.handshake.HandshakeRequest

enum class NetworkProtocol : Protocol<NetworkPacket> {

    /**
     * HANDSHAKE PROTOCOL
     */
    HANDSHAKE {
        init {
            /*
             * Inbound
             */
            inbound[15] = HandshakeRequest

            /*
             * Outbound
             */

        }
    },

    /**
     * JS5 PROTOCOL
     */
    JS5 {
        init {

        }
    },

    /**
     * LOGIN / AUTH PROTOCOL
     */
    LOGIN {
        init {

        }
    },

    /**
     * GAME / IN-GAME PROTOCOL
     */
    GAME {
        init {

        }
    };

    override val inbound = PacketCodecRegistry<NetworkPacket>()

    override val outbound = PacketCodecRegistry<NetworkPacket>()
}