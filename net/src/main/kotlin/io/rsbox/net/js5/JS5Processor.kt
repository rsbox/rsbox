package io.rsbox.net.js5

import io.rsbox.cache.GameCache
import io.rsbox.common.di.inject
import io.rsbox.net.Session
import org.tinylog.kotlin.Logger

object JS5Processor {

    private val gameCache: GameCache by inject()

    /**
     * In order to increase JS5 performance, we pre-calculate responses for each archive and group's cache data
     * so that we do not need to waste IO time after the start of the server.
     *
     * This data is simply just stored in memory.
     */
    private val CACHED_RESPONSES = hashMapOf<JS5Request, JS5Response>()

    /**
     * Whether all of the cache responses have been calculated.
     */
    private var allResponsesCached: Boolean = false

    fun handle(session: Session, request: JS5Request) {
        /*
         * If all response are not cached, we go ahead and cache all of the responses.
         */
        if(!allResponsesCached) {
            this.cacheJs5Responses()
        }

        val response = request.createResponse()
        session.writeAndFlush(response)
    }

    private fun cacheJs5Responses() {
        Logger.info("Caching all JS5 responses in memory...")

        /*
         * Create a response for the game cache MASTER INDEX (idx255)
         */
        createResponse(255, 255)

        /*
         * Create a response for each index table
         */
        for(i in 0 until gameCache.archiveCount) {
            createResponse(255, i)
        }

        /*
         * Create a response for every other archive group.
         */
        for(archive in 0 until gameCache.archiveCount) {
            gameCache.readArchive(archive).groupSettings.map { it.key }.forEach { group ->
                createResponse(archive, group)
            }
        }

        allResponsesCached = true

        Logger.info("${CACHED_RESPONSES.size} JS5 responses have been successfully cached into memory.")
    }

    private fun createResponse(archive: Int, group: Int): JS5Response {
        val data = gameCache.readGroup(archive, group)

        val compressionType = data.readUnsignedByte().toInt()
        val compressionLength = data.readInt()
        val bytes = ByteArray(data.writerIndex() - Byte.SIZE_BYTES - Int.SIZE_BYTES)
        data.readBytes(bytes)

        return JS5Response(archive, group, compressionType, compressionLength, bytes)
    }

    private fun JS5Request.createResponse(): JS5Response {
        val cachedRequest = JS5Request(archive, group, false)
        if(!CACHED_RESPONSES.containsKey(cachedRequest)) {
            CACHED_RESPONSES[cachedRequest] = createResponse(archive, group)
        }

        return CACHED_RESPONSES[cachedRequest]!!
    }
}