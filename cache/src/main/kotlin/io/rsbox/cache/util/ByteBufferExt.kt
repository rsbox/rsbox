package io.rsbox.cache.util

import java.nio.ByteBuffer

val ByteBuffer.medium: Int get() {
    return (this.short.toInt() shl 8) or (this.get().toInt() and 0xFF)
}

fun ByteBuffer.putMedium(value: Int) {
    this.putShort((value shr 8).toShort()).put(value.toByte())
}

fun ByteBuffer.slice(length: Int): ByteBuffer {
    val i = this.position()
    val slice = this.duplicate().limit(i)
    this.position(i)
    return slice
}