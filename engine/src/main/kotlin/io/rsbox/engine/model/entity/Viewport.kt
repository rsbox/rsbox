package io.rsbox.engine.model.entity

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
     * Initializes the client's viewport GPI. This is done when the player logs in.
     */
    fun initialize() {
        this.reset()

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
        }
    }

    fun reset() {
        repeat(2048) { gpiLocalPlayers[it] = null }
        repeat(2048) { gpiLocalPlayerIndexes[it] = 0 }
        gpiLocalPlayerCount = 0
        repeat(2048) { gpiExternalPlayerIndexes[it] = 0 }
        gpiExternalPlayerCount = 0
        repeat(2048) { gpiPlayerSkipFlags[it] = 0 }
    }


}