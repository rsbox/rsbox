package io.rsbox.cache.old

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.netty.buffer.ByteBuf
import org.tinylog.kotlin.Logger
import java.io.File

/**
 * A wrapper object which holds instances and helper methods of the
 * Jagex disk store and Jagex cache.
 *
 * @constructor
 */
class GameCache {

    /**
     * The disk store instance of the opened Jagex cache directory.
     */
    lateinit var diskStore: Js5DiskStore private set

    /**
     * The Jagex cache object used for loading group entry files.
     */
    lateinit var cache: Js5Cache private set

    /**
     * A list of all of the cache archive checksums
     */
    val crcs = mutableListOf<Int>()

    /**
     * The number of archive files the opened cache contains.
     */
    val archiveCount: Int get() = diskStore.archiveCount

    /**
     * Opens the game cache files from a given provided directory.
     *
     * @param dir File
     */
    fun open(dir: File) {
        crcs.clear()

        if(dir.listFiles()!!.isEmpty()) {
            throw IllegalStateException("No game cache files found. Copy OSRS cache files to 'data/cache/'.")
        }

        diskStore = Js5DiskStore.open(dir.toPath())
        cache = Js5Cache(diskStore)

        Logger.info("Found ${diskStore.archiveCount} game cache archives.")

        /*
         * Validate the game cache.
         */
        Logger.info("Validating game cache files.")

        val validator = cache.generateValidator(
            includeWhirlpool = false,
            includeSizes = false
        )

        val container = Js5Container(validator.encode())
        diskStore.write(Js5Store.MASTER_INDEX, Js5Store.MASTER_INDEX, data = container.encode())
        validator.archiveValidators.map { it.crc }.also { crcs.addAll(it) }

        Logger.info("Successfully loaded game cache files from directory: ${dir.path}")
    }

    /**
     * Closes both the currently open Jagex disk store and the Jagex cache.
     */
    fun close() {
        diskStore.close()
        cache.close()
    }

    fun readArchive(archive: Int) = cache.readArchive(archive)

    fun readGroups(archive: Int, group: Int): Map<Int, ByteBuf> = cache.readArchive(archive).readGroup(group).files.mapValues { it.value.data.retain() }

    fun readGroup(archive: Int, group: Int): ByteBuf = diskStore.read(archive, group).retain()

}