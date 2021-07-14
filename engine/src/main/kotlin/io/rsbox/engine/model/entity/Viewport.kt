package io.rsbox.engine.model.entity

import io.netty.buffer.Unpooled
import io.rsbox.engine.model.entity.update.UpdateSegment
import io.rsbox.engine.model.entity.update.segment.*
import io.rsbox.engine.net.packet.outbound.PlayerUpdate

class Viewport(val client: Client) {

    val player get() = client.player

    val session get() = client.session

    /**
     * GPI Local Scene Players
     */
    val gpiLocalPlayers = arrayOfNulls<Player>(2048)
    val gpiLocalPlayerIndexes = IntArray(2048)
    var gpiLocalPlayerCount = 0

    /**
     * GPI External Scene Players
     */
    val gpiExternalPlayerIndexes = IntArray(2048)
    var gpiExternalPlayerCount = 0

    /**
     * The flags used to determine if whether a given local player is processed or
     * not on the client
     */
    val gpiPlayerSkipFlags = IntArray(2048)

    /**
     * The array of 13-bit tile hashes functioning as multipliers.
     */
    val gpiTileHashes = IntArray(2048)

    /**
     * Initializes the client's viewport GPI. This is done when the player logs in.
     */
    fun initialize() {
        /*
         * Set this viewport's player's local gpi data first
         */
        gpiLocalPlayers[player.index] = player
        gpiLocalPlayerIndexes[gpiLocalPlayerCount++] = player.index

        /*
         * Set everyone else's local player gpi.
         */
        for(i in 1 until 2048) {
            if(i == player.index) continue
            gpiExternalPlayerIndexes[gpiExternalPlayerCount++] = i
            gpiTileHashes[i] = client.player.world.players[i]?.tile?.as18BitInteger ?: 0
        }
    }

    fun reset() {
        gpiLocalPlayerCount = 0
        gpiExternalPlayerCount = 0

        for(i in 1 until 2048) {
            if(gpiLocalPlayers[i] != null) {
                gpiLocalPlayerIndexes[gpiLocalPlayerCount++] = i
            } else {
                gpiExternalPlayerIndexes[gpiExternalPlayerCount++] = i
            }

            gpiPlayerSkipFlags[i] = gpiPlayerSkipFlags[i] shr 1
        }
    }

    /**
     * Creates the player update of this current player's viewport
     */
    fun updatePlayer() {
        val segments = getPlayerUpdateSegments()

        /*
         * Buffers
         */
        var buf = Unpooled.buffer()
        var maskBuf = Unpooled.buffer()

        for(segment in segments) {
            when(segment) {
                is PlayerUpdateSegment -> maskBuf = segment.encode(maskBuf)
                else -> buf = segment.encode(buf)
            }
        }

        val maskBytes = ByteArray(maskBuf.readableBytes())
        maskBuf.readBytes(maskBytes)

        buf.writeBytes(maskBytes)

        client.write(PlayerUpdate(payload = buf))

        /*
         * Clear / reset the gpi.
         */
        reset()
    }

    private fun getPlayerUpdateSegments(): List<UpdateSegment> {
        val segments = mutableListOf<UpdateSegment>()

        /*
         * Local segments.
         */
        addLocalPlayerUpdateSegments(true, segments)
        addLocalPlayerUpdateSegments(false, segments)

        var additions = 0

        /*
         * External segments
         */
        additions += addExternalPlayerUpdateSegments(true, additions, segments)
        additions += addExternalPlayerUpdateSegments(false, additions, segments)

        return segments
    }

