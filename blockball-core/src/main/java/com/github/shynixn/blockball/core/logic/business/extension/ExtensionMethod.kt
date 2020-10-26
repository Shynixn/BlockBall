@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.extension

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.executor.CommandExecutor
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.LoggingService
import java.lang.reflect.Field
import java.util.concurrent.CompletableFuture

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
/**
 * Executes the given [f] via the [concurrencyService] synchronized with the server tick.
 */
inline fun sync(
    concurrencyService: ConcurrencyService,
    delayTicks: Long = 0L,
    repeatingTicks: Long = 0L,
    crossinline f: () -> Unit
) {
    concurrencyService.runTaskSync(delayTicks, repeatingTicks) {
        f.invoke()
    }
}

/**
 * Accepts the action safely.
 */
fun <T> CompletableFuture<T>.thenAcceptSafely(f: (T) -> Unit) {
    this.thenAccept(f).exceptionally { e ->
        BlockBallApi.resolve(LoggingService::class.java).error("Failed to execute Task.", e)
        throw RuntimeException(e)
    }
}

/**
 * Translates the given chatColor.
 */
fun String.translateChatColors(): String {
    return ChatColor.translateChatColorCodes('&', this)
}

/**
 * Strips the chat colors from the string.
 */
fun String.stripChatColors(): String {
    return ChatColor.stripChatColors(this)
}

/**
 * Accessible field.
 */
fun Field.accessible(flag: Boolean): Field {
    this.isAccessible = flag
    return this
}

/**
 * Merges arguments starting from [starting] to [amount] from the given [args].
 */
fun CommandExecutor.mergeArgs(starting: Int, amount: Int, args: Array<out String>): String {
    val builder = StringBuilder()
    var counter = 0
    var i = starting

    while (counter < amount) {
        if (builder.isNotEmpty()) {
            builder.append(' ')
        }

        if (i < args.size) {
            builder.append(args[i].stripChatColors())
        }

        counter++
        i++
    }

    return builder.toString()
}

/**
 * Casts any instance to any type.
 */
fun <T> Any?.cast(): T {
    return this as T
}

/**
 * Executes the given [f] via the [concurrencyService] asynchronous.
 */
inline fun async(
    concurrencyService: ConcurrencyService,
    delayTicks: Long = 0L,
    repeatingTicks: Long = 0L,
    crossinline f: () -> Unit
) {
    concurrencyService.runTaskAsync(delayTicks, repeatingTicks) {
        f.invoke()
    }
}
