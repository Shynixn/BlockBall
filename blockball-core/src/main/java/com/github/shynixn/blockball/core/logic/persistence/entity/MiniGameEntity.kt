@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.Sound

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
open class MiniGameEntity(
        /**
         *  Arena of the game.
         */
        override val arena: Arena) : GameEntity(arena), MiniGame {
    /**
     * Is the lobby countdown active.
     */
    override var lobbyCountDownActive: Boolean = false
    /**
     * Actual countdown.
     */
    override var lobbyCountdown: Int = 20

    /**
     * Actual game coutndown.
     */
    override var gameCountdown: Int = 20
    /**
     * Index of the current match time.
     */
    override var matchTimeIndex: Int = 0

    /**
     * Returns if the lobby is full.
     */
    override val isLobbyFull: Boolean
        get() {
            val amount = arena.meta.redTeamMeta.maxAmount + arena.meta.blueTeamMeta.maxAmount

            if (this.ingamePlayersStorage.size >= amount) {
                return true
            }

            return false
        }


    /**
     * Returns the bling sound.
     */
    override val blingSound: Sound = SoundEntity("NOTE_PLING", 1.0, 2.0)
    /**
     * Are the players currently waiting in the lobby?
     */
    override var inLobby: Boolean = false

    /**
     * Storage for [spectatorPlayers],
     */
    override val spectatorPlayersStorage: MutableMap<Any, GameStorage> = HashMap()

    /**
     * List of players which are spectating the game.
     */
    override val spectatorPlayers: List<Any>
        get() {
            return spectatorPlayersStorage.keys.toList()
        }
}