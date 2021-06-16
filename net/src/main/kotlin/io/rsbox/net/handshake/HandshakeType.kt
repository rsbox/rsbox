package io.rsbox.net.handshake

enum class HandshakeType(val opcode: Int) {

    JS5(15),

    LOGIN(18);

    companion object {

        val values = enumValues<HandshakeType>()

        fun fromOpcode(opcode: Int): HandshakeType = values.firstOrNull { it.opcode == opcode }
            ?: throw IllegalArgumentException("Unknown handshake opcode: $opcode")

    }
}