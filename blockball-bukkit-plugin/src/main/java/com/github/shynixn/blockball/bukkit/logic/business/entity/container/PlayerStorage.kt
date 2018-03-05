package com.github.shynixn.blockball.bukkit.logic.business.entity.container

import com.github.shynixn.blockball.api.business.entity.InGameStats
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Scoreboard

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
class PlayerStorage(
        /** Player of the storage. */
        val player: Player) : InGameStats {

    private var level: Int = 0
    private var exp: Float = 0.0F
    private var maxHealth: Double = 0.0
    private var health: Double = 0.0
    private var hunger: Int = 0
    private var inventoryContents: Array<ItemStack>? = null
    private var armorContents: Array<ItemStack>? = null
    private var flying: Boolean = false
    private var allowFlying: Boolean = false
    private var walkingSpeed: Float = 0.toFloat()
    private var scoreboard: Scoreboard? = null
    private var gamemode : GameMode? = null

    /** Team of the player. */
    override var team: Team? = null
    private var gameType: GameType = GameType.HUBGAME;

    /**
     * Stores the metadata for a [player] joining a hubgame.
     */
    fun storeForType(gameType: GameType) {
        this.gameType = gameType;
        if (gameType == GameType.HUBGAME) {
            this.gamemode = player.gameMode
            this.armorContents = player.inventory?.armorContents?.clone()
            this.flying = player.isFlying
            this.allowFlying = player.allowFlight
            this.walkingSpeed = player.walkSpeed
            this.scoreboard = player.scoreboard
        } else if (gameType == GameType.MINIGAME) {
            this.gamemode = player.gameMode
            this.level = player.level
            this.exp = player.exp
            this.maxHealth = player.maxHealth
            this.health = player.health
            this.hunger = player.foodLevel
            this.inventoryContents = player.inventory?.contents?.clone()
            this.armorContents = player.inventory?.armorContents?.clone()
            this.flying = player.isFlying
            this.allowFlying = player.allowFlight
            this.walkingSpeed = player.walkSpeed
            this.scoreboard = player.scoreboard
        }
    }

    /** Resets the players state before joining. */
    override fun resetState() {
        if (gameType == GameType.HUBGAME) {
            player.gameMode = gamemode
            player.inventory.armorContents = this.armorContents
            player.allowFlight = this.allowFlying
            player.isFlying = this.flying
            player.walkSpeed = this.walkingSpeed
            player.scoreboard = this.scoreboard
        } else if (gameType == GameType.MINIGAME) {
            player.gameMode = gamemode
            player.level = this.level
            player.exp = this.exp
            player.maxHealth = maxHealth
            player.health = health
            player.foodLevel = hunger
            player.inventory.contents = this.inventoryContents
            player.inventory.armorContents = this.armorContents
            player.allowFlight = this.allowFlying
            player.isFlying = this.flying
            player.walkSpeed = this.walkingSpeed
            player.scoreboard = this.scoreboard
        }
    }
}