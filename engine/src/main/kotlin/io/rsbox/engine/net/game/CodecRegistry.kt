package io.rsbox.engine.net.game

import kotlin.reflect.KClass

class CodecRegistry {

    private val packetMap = mutableMapOf<KClass<Packet>, Int>()
    private val codecMap = mutableMapOf<Int, Codec<Packet>>()
    private val lengthMap = mutableMapOf<Int, Int>()

    val size: Int get() = packetMap.size

    operator fun set(packet: KClass<Packet>, opcode: Int, length: Int, codec: Codec<Packet>) {
        this.packetMap[packet] = opcode
        this.codecMap[opcode] = codec
        this.lengthMap[opcode] = length
    }

    fun getOpcode(packet: KClass<out Packet>): Int = packetMap[packet]
        ?: throw IllegalArgumentException("Unknown packet type: ${packet.simpleName}")

    fun getCodec(opcode: Int): Codec<Packet> = codecMap[opcode]
        ?: throw IllegalArgumentException("Unknown packet opcode: $opcode")

    fun getLength(opcode: Int): Int = lengthMap[opcode]
        ?: throw IllegalArgumentException("Unknown packet opcode: $opcode")

    fun getPacketType(opcode: Int): PacketType = when(getLength(opcode)) {
        -1 -> PacketType.VARIABLE_BYTE
        -2 -> PacketType.VARIABLE_SHORT
        else -> PacketType.FIXED
    }
}