package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.api.persistence.entity.HologramMeta

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
interface HologramService {

    /**
     * Generates a new hologram from the given meta values.
     */
    fun createNewHologram(hologramMeta: HologramMeta): HologramProxy

    /**
     * Changes the hologram configuration.
     */
    fun changeConfiguration(hologramProxy: HologramProxy, hologramMeta: HologramMeta)

    /**
     * Adds the given [player] to this hologram.
     * Does nothing if the player is already added.
     */
    fun <P> addPlayer(hologramProxy: HologramProxy, player: P)

    /**
     * Removes the given [player] from this hologram.
     * Does nothing if the player is already removed.
     */
    fun <P> removePlayer(hologramProxy: HologramProxy, player: P)
}