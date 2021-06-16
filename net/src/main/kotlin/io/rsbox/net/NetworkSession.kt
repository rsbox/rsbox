package io.rsbox.net

import io.rsbox.net.core.Session
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.NetSocket

/**
 * Represents a client connection to this server.
 */
data class NetworkSession(
    val vertx: Vertx,
    val server: NetworkServer,
    val socket: NetSocket
) : Session {

    private var connected: Boolean = true

    /**
     * The current network protocol this session is using for processing packets.
     */
    internal var protocol: NetworkProtocol = NetworkProtocol.HANDSHAKE

    internal fun init() {
        /*
         * Nothing to do
         */
    }

    internal fun close() {
        this.connected = false
    }

    internal fun receive(event: Buffer) {
        val buf = event.byteBuf
        buf.markReaderIndex()

        val opcode = buf.readUnsignedByte().toInt()
        val codec = this.protocol.inbound[opcode]
        val packet = codec.decode(buf)
        packet.handle(this)
    }

    fun write(packet: NetworkPacket) {
        throw UnsupportedOperationException("Sending packet not yet supported.")
    }
}