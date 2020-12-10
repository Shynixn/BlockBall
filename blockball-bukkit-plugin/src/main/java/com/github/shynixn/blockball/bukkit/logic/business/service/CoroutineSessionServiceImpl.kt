package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.CoroutineSessionService
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.minecraftDispatcher
import com.google.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bukkit.plugin.Plugin
import kotlin.coroutines.CoroutineContext

class CoroutineSessionServiceImpl @Inject constructor(private val plugin: Plugin) : CoroutineSessionService {
    /**
     * Launch.
     */
    override fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit): Job {
        return plugin.launch(dispatcher, f)
    }

    /**
     * LaunchAsync.
     */
    override fun launchAsync(f: suspend CoroutineScope.() -> Unit): Job {
        return plugin.launchAsync(f)
    }

    /**
     * Minecraft.
     */
    override val minecraftDispatcher: CoroutineContext
        get() = plugin.minecraftDispatcher

    /**
     * Async.
     */
    override val asyncDispatcher: CoroutineContext
        get() = plugin.asyncDispatcher
}
