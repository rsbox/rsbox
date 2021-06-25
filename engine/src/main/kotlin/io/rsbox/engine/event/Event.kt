package io.rsbox.engine.event

abstract class Event(private var isCancellable: Boolean) {

    private var cancelled: Boolean = false

    fun isCancelled(): Boolean = cancelled

    fun cancel() {
        this.cancelled = true
        this.onCancel()
    }

    open fun onCancel() {
        /*
         * Do nothing.
         */
    }
}