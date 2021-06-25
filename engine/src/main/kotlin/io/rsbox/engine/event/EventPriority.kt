package io.rsbox.engine.event

/**
 * Represents the priority level of a event listener. The listeners execute in descending order from [level]
 *
 * @property level Int
 * @constructor
 */
sealed class EventPriority(val level: Int) {
    object LOWEST : EventPriority(-2)
    object LOW : EventPriority(-1)
    object NORMAL : EventPriority(0)
    object HIGH : EventPriority(1)
    object HIGHEST : EventPriority(2)
    class CUSTOM(weight: Int) : EventPriority(weight)
}
