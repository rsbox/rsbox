package io.rsbox.engine.net.login

import io.rsbox.engine.net.Message
import io.rsbox.engine.net.Session

data class LoginRequest(
    val session: Session,
    val username: String,
    val password: String?,
    val xteas: IntArray,
    val authCode: Int?,
    val isResizableMode: Boolean,
    val clientWidth: Int,
    val clientHeight: Int
) : Message {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginRequest

        if (session != other.session) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (!xteas.contentEquals(other.xteas)) return false
        if (authCode != other.authCode) return false
        if (isResizableMode != other.isResizableMode) return false
        if (clientWidth != other.clientWidth) return false
        if (clientHeight != other.clientHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = session.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + xteas.contentHashCode()
        result = 31 * result + (authCode ?: 0)
        result = 31 * result + isResizableMode.hashCode()
        result = 31 * result + clientWidth
        result = 31 * result + clientHeight
        return result
    }
}