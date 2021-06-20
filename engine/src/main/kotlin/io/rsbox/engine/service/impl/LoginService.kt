package io.rsbox.engine.service.impl

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.engine.net.login.LoginRequest
import io.rsbox.engine.service.Service
import org.tinylog.kotlin.Logger
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class LoginService : Service {

    private val executor = Executors.newFixedThreadPool(
        LOGIN_THREADS,
        ThreadFactoryBuilder()
            .setNameFormat("login-worker")
            .build()
        )

    private val loginRequestQueue = LinkedBlockingQueue<LoginRequest>()

    override fun start() {
        Logger.info("Starting login service on $LOGIN_THREADS threads...")

        /*
         * Start a login worker for each available login-worker threads.
         */
        repeat(LOGIN_THREADS) {
            executor.execute {
                while(true) {
                    val loginRequest = loginRequestQueue.take()
                    println("Received login request.")
                }
            }
        }
    }

    override fun stop() {
        Logger.info("Stopping login service...")
        executor.shutdown()
    }

    fun queue(request: LoginRequest) {
        loginRequestQueue.add(request)
    }

    companion object {
        private const val LOGIN_THREADS = 4
    }
}