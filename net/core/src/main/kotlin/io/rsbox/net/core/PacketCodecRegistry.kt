package io.rsbox.net.core

import kotlin.reflect.KClass

class PacketCodecRegistry<out P : Packet> {

    private val opcodeTypeMap = hashMapOf<KClass<out P>, Int>()
    private val codecs = hashMapOf<Int, PacketCodec<P>>()
    private val packetInfoMap = hashMapOf<Int, Pair<PacketDataType, Int>>()

    operator fun get(opcode: Int): PacketCodec<@UnsafeVariance P> = requireNotNull(this.codecs[opcode]) {
        "Unknown packet opcode: $opcode"
    }

    operator fun get(type: KClass<@UnsafeVariance P>): PacketCodec<@UnsafeVariance P> = this[getOpcode(type)]

    fun getOpcode(type: KClass<@UnsafeVariance P>): Int = requireNotNull(this.opcodeTypeMap[type]) {
        "Unknown packet type: ${type.simpleName}"
    }

    fun getDataType(type: KClass<@UnsafeVariance P>): PacketDataType = requireNotNull(this.packetInfoMap[getOpcode(type)]?.first) {
        "Unknown packet type: ${type.simpleName}"
    }

    fun getLength(type: KClass<@UnsafeVariance P>): Int = requireNotNull(this.packetInfoMap[getOpcode(type)]?.second) {
        "Unknown packet type: ${type.simpleName}"
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : @UnsafeVariance P> set(type: KClass<T>, opcode: Int, dataType: PacketDataType, length: Int, codec: PacketCodec<T>) {
        this.opcodeTypeMap[type] = opcode
        this.codecs[opcode] = codec as PacketCodec<P>
        this.packetInfoMap[opcode] = dataType to length
    }

    inline operator fun <reified T : @UnsafeVariance P> set(opcode: Int, length: Int, codec: PacketCodec<T>) {
        this[T::class, opcode, PacketDataType.FIXED, length] = codec
    }

    inline operator fun <reified T : @UnsafeVariance P> set(opcode: Int, codec: PacketCodec<T>) {
        this[T::class, opcode, PacketDataType.FIXED, -255] = codec
    }

    operator fun contains(opcode: Int): Boolean = opcode in this.codecs

}