package io.rsbox.engine.net.service.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.engine.net.login.LoginRequest
import io.rsbox.engine.net.service.EngineService
import io.rsbox.engine.net.service.Service
import org.tinylog.kotlin.Logger
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

@EngineService
class LoginService : Service {

    private val loginExecutor = Executors.newFixedThreadPool(LOGIN_THREADS, ThreadFactoryBuilder()
        .setNameFormat("login-worker")
        .setUncaughtExceptionHandler { t, e -> Logger.error(e) { "An error occured"} }
        .build()
    )

    val normalLoginQueue = LinkedBlockingQueue<LoginRequest.Normal>()

    override fun onEnabled() {
        this.start()
    }

    private fun start() {
        Logger.info("Starting login service...")

        repeat(LOGIN_THREADS) {
            loginExecutor.execute(LoginWorker(this))
        }

        Logger.info("Login processing workers running on $LOGIN_THREADS threads.")
    }

    /**
     * Represents a single login request worker which processes the login request.
     *
     * @property service LoginService
     * @constructor
     */
    private class LoginWorker(private val service: LoginService) : Runnable {

        override fun run() {
            while(true) {
                val loginRequest = service.normalLoginQueue.take()
                println("Received login request for: ${loginRequest.username}")
            }
        }
    }

    companion object {
        private const val LOGIN_THREADS = 4
    }
}