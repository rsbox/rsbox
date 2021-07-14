package io.rsbox.engine.model.entity.update

class UpdateStates {

    /**
     * A bit packed value of all the updates for a given game cycle.
     */
    var state: Int = 0
        private set

    fun hasUpdates(): Boolean = state != 0

    fun add(mask: Int) {
        state = state or mask
    }

    fun hasUpdate(mask: Int): Boolean {
        return (state and mask) != 0
    }

    fun reset() {
        state = 0
    }

}