package io.rsbox.net.core

import kotlin.reflect.KClass

class PacketCodecRegistry<out P : Packet> {

    private val opcodeTypeMap = hashMapOf<KClass<out P>, Int>()
    private val codecs = hashMapOf<Int, PacketCodec<P>>()

    operator fun get(opcode: Int): PacketCodec<@UnsafeVariance P> = requireNotNull(this.codecs[opcode]) {
        "Unknown packet opcode: $opcode"
    }

    operator fun get(type: KClass<@UnsafeVariance P>): PacketCodec<@UnsafeVariance P> = this[getOpcode(type)]

    fun getOpcode(type: KClass<@UnsafeVariance P>): Int = requireNotNull(this.opcodeTypeMap[type]) {
        "Unknown packet type: ${type.simpleName}"
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : @UnsafeVariance P> set(type: KClass<T>, opcode: Int, codec: PacketCodec<T>) {
        this.opcodeTypeMap[type] = opcode
        this.codecs[opcode] = codec as PacketCodec<P>
    }

    inline operator fun <reified T : @UnsafeVariance P> set(opcode: Int, codec: PacketCodec<T>) {
        this[T::class, opcode] = codec
    }

    operator fun contains(opcode: Int): Boolean = opcode in this.codecs

}