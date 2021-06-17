package io.rsbox.net.js5

import io.netty.buffer.ByteBuf
import io.rsbox.net.Session
import io.rsbox.net.core.Message
import io.rsbox.net.core.MessageCodec

class JS5Response(
    val archive: Int,
    val group: Int,
    val compressionType: Int,
    val compressionLength: Int,
    val data: ByteArray
) : Message {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JS5Response

        if (archive != other.archive) return false
        if (group != other.group) return false
        if (compressionType != other.compressionType) return false
        if (compressionLength != other.compressionLength) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = archive
        result = 31 * result + group
        result = 31 * result + compressionType
        result = 31 * result + compressionLength
        result = 31 * result + data.contentHashCode()
        return result
    }

    companion object : MessageCodec<JS5Response> {
        override fun encode(session: Session, out: ByteBuf, msg: JS5Response) {
            out.writeByte(msg.archive)
            out.writeShort(msg.group)
            out.writeByte(msg.compressionType)
            out.writeInt(msg.compressionLength)

            msg.data.forEach { byte ->
                if(out.writerIndex() % 512 == 0) {
                    out.writeByte(-1)
                }
                out.writeByte(byte.toInt())
            }
        }
    }
}