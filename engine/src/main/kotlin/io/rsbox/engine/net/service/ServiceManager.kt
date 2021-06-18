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
    internal fun init() {
        Logger.info("Scanning for engine services...")

        val result = ClassGraph().enableAllInfo().scan().getClassesWithAnnotation(EngineService::class.java.name)
        result.forEach { cls ->
            if(cls.implementsInterface(Service::class.java.name) || cls.implementsInterface(CyclingService::class.java.name)) {
                this.loadService(cls.loadClass().kotlin)
            } else {
                return@forEach
            }
        }

        Logger.info("Successfully loaded ${services.size} engine services.")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> loadService(type: KClass<T>) {
        Logger.info("Found engine service: '${type::class.simpleName}'.")

        val service = type::class.java.getDeclaredConstructor().newInstance() as T
        services[type as KClass<out Service>] = service as Service

        /*
         * Invoke the instance being enabled.
         */
        service.onEnabled()
    }
}