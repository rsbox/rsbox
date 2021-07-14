package io.rsbox.cache

import io.rsbox.cache.disk.FileStore

class Cache private constructor(private val fileStore: FileStore) {



    companion object {
        /**
         * The name prefix of the data cache dat2 files.
         */
        const val DAT_FILE_NAME = "main_file_cache.dat2"

        /**
         * The name prefix of the index cache idx files.
         */
        const val IDX_FILE_NAME = "main_file_cache.idx"

        /**
         * The Master archive / master index id appended to the idx file.
         */
        const val MASTER_ARCHIVE = 255

        /**
         * Loads the cache model structure from the data that has been loaded into a provided
         * cache file store.
         *
         * @param fileStore FileStore
         * @return Cache
         */
        fun load(fileStore: FileStore): Cache = Cache(fileStore)
    }
}