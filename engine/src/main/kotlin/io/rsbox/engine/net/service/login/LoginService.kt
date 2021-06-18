package io.rsbox.engine.net.service.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.rsbox.engine.net.login.LoginRequest
import io.rsbox.engine.net.service.EngineService
import io.rsbox.engine.net.service.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.tinylog.kotlin.Logger
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@EngineService
class LoginService : Service {

    val loginCoroutineScope = CoroutineScope(Executors.newFixedThreadPool(4, ThreadFactoryBuilder()
        .setNameFormat("login-thread")
        .setDaemon(false)
        .build()
    ).asCoroutineDispatcher())

    val normalLoginQueue = ConcurrentLinkedDeque<LoginRequest.Normal>()

    override fun onEnabled() {
        this.start()
    }

    private fun start() {
        Logger.info("Starting login service...")


    }
}