package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta
import com.github.shynixn.blockball.api.persistence.entity.Stats

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
class StatsEntity(override val playerMeta: PlayerMeta) : Stats {
    /**
     * Database id.
     */
    override var id: Long = 0L
    /** [amountOfGoals] the player has shot. */
    @YamlSerialize(value = "amount-goals", orderNumber = 4)
    override var amountOfGoals: Int = 0
    /** [amountOfPlayedGames] of the player. */
    @YamlSerialize(value = "amount-games", orderNumber = 3)
    override var amountOfPlayedGames: Int = 0
    /** [amountOfWins] of the player. */
    @YamlSerialize(value = "amount-wins", orderNumber = 2)
    override var amountOfWins: Int = 0
    /** [winRate] of the player. */
    override val winRate: Double
        get() {
            if (this.amountOfPlayedGames == 0)
                return 0.0
            return (this.amountOfWins.toDouble()) / (this.amountOfPlayedGames.toDouble())
        }
    /** [goalsPerGame] of the player. */
    override val goalsPerGame: Double
        get() {
            if (this.amountOfPlayedGames == 0)
                return 0.0
            return (this.amountOfGoals.toDouble()) / (this.amountOfPlayedGames.toDouble())
        }
}