package io.rsbox.event

enum class EventPriority(val priority: Int) : Comparable<EventPriority> {

    LOWEST(0),

    LOW(1),

    NORMAL(2),

    HIGH(3),

    HIGHEST(4);

}




