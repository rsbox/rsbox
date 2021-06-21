package io.rsbox.engine.net.game.model

import kotlin.reflect.KClass

class PacketCodecRegistry<P : Packet> {

    private val codecs = mutableMapOf<Int, PacketCodec<P>>()
    private val lengths = mutableMapOf<Int, Int>()
    private val types = mutableMapOf<KClass<P>, Int>()

    fun getOpcode(type: KClass<P>): Int = types[type]
        ?: throw IllegalArgumentException("Unknown packet opcode for packet type: ${type.simpleName}.")

    operator fun get(opcode: Int):
}