package io.rsbox.engine.net.game

import io.github.classgraph.ClassGraph
import org.tinylog.kotlin.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

object PacketRegistry {

    val inbound = CodecRegistry()

    val outbound = CodecRegistry()

    internal fun register() {
        Logger.info("Registering game engine packets...")

        this.registerOutboundPackets()
        this.registerInboundPackets()

        Logger.info("Registered ${inbound.size} INBOUND game packets.")
        Logger.info("Registered ${outbound.size} OUTBOUND game packets.")
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerOutboundPackets() {
        val scan = ClassGraph()
            .enableAllInfo()
            .acceptPackages("io.rsbox.engine.net.packet.outbound")
            .scan()
            .getClassesWithAnnotation(OutboundPacket::class.qualifiedName)

        scan.forEach { classInfo ->
            val annotation = classInfo.getAnnotationInfo(OutboundPacket::class.qualifiedName).loadClassAndInstantiate() as OutboundPacket
            val opcode = annotation.opcode
            val packetType = annotation.type
            val packetClass = classInfo.loadClass().kotlin as KClass<Packet>
            val codec = packetClass.companionObjectInstance as Codec<Packet>
            val length = when(packetType) {
                PacketType.VARIABLE_BYTE -> -1
                PacketType.VARIABLE_SHORT -> -2
                PacketType.FIXED -> -3
            }

            /*
             * Register the outbound packet data
             */
            outbound[packetClass, opcode, length] = codec
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerInboundPackets() {
        val scan = ClassGraph()
            .enableAllInfo()
            .acceptPackages("io.rsbox.engine.net.packet.inbound")
            .scan()
            .getClassesWithAnnotation(InboundPacket::class.qualifiedName)

        scan.forEach { classInfo ->
            val annotation = classInfo.getAnnotationInfo(InboundPacket::class.qualifiedName).loadClassAndInstantiate() as InboundPacket
            val opcode = annotation.opcode
            val length = annotation.length
            val packetClass = classInfo.loadClass().kotlin as KClass<Packet>
            val codec = packetClass.companionObjectInstance as Codec<Packet>

            /*
             * Register the inbound packet data
             */
            inbound[packetClass, opcode, length] = codec
        }
    }
}