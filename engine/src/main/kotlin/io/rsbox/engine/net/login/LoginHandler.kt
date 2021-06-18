package io.rsbox.engine.net.login

import io.rsbox.engine.net.Session

object LoginHandler {

    fun handle(session: Session, request: LoginRequest) {
        if(request is LoginRequest.Error) {
            session.writeAndClose(request.response)
            return
        }


    }

}