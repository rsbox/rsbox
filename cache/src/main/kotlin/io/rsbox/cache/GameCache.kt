package io.rsbox.cache

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.Js5Container
import io.guthix.js5.container.Js5Store
import io.guthix.js5.container.disk.Js5DiskStore
import io.netty.buffer.ByteBuf
import io.rsbox.common.di.inject
import org.tinylog.kotlin.Logger
import java.io.File

class GameCache {

    val diskStore: Js5DiskStore by inject()

    val cache: Js5Cache by inject()

    val crcs = mutableListOf<Int>()

    val archiveCount: Int get() = diskStore.archiveCount


    fun load() {
        crcs.clear()

        val cacheDir = File("data/cache/")

        if(cacheDir.listFiles()!!.isEmpty()) {
            throw IllegalStateException("No game cache files found. Copy OSRS cache files to 'data/cache/'.")
        }

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
    }

    fun close() {
        diskStore.close()
        cache.close()
    }

    fun readArchive(archive: Int) = cache.readArchive(archive)

    fun readGroups(archive: Int, group: Int): Map<Int, ByteBuf> = cache.readArchive(archive).readGroup(group).files.mapValues { it.value.data.retain() }

    fun readGroup(archive: Int, group: Int): ByteBuf = diskStore.read(archive, group).retain()

}