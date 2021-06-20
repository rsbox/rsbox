package io.rsbox.engine.net.js5

import io.netty.buffer.ByteBuf
import io.rsbox.cache.GameCache
import io.rsbox.common.di.inject
import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Protocol
import io.rsbox.engine.net.ServerStatus
import io.rsbox.engine.net.Session
import org.tinylog.kotlin.Logger

class JS5Protocol(override val session: Session) : Protocol {

    override fun decode(buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) return

        buf.markReaderIndex()

        when(buf.readByte().toInt()) {
            GAME_INIT, GAME_LOADING, GAME_READY -> {
                buf.skipBytes(3)
            }

            REQUEST_PRIORITY, REQUEST_NORMAL -> {
                if(buf.readableBytes() >= 3) {
                    val archive = buf.readUnsignedByte().toInt()
                    val group = buf.readUnsignedShort()
                    val message = JS5Request(archive, group, false)
                    out.add(message)
                } else {
                    buf.resetReaderIndex()
                }
            }
        }
    }

    override fun encode(message: Message, out: ByteBuf) {
        when(message) {
            is ServerStatus -> {
                out.writeByte(message.id)
            }

            is JS5Response -> {
                out.writeByte(message.archive)
                out.writeShort(message.group)
                out.writeByte(message.compressionType)
                out.writeInt(message.compressionLength)

                message.data.forEach { byte ->
                    if(out.writerIndex() % 512 == 0) {
                        out.writeByte(-1)
                    }

                    out.writeByte(byte.toInt())
                }
            }

            else -> {
                /*
                 * Do nothing.
                 */
            }
        }
    }

    override fun handle(session: Session, message: Message) {
        if(message !is JS5Request) return

        if(!cached) generateCachedResponses()

        val response = message.createResponse()
        session.writeAndFlush(response)
    }

    companion object {
        private val gameCache: GameCache by inject()

        private const val REQUEST_PRIORITY = 0
        private const val REQUEST_NORMAL = 1
        private const val GAME_INIT = 2
        private const val GAME_LOADING = 3
        private const val GAME_READY = 6

        private var cached = false
        private val CACHED_RESPONSES = hashMapOf<JS5Request, JS5Response>()

        private fun generateCachedResponses() {
            Logger.info("Caching JS5 responses...")

            /*
             * Cache a response for the master index archive
             */
            createResponse(255, 255)

            for(i in 0 until gameCache.archiveCount) {
                createResponse(255, i)
            }

            for(archive in 0 until gameCache.archiveCount) {
                gameCache.readArchive(archive).groupSettings.map { it.key }.forEach { group ->
                    createResponse(archive, group)
                }
            }

            cached = true

            Logger.info("Successfully cached ${CACHED_RESPONSES.values.size} JS5 request responses.")
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
            if(!CACHED_RESPONSES.containsKey(this)) {
                CACHED_RESPONSES[this] = createResponse(archive, group)
            }

            return CACHED_RESPONSES[this]!!
        }
    }
}