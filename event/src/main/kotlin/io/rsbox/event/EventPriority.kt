package io.rsbox.event

enum class EventPriority(val level: Int) : Comparable<EventPriority> {

    LOWEST(-2),

    LOW(-1),

    NORMAL(0),

    HIGH(1),

    HIGHEST(2);

}




