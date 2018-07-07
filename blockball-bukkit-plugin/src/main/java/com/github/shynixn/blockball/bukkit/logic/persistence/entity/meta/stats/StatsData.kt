package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.stats

import com.github.shynixn.blockball.api.persistence.entity.meta.stats.Stats
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject

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
class StatsData : PersistenceObject(), Stats {
    /** [amountOfGoals] the player has shot. */
    @YamlSerializer.YamlSerialize(value = "amount-goals", orderNumber = 4)
    override var amountOfGoals: Int = 0
    /** [amountOfPlayedGames] of the player. */
    @YamlSerializer.YamlSerialize(value = "amount-games", orderNumber = 3)
    override var amountOfPlayedGames: Int = 0
    /** [amountOfWins] of the player. */
    @YamlSerializer.YamlSerialize(value = "amount-wins", orderNumber = 2)
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

    /**
     * Id reference to the player in the database.
     */
    @YamlSerializer.YamlSerialize(value = "playerId", orderNumber = 1)
    var playerId: Long = 0
}