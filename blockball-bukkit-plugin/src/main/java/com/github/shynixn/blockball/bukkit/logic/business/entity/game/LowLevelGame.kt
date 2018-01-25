package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitGame
import com.github.shynixn.blockball.api.business.entity.Ball
import com.github.shynixn.blockball.api.business.entity.Game
import com.github.shynixn.blockball.api.business.entity.InGameStats
import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
abstract class LowLevelGame(
        /** Arena of the game. */
        override val arena: BukkitArena) : BukkitGame, Runnable {
    /** Ingame stats of the players. */
    override val ingameStats: MutableMap<Player, InGameStats> = HashMap()
    /** Ball of the game. */
    override var ball: Ball? = null
    /** Status of the game. */
    override var status: GameStatus = GameStatus.ENABLED
    /** List of players in the redTeam. */
    override val redTeam: MutableList<Player> = ArrayList()
    /** List of players in the blueTeam. */
    override val blueTeam: MutableList<Player> = ArrayList()

    /** Checks if the player has joined the game. */
    override fun hasJoined(player: Player): Boolean {
        return ingameStats.containsKey(player)
    }

    /** Returns all players inside of the game. */
    override fun getPlayers(): List<Player> {
        val list = ArrayList<Player>()
        list.addAll(redTeam)
        list.addAll(blueTeam)
        return list
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        ingameStats.keys.forEach { p -> leave(p); }
        ingameStats.clear()
        ball?.remove()
        redTeam.clear()
        blueTeam.clear()
    }
}