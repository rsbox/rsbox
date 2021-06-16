package io.rsbox.net

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.rsbox.net.core.Message
import io.rsbox.net.handshake.HandshakeProtocol
import io.rsbox.net.pipeline.GameChannelDecoder
import io.rsbox.net.pipeline.GameChannelEncoder
import org.tinylog.kotlin.Logger
import java.util.*

/**
 * Represents a connection from a OSRS client to this server's networking socket.
 *
 * @property ctx ChannelHandlerContext
 * @constructor
 */
class Session(val ctx: ChannelHandlerContext) {

    /**
     * The netty IO socket channel of this session.
     */
    val channel get() = ctx.channel()

    /**
     * The remote socket address of the session's connection.
     */
    val remoteAddress get() = ctx.channel().remoteAddress()

    /**
     * Whether the connection is currently connected.
     */
    val isConnected get() = ctx.channel().isActive

    /**
     * A unique identifier of this session.
     */
    val uuid = UUID.randomUUID()

    /**
     * A random seed used to seed the ISAAC random number generator which is used
     * during the game protocol to obfuscate packet opcodes.
     *
     * This is needed to prevent MAN-IN-THE-MIDDLE attacks.
     */
    val seed: Long = (Math.random() * Long.MAX_VALUE).toLong()

    /**
     * The current network protocol this session is using for processing inbound
     * and outbound packets.
     */
    var protocol = HandshakeProtocol(this)

    /**
     * A queue of messages which have been received inbound for this session but have
     * not been handled yet.
     */
    private val messageQueue = ArrayDeque<Message>()

    internal fun onConnect() {
        /*
         * Add the final channel encoder and decoder pipelines for this session's
         * network connection.
         */
        val encoder = GameChannelEncoder(this)
        val decoder = GameChannelDecoder(this)
        val p = channel.pipeline()

        p.addBefore("handler", "decoder", decoder)
        p.addBefore("decoder", "encoder", encoder)
    }

    internal fun onDisconnect() {
        println("Connection lost")
    }

    fun close() {
        channel.close()
    }

    internal fun receive(msg: Any) {
        if(msg !is Message) return

        /*
         * Handle the message.
         */
        messageQueue.addFirst(msg)

        /*
         * If the protocol of this session is Handshake, JS5, or Login.
         * We can cycle the message queue on the current session's thread.
         *
         * For the game protocol, we want to cycle the message queue in sync with the
         * main game cycle thread.
         */
        this.cycle()
    }

    /**
     * Processes the queue of messages for this sessions and handles each one.
     */
    fun cycle() {
        while(messageQueue.isNotEmpty()) {
            val msg = messageQueue.pop()
            msg.handle(this)
        }
    }

    internal fun onError(cause: Throwable) {
        if(cause.stackTrace.isEmpty() || cause.stackTrace[0].methodName != "read0") {
            Logger.error("An exception occurred in [session: $uuid] network thread.")
        }

        this.close()
    }

    fun write(msg: Message): ChannelFuture = ctx.write(msg)

    fun writeAndFlush(msg: Message): ChannelFuture = ctx.writeAndFlush(msg)

    fun flush(): ChannelHandlerContext = ctx.flush()

    fun writeAndClose(msg: Message) {
        ctx.writeAndFlush(msg).addListener { f ->
            if(f.isSuccess) this.close()
        }
    }
}