package io.rsbox.engine.model.entity.update

import io.netty.buffer.ByteBuf

interface UpdateSegment {

    fun encode(buf: ByteBuf): ByteBuf

}