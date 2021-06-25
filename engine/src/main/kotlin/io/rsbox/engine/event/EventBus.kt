@file:Suppress("FunctionName")

package io.rsbox.engine.event

import com.google.common.collect.MultimapBuilder
import kotlin.reflect.KClass

val listenerMap = MultimapBuilder.hashKeys().arrayListValues().build<KClass<out Event>, Pair<EventPriority, (Event) -> Unit>>()

fun <E : Event> event(event: E, action: (E) -> Unit) {
    val listeners = listenerMap[event::class].toMutableList()
    listeners.sortByDescending { it.first.level }

    listeners.forEach { listener ->
        if(event.isCancelled()) {
            return@forEach
        }

        listener.second.invoke(event)
    }

    if(!event.isCancelled()) {
        action(event)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified E : Event> on_event(priority: EventPriority = EventPriority.NORMAL, noinline action: (E) -> Unit) {
    listenerMap.put(E::class, priority to (action as (Event) -> Unit))
}