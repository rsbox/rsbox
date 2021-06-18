package io.rsbox.engine.net

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.rsbox.common.di.inject
import io.rsbox.engine.net.core.Message
import io.rsbox.engine.net.core.Protocol
import io.rsbox.engine.net.handshake.HandshakeProtocol
import io.rsbox.engine.net.login.LoginDecoder
import io.rsbox.engine.net.pipeline.GameChannelDecoder
import io.rsbox.engine.net.pipeline.GameChannelEncoder
import org.tinylog.kotlin.Logger
import java.util.*

/**
 * Represents a connection from a OSRS client to this server's networking socket.
 *
 * @property ctx ChannelHandlerContext
 * @constructor
 */
class Session(val ctx: ChannelHandlerContext) {

    private val networkServer: NetworkServer by inject()

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
    var protocol: Protocol = HandshakeProtocol(this)

    /**
     * A queue of messages which have been received inbound for this session but have
     * not been handled yet.
     */
    private val messageQueue = ArrayDeque<Message>()

    /**
     * The login decoder instance currently in use. If this field is null,
     * the current protocol is not doing a login.
     */
    internal var loginDecoder: LoginDecoder? = null

    internal fun onConnect() {
        /*
         * Register this session with the network server.
         */
        networkServer.sessions.add(this)

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

    fun close() {
        channel.close()
        networkServer.sessions.remove(this)
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
            cause.printStackTrace()
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