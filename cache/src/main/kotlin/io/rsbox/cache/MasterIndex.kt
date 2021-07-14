package io.rsbox.cache

import java.nio.ByteBuffer

/**
 * Represents a MASTER index which holds records to all other Index entries.
 *
 * @property crc32 Int
 * @property version Int
 * @constructor
 */
class MasterIndex private constructor(val crc32: Int, val version: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MasterIndex

        if (crc32 != other.crc32) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = crc32
        result = 31 * result + version
        return result
    }

   companion object {

       /**
        * Decodes the master index bytes and returns a list of all the indexes which are referenced
        * by the master index file.
        *
        * @param masterIndex ByteBuffer
        * @return List<MasterIndex>
        */
       fun decodeAll(masterIndex: ByteBuffer): List<MasterIndex> {
           val count = masterIndex.remaining() / (Int.SIZE_BYTES * 2)
           val indexes = arrayOfNulls<MasterIndex>(count)
           for(i in 0 until count) {
               indexes[i] = MasterIndex(masterIndex.int, masterIndex.int)
           }

           return indexes.filterNotNull().toList()
       }
   }

}