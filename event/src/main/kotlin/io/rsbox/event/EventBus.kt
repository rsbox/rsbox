package io.rsbox.event

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject

object EventBus {

    val disposables = mutableMapOf<Any, CompositeDisposable>()
    val publishSubject = PublishSubject.create<Event>()

    inline fun <T : Event> fire(event: T, action: (T) -> Unit) {
        publishSubject.onNext(event)
        if(event.isCancellable && event.isCancelled()) {
            event.onCancel()
            return
        }

        action(event)
    }

}

@Suppress("FunctionName")
inline fun <reified T : Event> Any.on_event(priority: EventPriority = EventPriority.NORMAL, noinline consumer: (T) -> Unit) {
    val event = EventBus.publishSubject.ofType(T::class.java)
    val observer = event.subscribe {
        if(!it.isCancelled()) {
            consumer(it)
        }
    }
    val disposable = EventBus.disposables[this]
        ?: CompositeDisposable().apply { EventBus.disposables[this] = this }
    disposable.add(observer)
}