package io.rsbox.engine.service

import io.rsbox.engine.service.impl.LoginService
import org.tinylog.kotlin.Logger
import kotlin.reflect.KClass

class ServiceManager {

    private val services = mutableMapOf<KClass<out Service>, Service>()

    init {
        /*
         * Add all engine services.
         */
        register<LoginService>()
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : Service> register() {
        val klass = T::class.java as Class<Service>
        val service = klass.getDeclaredConstructor().newInstance() as Service
        services[klass.kotlin] = service
    }

    fun startServices() {
        Logger.info("Preparing to start engine services...")

        services.values.forEach { service ->
            service.start()
        }

        Logger.info("Completed starting ${services.size} engine services.")
    }

    fun stopServices() {
        Logger.info("Shutting down engine services.")

        services.values.forEach { service ->
            service.stop()
        }

        Logger.info("Completed shutdown of ${services.size} engine services.")
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Service> get(type: KClass<T>): T {
        return services[type] as T
    }
}