package io.rsbox.engine.module

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

/**
 * Reads the region xtea keys from the provided xtea.json
 */
object XteaProvider {

    val EMPTY_KEYS = IntArray(4) { 0 }

    private val xteaKeys = hashMapOf<Int, IntArray>()

    private val validRegions: IntArray get() = xteaKeys.keys.toIntArray()

    fun load(file: File) {
        if(!file.exists()) {
            throw FileNotFoundException("Unable to locate region XTEA keys file at: '${file.path}'.")
        }

        Logger.info("Loading region XTEA keys from: '${file.path}'.")

        val reader = Files.newBufferedReader(file.toPath())
        val xteas = Json { ignoreUnknownKeys = true }.decodeFromString<Array<RegionXteas>>(reader.readText())
        reader.close()

        xteas.forEach { xtea ->
            xteaKeys[xtea.mapsquare] = xtea.key
        }
    }

    operator fun get(region: Int): IntArray {
        if(!xteaKeys.containsKey(region)) {
            Logger.warn("No XTEA region keys found for region: $region.")
            return EMPTY_KEYS
        }

        return xteaKeys[region]!!
    }

    @Serializable
    data class RegionXteas(val mapsquare: Int, val key: IntArray)
}