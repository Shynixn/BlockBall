package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.persistence.entity.BossBarMeta

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
interface BossBarService {

    /**
     * Adds the given [player] to this bossbar.
     * Does nothing if the player is already added.
     */
    fun <B, P> addPlayer(bossBar: B, player: P)

    /**
     * Removes the given [player] from this bossbar.
     * Does nothing if the player is already removed.
     */
    fun <B, P> removePlayer(bossBar: B, player: P)

    /**
     * Returns a list of all players watching thie bossbar.
     */
    fun <B, P> getPlayers(bossBar: B): List<P>

    /**
     * Changes the style of the bossbar with given [bossBarMeta].
     */
    fun <B, P> changeConfiguration(bossBar: B, title: String, bossBarMeta: BossBarMeta, player: P)

    /**
     * Generates a new bossbar from the given bossBar meta values.
     */
    fun <B> createNewBossBar(bossBarMeta: BossBarMeta): B

    /**
     * Clears all resources this [bossBar] has allocated from this service.
     */
    fun <B> cleanResources(bossBar: B)
}