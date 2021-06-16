package io.rsbox.engine.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

data class GameCoroutineScope(private val dispatcher: CoroutineDispatcher) : CoroutineScope by CoroutineScope(dispatcher)