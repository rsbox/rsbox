package io.rsbox.engine.net.game

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InboundPacket(val opcode: Int, val length: Int)
