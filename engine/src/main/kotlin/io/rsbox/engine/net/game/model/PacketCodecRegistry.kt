package io.rsbox.engine.net.game.model

import kotlin.reflect.KClass

class PacketCodecRegistry<P : Packet> {

    private val codecs = mutableMapOf<Int, PacketCodec<P>>()
    private val lengths = mutableMapOf<Int, Int>()
    private val types = mutableMapOf<KClass<P>, Int>()

    fun getOpcode(type: KClass<out P>): Int = requireNotNull(types[type]) {
        "Unknown packet type: ${type.simpleName}"
    }

    fun getCodec(opcode: Int): PacketCodec<P> = requireNotNull(codecs[opcode]) {
        "Unknown packet opcode: $opcode"
    }

    fun getLength(opcode: Int): Int = requireNotNull(lengths[opcode]) {
        "Unknown packet opcode: $opcode"
    }

    fun getPacketType(opcode: Int): PacketType {
        return try {
            when(val length = getLength(opcode)) {
                -1 -> PacketType.VARIABLE_BYTE
                -2 -> PacketType.VARIABLE_SHORT
                else -> PacketType.FIXED(length)
            }
        } catch (e : Exception) {
            PacketType.UNKNOWN
        }
    }

    operator fun set(opcode: Int, length: Int, type: KClass<P>, codec: PacketCodec<P>) {
        this.codecs[opcode] = codec
        this.lengths[opcode] = length
        this.types[type] = opcode
    }

    fun setCodec(opcode: Int, codec: PacketCodec<P>) {
        this.codecs[opcode] = codec
    }

    fun setLength(opcode: Int, length: Int) {
        this.lengths[opcode] = length
    }

    fun setType(opcode: Int, type: KClass<P>) {
        this.types[type] = opcode
    }
}