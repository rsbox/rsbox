package io.rsbox.cache.util

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.zip.CRC32

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

fun ByteBuffer.sliceShort(length: Int): ShortBuffer {
    return this.slice(length * Short.SIZE_BYTES).asShortBuffer()
}

fun ByteBuffer.sliceInt(length: Int): IntBuffer {
    return this.slice(length * Int.SIZE_BYTES).asIntBuffer()
}

fun ByteBuffer.writeTo(out: OutputStream): Long {
    val length = this.remaining()
    if(this.hasArray()) {
        out.write(this.array(), this.arrayOffset() + this.position(), length)
        this.position(this.limit())
    } else {
        out.write(this.toByteArray(length))
    }

    return length.toLong()
}

fun ByteBuffer.wrapped(): ByteBuffer = ByteBuffer.wrap(this.toByteArray())

fun ByteBuffer.toByteArray(): ByteArray = this.toByteArray(this.remaining())

fun ByteBuffer.toByteArray(length: Int): ByteArray {
    val bytes = ByteArray(length)
    this.get(bytes)
    return bytes
}

fun ByteBuffer.crc32(): Int {
    val crc = CRC32()
    crc.update(this)
    return crc.value.toInt()
}

fun merge(vararg bufs: ByteBuffer): ByteBuffer {
    if(bufs.size == 1) return bufs[0]
    var length = 0
    bufs.forEach { b ->
        length += b.remaining()
    }
    val buf = ByteBuffer.allocate(length)
    bufs.forEach { b ->
        buf.put(b)
    }
    return buf.flip()
}