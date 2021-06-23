package io.rsbox.event

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventListener(val value: KClass<out Event>, val priority: EventPriority = EventPriority.NORMAL)
