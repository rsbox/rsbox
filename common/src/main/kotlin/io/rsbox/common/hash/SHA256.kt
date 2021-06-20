package io.rsbox.common.hash

import java.security.MessageDigest

object SHA256 {

    private val digest = MessageDigest.getInstance("SHA-256")

    fun hash(string: String): String {
        val hashed = digest.digest(string.toByteArray(Charsets.UTF_8))
        return hashed.toHexString()
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") { String.format("%02X", it) }
    }
}