package io.rsbox.cache

import io.rsbox.cache.util.ByteBufferInputStream
import io.rsbox.cache.util.ByteBufferOutputStream
import io.rsbox.cache.util.wrapped
import io.rsbox.cache.util.writeTo
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.SequenceInputStream
import java.nio.ByteBuffer
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Responsible for the implementation of the various compressions used
 * within the OSRS game cache for compressing group data files.
 */
sealed class Compression {

    abstract fun compress(buf: ByteBuffer, out: ByteBufferOutputStream)

    abstract fun decompress(compressed: ByteBuffer): ByteBuffer

    fun compress(buf: ByteBuffer): ByteBuffer {
        val out = ByteBufferOutputStream()
        compress(buf, out)
        return out.buf.flip()
    }

    val ordinal: Byte get() = when(this) {
        NONE -> 0
        BZIP2 -> 1
        GZIP -> 2
    }

    val headerSize: Int get() = if(this == NONE) 0 else Int.SIZE_BYTES

    /**
     * Represents no data compression implementation.
     */
    object NONE : Compression() {

        override fun compress(buf: ByteBuffer, out: ByteBufferOutputStream) {
            out.write(buf)
        }

        override fun decompress(compressed: ByteBuffer): ByteBuffer {
            return compressed.wrapped()
        }
    }

    /**
     * Represents the BZIP2 compression standard
     */
    object BZIP2 : Compression() {

        private const val BLOCK_SIZE = 1

        private val HEADER = byteArrayOf('B'.code.toByte(), 'Z'.code.toByte(), 'h'.code.toByte(), ('0' + BLOCK_SIZE).code.toByte())

        override fun compress(buf: ByteBuffer, out: ByteBufferOutputStream) {
            val start = out.buf.position()
            val length = buf.remaining()
            val stream = BZip2CompressorOutputStream(out, BLOCK_SIZE)
            buf.writeTo(stream)
            out.buf.putInt(start, length)
        }

        override fun decompress(compressed: ByteBuffer): ByteBuffer {
            val bytes = ByteArray(compressed.int)
            val stream = BZip2CompressorInputStream(SequenceInputStream(ByteArrayInputStream(HEADER), ByteBufferInputStream(compressed)))
            stream.readNBytes(bytes, 0, bytes.size)
            return ByteBuffer.wrap(bytes)
        }
    }

    /**
     * Represents the GZIP compression implementation used within the game group file data.
     */
    object GZIP : Compression() {

        override fun compress(buf: ByteBuffer, out: ByteBufferOutputStream) {
            out.writeInt(buf.remaining())
            val stream = GZIPOutputStream(out)
            buf.writeTo(stream)
        }

        override fun decompress(compressed: ByteBuffer): ByteBuffer {
            val bytes = ByteArray(compressed.int)
            val stream = GZIPInputStream(ByteBufferInputStream(compressed))
            stream.readNBytes(bytes, 0, bytes.size)
            return ByteBuffer.wrap(bytes)
        }
    }

    companion object {

        fun fromOrdinal(ordinal: Int): Compression = when(ordinal) {
            0 -> NONE
            1 -> BZIP2
            2 -> GZIP
            else -> throw IllegalArgumentException("Unknown ordinal compression type of value: $ordinal.")
        }

        fun best(buf: ByteBuffer): Compression {
            val out = ByteBufferOutputStream()
            GZIP.compress(buf.duplicate(), out)
            val gzip = out.buf.position()
            out.buf.clear()
            BZIP2.compress(buf.duplicate(), out)
            val bzip2 = out.buf.position()
            val none = buf.remaining()
            if(none <= gzip && none <= bzip2) return NONE
            if(gzip <= bzip2) return GZIP
            return BZIP2
        }
    }
}