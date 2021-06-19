package io.rsbox.engine.net.service

import io.github.classgraph.ClassGraph
import org.tinylog.kotlin.Logger
import kotlin.reflect.KClass

class ServiceManager {

    /**
     * Backing storage map of loaded engine services.
     */
    val services = hashMapOf<KClass<out Service>, Service>()

    /**
     * Gets a service instance which has been loaded by this service manager.
     * @return T
     */
    inline fun <reified T : Service> get(): T {
        return services[T::class] as T? ?: throw IllegalArgumentException("Unknown or unloaded service: ${T::class.java.simpleName}")
    }

    /**
     * Scan for any service annotated classes and load them into this service manager instance.
     */
    @Suppress("UNCHECKED_CAST")
    internal fun init() {
        Logger.info("Scanning for engine services...")

        val result = ClassGraph().enableAllInfo().scan().getClassesWithAnnotation(EngineService::class.java.name)
        result.forEach { cls ->
            if(cls.implementsInterface(Service::class.java.name) || cls.implementsInterface(CyclingService::class.java.name)) {
                val klass = cls.loadClass() as Class<Service>
                val service = cls.loadClass().getDeclaredConstructor().newInstance() as Service
                services[klass.kotlin] = service
                service.onEnabled()
            } else {
                return@forEach
            }
        }

        Logger.info("Successfully loaded ${services.size} engine services.")
    }
}