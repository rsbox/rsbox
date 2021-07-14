package io.rsbox.engine.model.entity.update

class UpdateContainer {

    /**
     * The object holding stateful information for the updates to use and reset.
     */
    val state: UpdateState = UpdateState(this)

    /**
     * A bit packed value of all the updates for a given game cycle.
     */
    var mask: Int = 0
        private set

    fun hasUpdates(): Boolean = mask != 0

    fun addUpdate(mask: Int) {
        this.mask = this.mask or mask
    }

    fun addUpdate(type: UpdateType) {
        addUpdate(type.mask)
    }

    fun hasUpdate(mask: Int): Boolean {
        return (this.mask and mask) != 0
    }

    fun clear() {
        mask = 0
        state.teleported = false
    }

}