package io.rsbox.net.login

import io.rsbox.net.Session

object LoginHandler {

    fun handle(session: Session, request: LoginRequest) {
        if(request is LoginRequest.Error) {
            session.writeAndClose(request.response)
            return
        }


    }

}