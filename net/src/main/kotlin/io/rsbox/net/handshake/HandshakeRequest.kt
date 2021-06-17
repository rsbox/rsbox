package io.rsbox.net.handshake

import io.netty.buffer.ByteBuf
import io.rsbox.common.di.inject
import io.rsbox.config.RSBoxConfig
import io.rsbox.net.ServerResponseType
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodec
import io.rsbox.net.js5.JS5Protocol
import io.rsbox.net.login.LoginDecoder
import io.rsbox.net.login.LoginProtocol

sealed class HandshakeRequest : Message {

    /**
     * Represents a JS5 Handshake Request.
     *
     * @property revision Int
     * @constructor
     */
    class JS5(val revision: Int) : HandshakeRequest() {

        private val rsboxConfig: RSBoxConfig by inject()

        override fun handle(session: Session) {
            val serverRevision = rsboxConfig.revision

            /*
             * If the client has an mismatched revision from the server.
             */
            if(serverRevision != revision) {
                session.writeAndClose(ServerResponseType.REVISION_MISMATCH)
                return
            }

            /*
             * Update the session protocol to JS5
             */
            session.protocol = JS5Protocol(session)
            session.writeAndFlush(ServerResponseType.ACCEPTABLE)
        }

        companion object : MessageCodec<JS5> {
            override fun decode(session: Session, buf: ByteBuf): JS5 {
                val revision = buf.readInt()
                return JS5(revision)
            }
        }
    }

    /**
     * Represents a Login handshake request.
     */
    class Login : HandshakeRequest() {

        override fun handle(session: Session) {
            /*
             * Change the session protocol to the 'Login' protocol
             */
            session.protocol = LoginProtocol(session)
            session.loginDecoder = LoginDecoder(session)

            session.writeServerResponse(ServerResponseType.ACCEPTABLE)
            session.writeSeed(session.seed)
            session.channel.flush()
        }

        private fun Session.writeServerResponse(type: ServerResponseType) {
            this.ctx.write(this.ctx.alloc().buffer(Byte.SIZE_BYTES).writeByte(type.id))
        }

        private fun Session.writeSeed(seed: Long) {
            this.ctx.write(this.ctx.alloc().buffer(Long.SIZE_BYTES).writeLong(seed))
        }

        companion object : MessageCodec<Login> {
            override fun decode(session: Session, buf: ByteBuf): Login {
                return Login()
            }
        }
    }

}