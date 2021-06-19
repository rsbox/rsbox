package io.rsbox.engine.net.handshake

import io.rsbox.engine.net.Message

sealed class HandshakeRequest(val type: HandshakeType) : Message {

    class JS5 : HandshakeRequest(HandshakeType.JS5)

    class Login : HandshakeRequest(HandshakeType.LOGIN)

}