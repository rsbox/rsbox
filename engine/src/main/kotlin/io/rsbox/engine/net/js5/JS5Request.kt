package io.rsbox.engine.net.js5

import io.rsbox.engine.net.Message

data class JS5Request(val archive: Int, val group: Int, val priority: Boolean) : Message