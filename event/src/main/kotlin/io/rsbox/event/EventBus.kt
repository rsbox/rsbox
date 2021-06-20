package io.rsbox.event

import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

/**
 * The backing event bus system which handles the listening and firing of engine events
 * through the reactive rxjava3 library.
 *
 * This event bus is NOT async and runs on the main thread. It has to avoid thread conflicts.
 */

val eventSubjects = TreeMap <EventPriority, Subject<Event>>()
val eventHistoryMap = hashMapOf<Class<Event>, Event>()
val eventListenerMap = TreeMap<EventPriority, Int>()

@DslMarker
annotation class ListenerDsl

@DslMarker
annotation class TriggerDsl

@ListenerDsl
@Suppress("FunctionName")
inline fun <reified T : Event> on_event(priority: EventPriority = EventPriority.NORMAL, noinline action: (T) -> Unit) {
    val observable = synchronized(io.rsbox.event.eventSubjects) {
        (io.rsbox.event.eventSubjects[priority] ?: PublishSubject.create<Event?>().toSerialized().also {
            io.rsbox.event.eventSubjects[priority] = it
        }).ofType(T::class.java)
    }

    val disposable = synchronized(io.rsbox.event.eventHistoryMap) {
        io.rsbox.event.eventHistoryMap.filter { T::class.java.isAssignableFrom(it.key) }
            .toSortedMap { a, b ->
                if(a.isAssignableFrom(b)) 1 else -1
            }.map { it.value }
            .fold(observable) { observable, lastEvent ->
                observable.mergeWith(ObservableSource { observer ->
                    observer.onNext(T::class.java.cast(lastEvent))
                })
            }
    }.doOnSubscribe {
        synchronized(io.rsbox.event.eventSubjects) {
            io.rsbox.event.eventListenerMap[priority] = io.rsbox.event.eventListenerMap[priority]?.let { count -> count + 1 } ?: 1
        }
    }.doOnDispose {
        synchronized(io.rsbox.event.eventSubjects) {
            io.rsbox.event.eventListenerMap[priority] = io.rsbox.event.eventListenerMap[priority]?.let { count -> count - 1 } ?: 0
            if(io.rsbox.event.eventListenerMap[priority] == 0) {
                io.rsbox.event.eventSubjects.remove(priority)
                io.rsbox.event.eventListenerMap.remove(priority)
            }
        }
    }

    disposable.subscribe { event ->
        if(!event.isCancelled()) {
            action(event)
        }
    }
}

@TriggerDsl
@Suppress("UNCHECKED_CAST")
fun <T : Event> fire_event(event: T, action: (T) -> Unit) {
    var cancelled = false

    synchronized(io.rsbox.event.eventHistoryMap) {
        io.rsbox.event.eventHistoryMap[event::class.java as Class<Event>] = event
    }

    synchronized(io.rsbox.event.eventSubjects) {
        io.rsbox.event.eventSubjects.descendingMap().toMap().forEach {
            it.value.onNext(event)
            if(!cancelled && (event.isCancellable && event.isCancelled())) {
                event.onCancel()
                cancelled = true
            }
        }
    }

    if(!cancelled) {
        action(event)
    }
}

