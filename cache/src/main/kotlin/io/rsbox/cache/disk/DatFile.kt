package io.rsbox.cache.disk

import io.rsbox.cache.util.medium
import io.rsbox.cache.util.putMedium
import io.rsbox.cache.util.slice
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.math.min

class DatFile(val file: Path) : Closeable {

    private val channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)
    private val buf = ByteBuffer.allocate(SECTOR_SIZE)

    fun read(archive: Int, group: Int, length: Int, sector: Int): ByteBuffer {
        val out = ByteBuffer.allocate(length)
        var chunk = 0

        while(out.hasRemaining()) {
            channel.read(buf, (sector * SECTOR_SIZE).toLong())
            buf.clear()

            val sectorGroup = buf.short.toUInt()
            val sectorChunk = buf.short.toUInt()
            val newSector = buf.medium
            val sectorArchive = buf.get().toUInt()

            out.put(buf.slice(min(buf.remaining(), out.remaining())))
            buf.clear()
            chunk++
        }
        return out.clear()
    }

    fun write(archive: Int, group: Int, data: ByteBuffer): Int {
        val startSector = (channel.size() / SECTOR_SIZE)
        var sector = startSector
        var chunk = 0

        while(data.hasRemaining()) {
            buf.putShort(group.toShort())
            buf.putShort(chunk.toShort())
            buf.putMedium((sector + 1).toInt())
            buf.put(archive.toByte())
            buf.put(data.slice(min(buf.remaining(), data.remaining())))
            channel.write(buf.clear(), sector * SECTOR_SIZE)
            buf.clear()
            chunk++
            sector++
        }
        return startSector.toInt()
    }

    override fun close() {
        channel.close()
    }

    companion object {
        private const val SECTOR_SIZE = 520
    }
}