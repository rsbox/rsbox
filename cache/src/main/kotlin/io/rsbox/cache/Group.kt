package io.rsbox.cache

import io.rsbox.cache.util.XteaCipher.decrypt
import io.rsbox.cache.util.crc32
import io.rsbox.cache.util.slice
import io.rsbox.cache.util.wrapped
import java.nio.ByteBuffer


/**
 * Represents a group data entry within an archive. A cache group contains the files which hold
 * the entry's data.
 *
 * @property compression Compression
 * @property data ByteBuffer
 * @property crc32 Int
 * @property version Int
 * @constructor
 */
class Group private constructor(
    val compression: Compression,
    val data: ByteBuffer,
    val crc32: Int,
    val version: Int
) {

    companion object {

        /**
         * Loads a group by decompressing the data. Also supports decrypting if the group's files are
         * encrypted with XTEA keys.
         *
         * @param compressed ByteBuffer
         * @param xteas IntArray
         * @return Group
         */
        fun decompress(compressed: ByteBuffer, xteas: IntArray? = null): Group {
            val start = compressed.position()
            val compression = Compression.fromOrdinal(compressed.get().toInt())
            val length = compressed.int + compression.headerSize
            var b = compressed.slice(length)
            val crc32 = compressed.duplicate().flip().position(start).crc32()
            if(xteas != null) decrypt(b.wrapped().also { b = it }, xteas)
            val data = compression.decompress(b)
            var version = 0
            if(compressed.hasRemaining()) {
                version = compressed.short.toUShort().toInt()
                if(compressed.hasRemaining()) throw IllegalStateException("Failed to decompress group's file data.")
            }
            return Group(compression, data, crc32, version)
        }
    }
}