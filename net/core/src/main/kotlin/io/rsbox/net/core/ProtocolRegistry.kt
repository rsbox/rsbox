package io.rsbox.net.core

import kotlin.reflect.KClass

class ProtocolRegistry<P : Packet>(val direction: Direction) {

    private val protocolCodecRegistry = hashMapOf<Protocol<P>, PacketCodecRegistry<P>>()

    operator fun get(protocol: Protocol<P>): PacketCodecRegistry<P> = requireNotNull(this.protocolCodecRegistry[protocol]) {
        "No codec registry found for protocol ${protocol::class.java.simpleName}"
    }

    operator fun <T : @UnsafeVariance P> set(type: KClass<T>, mapping: Pair<Int, Protocol<T>>, codec: PacketCodec<T>) {
        val protocol = mapping.second
        val opcode = mapping.first

        if(!protocolCodecRegistry.containsKey(protocol)) {
            protocolCodecRegistry[protocol] = PacketCodecRegistry()
        }

        val registry = this[protocol]
        registry[type, opcode] = codec
    }

    inline operator fun <reified T : @UnsafeVariance P> set(mapping: Pair<Int, Protocol<T>>, codec: PacketCodec<T>) {
        set(T::class, mapping, codec)
    }


}