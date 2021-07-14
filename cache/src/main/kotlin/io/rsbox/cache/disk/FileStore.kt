package io.rsbox.cache.disk

import io.rsbox.cache.Cache
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path

/**
 * Represents a OSRS cache file store located at a provided directory path.
 *
 * @property directory Path
 * @constructor
 */
class FileStore private constructor(private val directory: Path) : Closeable {

    private val datFile: DatFile = DatFile(directory.resolve(Cache.DAT_FILE_NAME))
    private val idxFiles = arrayOfNulls<IdxFile>(Cache.MASTER_ARCHIVE + 1)

    /**
     * The number of archives this cache file store contains index IDX files for.
     */
    val archiveCount: Int get() {
        return getIdxFile(Cache.MASTER_ARCHIVE).size
    }

    /**
     * Gets a IDX file for a give archive ID.
     *
     * @param archive Int
     * @return IdxFile
     */
    fun getIdxFile(archive: Int): IdxFile {
        return idxFiles[archive] ?: IdxFile(directory.resolve(Cache.IDX_FILE_NAME + archive)).apply {
            idxFiles[archive] = this
        }
    }

    /**
     * Gets the raw compressed bytes in a buffer from a archive+group entry.
     *
     * @param archive Int
     * @param group Int
     * @return ByteBuffer?
     */
    fun getGroupCompressed(archive: Int, group: Int): ByteBuffer? {
        return when(val entry = getIdxFile(archive).read(group)) {
            null -> null
            else -> datFile.read(archive, group, entry.length, entry.sector)
        }
    }

    /**
     * Sets the raw compressed data from a provided byte buffer for an archive+group cache
     * entry.
     *
     * @param archive Int
     * @param group Int
     * @param buf ByteBuffer
     */
    fun setGroupCompressed(archive: Int, group: Int, buf: ByteBuffer) {
        getIdxFile(archive).write(group, buf.remaining(), datFile.write(archive, group, buf))
    }

    override fun close() {
        datFile.close()
        idxFiles.filterNotNull().forEach {
            it.close()
        }
    }

    companion object {
        /**
         * Creates and opens a new cache file store from a provided directory of cache files.
         *
         * @param directory Path
         * @return FileStore
         */
        fun open(directory: Path): FileStore {
            Files.createDirectories(directory)
            return FileStore(directory)
        }
    }
}