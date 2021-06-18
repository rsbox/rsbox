package io.rsbox.engine.net.core

import kotlin.reflect.KClass

class MessageCodecRegistry(val protocol: Protocol) {

    private val opcodeTypeMap = mutableMapOf<KClass<out Message>, Int>()
    private val opcodeLengthMap = mutableMapOf<Int, Int>()
    private val codecs = mutableMapOf<Int, MessageCodec<out Message>>()

    fun getOpcode(type: KClass<out Message>): Int = requireNotNull(opcodeTypeMap[type]) {
        "Unknown message type: ${type.simpleName}"
    }

    operator fun get(opcode: Int): MessageCodec<out Message> = requireNotNull(codecs[opcode]) {
        "Unknown message opcode: $opcode"
    }

    fun getLength(opcode: Int): Int = requireNotNull(opcodeLengthMap[opcode]) {
        "Unknown message opcode: $opcode"
    }

    operator fun set(type: KClass<out Message>, opcode: Int, length: Int, codec: MessageCodec<out Message>) {
        this.opcodeTypeMap[type] = opcode
        this.opcodeLengthMap[opcode] = length
        this.codecs[opcode] = codec
    }

    inline operator fun <reified T : Message> set(opcode: Int, codec: MessageCodec<T>) {
        this[T::class, opcode, -255] = codec
    }

    inline operator fun <reified T : Message> set(mapping: Pair<Int, Int>, codec: MessageCodec<T>) {
        this[T::class, mapping.first, mapping.second] = codec
    }

}