package io.rsbox.engine.net.login

import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Session

data class LoginRequest(
    val session: Session,
    val username: String,
    val password: String?,
    val authCode: Int?,
    val isResizableMode: Boolean,
    val clientWidth: Int,
    val clientHeight: Int
) : Message