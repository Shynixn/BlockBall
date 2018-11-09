package com.github.shynixn.blockball.bukkit.logic.business.coroutine

import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
object DispatcherContainer {
    private var asyncCoroutine: CoroutineContext? = null
    private var syncCoroutine: CoroutineContext? = null

    /**
     * Gets the async coroutine context.
     */
    val async: CoroutineContext
        get() {
            if (asyncCoroutine == null) {
                asyncCoroutine = AsyncCoroutineDispatcher(JavaPlugin.getPlugin(BlockBallPlugin::class.java))
            }

            return asyncCoroutine!!
        }

    /**
     * Gets the sync coroutine context.
     */
    val sync: CoroutineContext
        get() {
            if (syncCoroutine == null) {
                syncCoroutine = MinecraftCoroutineDispatcher(JavaPlugin.getPlugin(BlockBallPlugin::class.java))
            }

            return syncCoroutine!!
        }
}