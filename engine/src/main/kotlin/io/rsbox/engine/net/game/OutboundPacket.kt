package io.rsbox.engine.net.game

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OutboundPacket(val opcode: Int, val type: PacketType = PacketType.FIXED)
