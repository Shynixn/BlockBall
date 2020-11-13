@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.RightclickManageService
import com.google.inject.Inject

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
class RightclickManageServiceImpl @Inject constructor(private val proxyService: ProxyService) : RightclickManageService {
    private val rightClickListener = HashMap<Any, (Any) -> Unit>()

    /**
     * Gets called one time when a location gets rightlicked by [player].
     */
    override fun <P, L> watchForNextRightClickSign(player: P, f: (L) -> Unit) {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val func = f as (Any) -> Unit
        rightClickListener[player] = func
    }

    /**
     * Executes the watcher for the given [player] if he has registered one.
     */
    override fun <P, L> executeWatchers(player: P, location: L): Boolean {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!rightClickListener.containsKey(player)) {
            return false
        }

        if (proxyService.setSignLines(location, listOf(ChatColor.BOLD.toString() + "BlockBall", ChatColor.GREEN.toString() + "Loading..."))) {
            rightClickListener[player]!!.invoke(location as Any)
            rightClickListener.remove(player)
        }

        return true
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    override fun <P> cleanResources(player: P) {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (rightClickListener.containsKey(player)) {
            rightClickListener.remove(player)
        }
    }
}