    private fun addLocalPlayerUpdateSegments(inital: Boolean, segments: MutableList<UpdateSegment>) {
        var skipCount = 0

        for(i in 0 until gpiLocalPlayerCount) {
            val index = gpiLocalPlayerIndexes[i]
            val player = gpiLocalPlayers[index]

            val skip = when(inital) {
                true -> (gpiPlayerSkipFlags[index] and 0x1) != 0
                else -> (gpiPlayerSkipFlags[index] and 0x1) == 0
            }

            if(skip) {
                continue
            }

            if(skipCount > 0) {
                skipCount--
                gpiPlayerSkipFlags[index] = gpiPlayerSkipFlags[index] or 0x2
                continue
            }

            if(player != client.player && (player == null || shouldUnrender(player))) {
                val lastTileHash = gpiTileHashes[index]
                val currentTileHash = player?.tile?.as18BitInteger ?: 0
                val shouldUpdateTileHash = lastTileHash != currentTileHash

                /*
                 * Remove Local Player Segment
                 */
                segments.add(RemoveLocalPlayerSegment(shouldUpdateTileHash))

                if(shouldUpdateTileHash) {
                    /*
                     * Player location hash segment
                     */
                    segments.add(PlayerTileHashSegment(lastTileHash, currentTileHash))
                }

                gpiLocalPlayers[index] = null
                gpiTileHashes[index] = currentTileHash
                continue
            }

            val requiresUpdateSegment = player.updates.hasUpdates()
            if(requiresUpdateSegment) {
                segments.add(PlayerUpdateSegment(player, isNewPlayer = false))
            }

            if(requiresUpdateSegment) {
                segments.add(SignalPlayerUpdateSegment())
            } else {
                for(j in i + 1 until gpiLocalPlayerCount) {
                    val nextIndex = gpiLocalPlayerIndexes[j]
                    val nextPlayer = gpiLocalPlayers[nextIndex]

                    val skipNext = when(inital) {
                        true -> (gpiPlayerSkipFlags[nextIndex] and 0x1) != 0
                        else -> (gpiPlayerSkipFlags[nextIndex] and 0x1) == 0
                    }

                    if(skipNext) {
                        continue
                    }

                    if(nextPlayer == null || nextPlayer != client.player && shouldUnrender(nextPlayer)) {
                        break
                    }
                    skipCount++
                }

                segments.add(PlayerSkipCountSegment(skipCount))
                gpiPlayerSkipFlags[index] = gpiPlayerSkipFlags[index] or 0x2
            }
        }

        if(skipCount > 0) {
            throw RuntimeException("Invalid player updating skip flag count.")
        }
    }

    private fun addExternalPlayerUpdateSegments(initial: Boolean, prevAdditions: Int, segments: MutableList<UpdateSegment>): Int {
        var skipCount = 0
        var additions = prevAdditions

        for(i in 0 until gpiExternalPlayerCount) {
            val index = gpiExternalPlayerIndexes[i]

            val skip = when(initial) {
                true -> (gpiPlayerSkipFlags[index] and 0x1) == 0
                else -> (gpiPlayerSkipFlags[index] and 0x1) != 0
            }

            if(skip) {
                continue
            }

            if(skipCount > 0) {
                skipCount--
                gpiPlayerSkipFlags[index] = gpiPlayerSkipFlags[index] or 0x2
                continue
            }

            val externalPlayer = if(index < player.world.players.capacity) player.world.players[index] else null

            if(externalPlayer != null && additions < 50 && gpiLocalPlayerCount + additions < 256 && shouldRender(externalPlayer)) {
                val lastTileHash = gpiTileHashes[index]
                val currentTileHash = externalPlayer.tile.as18BitInteger

                val tileHashSegment = if(lastTileHash != currentTileHash) PlayerTileHashSegment(lastTileHash, currentTileHash) else null

                segments.add(AddLocalPlayerSegment(externalPlayer, tileHashSegment))
                segments.add(PlayerUpdateSegment(externalPlayer, isNewPlayer = true))

                gpiPlayerSkipFlags[index] = gpiPlayerSkipFlags[index] or 0x2
                gpiTileHashes[index] = currentTileHash
                gpiLocalPlayers[index] = externalPlayer

                additions++
                continue
            }

            for(j in i + 1 until gpiExternalPlayerCount) {
                val nextIndex = gpiExternalPlayerIndexes[j]

                val skipNext = when(initial) {
                    true -> (gpiPlayerSkipFlags[nextIndex] and 0x1) == 0
                    else -> (gpiPlayerSkipFlags[nextIndex] and 0x1) != 0
                }

                if(skipNext) {
                    continue
                }

                val nextPlayer = if(nextIndex < player.world.players.capacity) player.world.players[nextIndex] else null
                if(nextPlayer != null && (shouldRender(nextPlayer) || nextPlayer.tile.as18BitInteger != gpiTileHashes[nextIndex])) {
                    break
                }

                skipCount++
            }

            segments.add(PlayerSkipCountSegment(skipCount))
            gpiPlayerSkipFlags[index] = gpiPlayerSkipFlags[index] or 0x2
        }

        if(skipCount > 0) {
            throw RuntimeException("Invalid player skip count for external segments.")
        }

        return additions
    }

    private fun shouldRender(other: Player): Boolean {
        return other.tile.isWithinRadius(client.player.tile, RENDER_PLAYERS_DISTANCE)
    }

    private fun shouldUnrender(other: Player): Boolean {
        return other.isOffline() ||
                !other.tile.isWithinRadius(client.player.tile, RENDER_PLAYERS_DISTANCE)
    }

    companion object {

        /**
         * The distance which player entities should be rendered within this player's viewport.
         */
        private const val RENDER_PLAYERS_DISTANCE = 15
    }
}