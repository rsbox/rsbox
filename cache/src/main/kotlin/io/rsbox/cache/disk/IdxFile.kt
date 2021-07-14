package io.rsbox.cache.disk

import io.rsbox.cache.util.medium
import io.rsbox.cache.util.putMedium
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class IdxFile(val file: Path) : Closeable {

    private val channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)
    private val buf = ByteBuffer.allocate(ENTRY_SIZE)

    val size: Int get() = (channel.size() / ENTRY_SIZE).toInt()

    fun read(group: Int): Entry? {
        val pos = group * ENTRY_SIZE
        if(pos + ENTRY_SIZE > channel.size()) return null
        channel.read(buf, pos.toLong())
        buf.clear()
        val length = buf.medium
        val sector = buf.medium
        buf.clear()
        if(length <= 0 && sector == 0) return null
        return Entry(length, sector)
    }

    fun write(group: Int, length: Int, sector: Int) {
        buf.putMedium(length)
        buf.putMedium(sector)
        channel.write(buf.clear(), (group * ENTRY_SIZE).toLong())
        buf.clear()
    }

    override fun close() {
        channel.close()
    }

    /**
     * Represents an IDX file's data entry which indexes some archive data location.
     *
     * @property length Int
     * @property sector Int
     * @constructor
     */
    data class Entry(val length: Int, val sector: Int)

    companion object {
        private const val ENTRY_SIZE = 6
    }
}