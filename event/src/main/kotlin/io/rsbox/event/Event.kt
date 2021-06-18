package io.rsbox.event

/**
 * Represents a abstract engine event.
 *
 * @property isCancellable Boolean
 * @constructor
 */
abstract class Event(val isCancellable: Boolean = false) {

    private var eventCancelled: Boolean = false

    /**
     * Gets whether the current event has been cancelled by a subscriber.
     *
     * @return Boolean
     */
    fun isCancelled(): Boolean = this.eventCancelled

    /**
     * The logic invoked when this event is cancelled.
     */
    open fun onCancel() {
        /*
         * Do nothing
         */
    }

    /**
     * Cancels an event's engine action.
     */
    fun cancel() {
        if(!isCancellable) return
        eventCancelled = true
    }
}