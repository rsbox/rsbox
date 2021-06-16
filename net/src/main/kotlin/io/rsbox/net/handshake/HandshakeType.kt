package io.rsbox.net.handshake

enum class HandshakeType(val id: Int) {

    JS5(id = 15),

    LOGIN(id = 18);

    companion object {

        val values = enumValues<HandshakeType>()

        fun fromId(id: Int): HandshakeType {
            return values.firstOrNull { it.id == id } ?: throw IllegalArgumentException("Unknown handshake type with ID: $id.")
        }

    }
}