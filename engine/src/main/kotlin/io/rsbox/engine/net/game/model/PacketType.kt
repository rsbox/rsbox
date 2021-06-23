package io.rsbox.engine.net.game.model

sealed class PacketType(val length: Int) {

    class FIXED(length: Int) : PacketType(length)

    object VARIABLE_BYTE : PacketType(-1)

    object VARIABLE_SHORT : PacketType(-2)

    object UNKNOWN : PacketType(-3)

}
