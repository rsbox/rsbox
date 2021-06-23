package io.rsbox.event

import io.github.classgraph.ClassGraph
import org.tinylog.kotlin.Logger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction

object EventBus {

    private val listeners = mutableMapOf<KClass<out Event>, MutableList<Triple<EventPriority, Any, KFunction<Event>>>>()

    @Suppress("UNCHECKED_CAST")
    fun register() {
        Logger.info("Scanning for event listeners...")

        val scan = ClassGraph().enableAllInfo().scan().getClassesWithMethodAnnotation(EventListener::class.qualifiedName)
        scan.forEach { cls ->
            cls.declaredMethodInfo.forEach { method ->
                if(method.hasAnnotation(EventListener::class.qualifiedName)) {
                    val annotation = method.getAnnotationInfo(EventListener::class.qualifiedName).loadClassAndInstantiate() as EventListener
                    val event = annotation.value
                    val priority = annotation.priority

                    val kmethod = method.loadClassAndGetMethod().kotlinFunction as KFunction<Event>
                    kmethod.isAccessible = true

                    val obj = cls.loadClass().kotlin.companionObject?.createInstance()
                        ?: cls.loadClass().kotlin.objectInstance!!

                    listeners[event] = listeners.computeIfAbsent(event) { mutableListOf() }.apply { this.add(Triple(priority, obj, kmethod)) }
                }
            }
        }

        Logger.info("Registered ${listeners.size} event listeners.")
    }

    private fun triggerEvent(event: Event) {
        val eventListeners = listeners.computeIfAbsent(event::class) { mutableListOf() }
        eventListeners.sortByDescending { it.first.level }

        for(listener in eventListeners) {
            if(event.isCancelled) break
            listener.third.call(listener.second, event)
        }
    }

    fun <E : Event> event(event: E, action: (E) -> Unit) {
        /*
         * Trigger all of the event listeners.
         */
        this.triggerEvent(event)

        /*
         * If the event was not cancelled, invoke the event firing action.
         */
        if(!event.isCancelled) {
            action(event)
        }
    }
}