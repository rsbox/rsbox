package io.rsbox.engine.net

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.rsbox.common.di.inject
import io.rsbox.common.util.IsaacRandom
import io.rsbox.engine.net.game.GameProtocol
import io.rsbox.engine.net.handshake.HandshakeProtocol
import io.rsbox.engine.net.pipeline.GameChannelDecoder
import io.rsbox.engine.net.pipeline.GameChannelEncoder
import org.tinylog.kotlin.Logger
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference

class Session(val ctx: ChannelHandlerContext) {

    private val networkServer: NetworkServer by inject()

    val channel get() = ctx.channel()
    val remoteAddress = channel.remoteAddress()
    val seed = (Math.random()*Long.MAX_VALUE).toLong()
    val uuid = UUID.randomUUID()

    var protocol: AtomicReference<Protocol> = AtomicReference(null)

    var xteas: IntArray = IntArray(4) { 0 }
    var encodeIsaac: IsaacRandom = IsaacRandom()
    var decodeIsaac: IsaacRandom = IsaacRandom()

    private val messageQueue = LinkedBlockingQueue<Message>()

    fun connect() {
        /*
         * Add the decoder and encoder channel pipelines.
         */
        val decoder = GameChannelDecoder(this)
        val encoder = GameChannelEncoder(this)

        val p = channel.pipeline()
        p.addBefore("handler", "decoder", decoder)
        p.addBefore("decoder", "encoder", encoder)

        /*
         * Set the initial session protocol.
         */
        this.protocol.set(HandshakeProtocol(this))
    }

    fun disconnect() {
        channel.close()
        networkServer.sessions.remove(this)
    }

    fun receive(message: Message) {
        messageQueue.add(message)

        if(protocol.get() !is GameProtocol) {
            this.cycle()
        }
    }

    fun error(cause: Throwable) {
        /*
         * Ignore unimplemented operations within the session's network thread.
         */
        if(cause is UnsupportedOperationException) {
            return
        }

        if(cause.stackTrace.isEmpty() || cause.stackTrace[0].methodName != "read0") {
            Logger.error(cause) { "An exception occurred in [session: $uuid]." }
        }

        this.disconnect()
    }

    fun cycle() {
        while(messageQueue.isNotEmpty()) {
            val message = messageQueue.poll()
            this.protocol.get().handle(this, message)
        }
    }

    fun write(msg: Any) = ctx.write(msg)

    fun writeAndFlush(msg: Any) = ctx.writeAndFlush(msg)

    fun flush() = ctx.flush()

    fun writeAndClose(msg: Any) {
        writeAndFlush(msg).addListener { result ->
            if(result.isSuccess) {
                this.disconnect()
            }
        }
    }

    fun buffer(capacity: Int): ByteBuf = ctx.alloc().buffer(capacity)

}