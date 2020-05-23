package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import java.util.*

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
class GameStorageEntity(override var uuid: UUID) : GameStorage {
    /**
     * Scoreboard of the player.
     */
    override var scoreboard: Any = ""

    /** Team of the player. */
    override var team: Team? = null

    /**
     * Team of the goal which may or may not be the same of the team depending on the swapping state.
     */
    override var goalTeam: Team? = null

    /**
     * Exp level of the player.
     */
    override var level: Int = 0

    /**
     * Actual exp of the player.
     */
    override var exp: Double = 0.0

    /**
     * Max health of the player.
     */
    override var maxHealth: Double = 20.0

    /**
     * Health of the player.
     */
    override var health: Double = 20.0

    /**
     * Hunger of the player.
     */
    override var hunger: Int = 10

    /**
     * Storage belongs to this [GameType].
     */
    override var gameType: GameType = GameType.BUNGEE

    /**
     * Gamemode of the player.
     */
    override var gameMode: GameMode = GameMode.SURVIVAL

    /**
     * Walking Speed of the player.
     */
    override var walkingSpeed: Double = 1.0

    /**
     * Was the player flying?
     */
    override var flying: Boolean = false

    /**
     * Was the player allowed to fly?
     */
    override var allowedFlying: Boolean = false

    /**
     * Inventory cache.
     */
    override var inventoryContents: Array<Any?> = arrayOfNulls(0)

    /**
     * Inventory armor cache.
     */
    override var armorContents: Array<Any?> = arrayOfNulls(0)
}