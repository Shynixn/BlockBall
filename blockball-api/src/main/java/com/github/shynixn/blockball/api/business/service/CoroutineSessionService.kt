package com.github.shynixn.blockball.api.business.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface CoroutineSessionService {
    /**
     * Launch.
     */
    fun launch(
        dispatcher: CoroutineContext = minecraftDispatcher,
        f: suspend CoroutineScope.() -> Unit
    ): Job

    /**
     * LaunchAsync.
     */
    fun launchAsync(f: suspend CoroutineScope.() -> Unit): Job

    /**
     * Minecraft.
     */
    val minecraftDispatcher: CoroutineContext

    /**
     * Async.
     */
    val asyncDispatcher: CoroutineContext
}
