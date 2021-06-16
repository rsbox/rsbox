package io.rsbox.net

import io.rsbox.net.core.Packet

sealed class NetworkPacket : Packet {

    abstract fun handle(session: NetworkSession)

    abstract class Handshake : NetworkPacket()

    abstract class JS5 : NetworkPacket()

    abstract class Login : NetworkPacket()

    abstract class Game : NetworkPacket()

}