package io.rsbox.event

abstract class Event(val isCancellable: Boolean = false) {

    var isCancelled: Boolean = false
        private set

    fun cancel() {
        if(!isCancellable) return
        this.isCancelled = true
        this.onCancel()
    }

    open fun onCancel() {
        /*
         * Do nothing by default.
         */
    }
}