package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Location
import org.bukkit.entity.Player

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
abstract class Page(
        /**
         * Page id.
         */
        val id: Int, private val previousId: Int) {
    /**
     * GetPrevious id.
     */
    open fun getPreviousIdFrom(cache: Array<Any?>): Int {
        return previousId
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    abstract fun getCommandKey(): PageKey

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    open fun buildPage(cache: Array<Any?>): ChatBuilder? {
        return null
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    open fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        return CommandResult.SUCCESS
    }

    internal fun mergeArgs(starting: Int, args: Array<String>): String {
        val builder = StringBuilder()
        for (i in starting until args.size) {
            if (builder.isNotEmpty()) {
                builder.append(' ')
            }
            builder.append(args[i])
        }
        return builder.toString()
    }

    internal fun printLocation(mlocation: Any): String {
        val location: Location = if (mlocation is Position) {
            (mlocation as PositionEntity).toLocation()
        } else {
            mlocation as Location
        }

        return location.world.name + " " + location.blockX + "x " + location.blockY + "y " + location.blockZ + "z"
    }
}