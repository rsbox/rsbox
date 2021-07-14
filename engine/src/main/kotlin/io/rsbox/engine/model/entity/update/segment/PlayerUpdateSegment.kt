package io.rsbox.engine.model.entity.update.segment

import io.guthix.buffer.writeBytesAdd
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.rsbox.engine.model.Tile
import io.rsbox.engine.model.entity.Player
import io.rsbox.engine.model.entity.update.UpdateSegment
import io.rsbox.engine.model.entity.update.UpdateType
import org.tinylog.kotlin.Logger
import kotlin.math.max

class PlayerUpdateSegment(private val player: Player, private val isNewPlayer: Boolean) : UpdateSegment {

    companion object {
        private val translation = arrayOf(-1, -1, -1, -1, 2, -1, 3, 5, 0, 4, 6, 1)
        private val animations = intArrayOf(808, 823, 819, 820, 821, 822, 824)

        private const val ARMS = 6
        private const val HAIR = 8
        private const val BEARD = 11
    }

    override fun encode(buf: ByteBuf): ByteBuf {

        /*
         * Do the initial calculations on the update state
         * of the player.
         */

        var mask = player.updates.mask

        var forceFaceEntity = false
        var forceFaceTile = false
        var forcedTile: Tile? = null

        if(isNewPlayer) {
            mask = mask or UpdateType.APPEARANCE.mask

            when {
                player.updates.state.faceAngle != 0 -> {
                    mask = mask or UpdateType.FACE_TILE.mask
                    forceFaceTile = true
                }

                player.updates.state.faceEntityIndex != -1 -> {
                    mask = mask or UpdateType.FACE_ENTITY.mask
                    forceFaceEntity = true
                }

                else -> {
                    mask = mask or UpdateType.FACE_TILE.mask
                    forcedTile = player.tile.translate(player.facingDirection)
                }
            }
        }

        if(mask >= 0x100) {
            mask = mask or UpdateType.EXCESS_MASK
            buf.writeByte(mask and 0xFF)
            buf.writeByte(mask shr 8)
        } else {
            buf.writeByte(mask and 0xFF)
        }

        UpdateType.values.forEach { type ->
            val forceUpdate = when(type) {
                UpdateType.FACE_TILE -> forceFaceTile || forcedTile != null
                UpdateType.FACE_ENTITY -> forceFaceEntity
                UpdateType.APPEARANCE -> isNewPlayer
                else -> false
            }

            /*
             * Check if the update state of the player has a update type in it's container.
             * If forceUpdate is true, then we force the player update.
             */
            if(player.updates.hasUpdate(type.mask) || forceUpdate) {
                encodePayload(buf, type, forcedTile)
            }
        }

        return buf
    }

    private fun encodePayload(buf: ByteBuf, updateType: UpdateType, forcedTile: Tile?) {
        when(updateType) {
            /**
             * APPEARANCE UPDATE TYPE
             */
            UpdateType.APPEARANCE -> {
                val appBuf = Unpooled.buffer()
                appBuf.writeByte(player.appearance.gender.id)
                appBuf.writeByte(player.skullIcon)
                appBuf.writeByte(player.prayerIcon)

                /*
                 * Appearance models
                 */
                for(i in 0 until 12) {
                    if(translation[i] == -1) {
                        appBuf.writeByte(0)
                    } else {
                        appBuf.writeShort(0x100 + player.appearance.models[translation[i]])
                    }
                }

                /*
                 * Appearance colors
                 */
                for(i in 0 until 5) {
                    val color = max(0, player.appearance.colors[i])
                    appBuf.writeByte(color)
                }

                /*
                 * Idle animations
                 */
                animations.forEach { animation ->
                    appBuf.writeShort(animation)
                }

                /*
                 * Other appearance data
                 */
                appBuf.writeStringCP1252(player.displayName)
                appBuf.writeByte(player.combatLevel)
                appBuf.writeShort(0)
                appBuf.writeByte(0)

                val appBytes = ByteArray(appBuf.readableBytes())
                appBuf.readBytes(appBytes)

                buf.writeByte(appBytes.size)
                buf.writeBytesAdd(appBytes)
            }

            else -> {
                Logger.warn("Received unhandled update type: $updateType.")
            }
        }
    }
}