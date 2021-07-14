package io.rsbox.cache.util

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.min

class ByteBufferInputStream(val buf: ByteBuffer) : InputStream() {

    override fun available(): Int {
        return buf.remaining()
    }

    override fun read(): Int {
        return if (buf.hasRemaining()) java.lang.Byte.toUnsignedInt(buf.get()) else -1
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        Objects.checkFromIndexSize(off, len, b.size)
        if (len == 0) return 0
        val remaining: Int = buf.remaining()
        if (remaining == 0) return -1
        val n = min(remaining, len)
        buf.get(b, off, n)
        return n
    }

    override fun read(b: ByteArray): Int {
        val remaining: Int = buf.remaining()
        if (remaining == 0) return -1
        val n = min(remaining, b.size)
        buf.get(b, 0, n)
        return n
    }

    override fun readAllBytes(): ByteArray {
        return buf.toByteArray()
    }

    override fun readNBytes(len: Int): ByteArray {
        require(len >= 0)
        return buf.toByteArray(min(buf.remaining(), len))
    }

    override fun readNBytes(b: ByteArray, off: Int, len: Int): Int {
        Objects.checkFromIndexSize(off, len, b.size)
        val n = Math.min(buf.remaining(), len)
        buf.get(b, off, n)
        return n
    }

    override fun skip(n: Long): Long {
        if (n <= 0) return 0
        val count = min(buf.remaining().toLong(), n).toInt()
        buf.position(buf.position() + count)
        return count.toLong()
    }

    override fun transferTo(out: OutputStream): Long {
        return buf.writeTo(out)
    }

    override fun markSupported(): Boolean {
        return true
    }

    override fun mark(readlimit: Int) {
        buf.mark()
    }

    override fun reset() {
        buf.reset()
    }

    override fun toString(): String {
        return "ByteBufferInputStream(buf=$buf)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteBufferInputStream) return false
        return buf === other.buf
    }

    override fun hashCode(): Int {
        return System.identityHashCode(buf)
    }
}