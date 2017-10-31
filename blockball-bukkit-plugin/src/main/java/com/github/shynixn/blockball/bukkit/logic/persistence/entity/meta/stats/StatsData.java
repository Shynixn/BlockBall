package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.stats;

import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.lib.YamlSerializer;
import org.bukkit.entity.Entity;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public class StatsData extends PersistenceObject<Stats> implements Stats {

    @YamlSerializer.YamlSerialize(value = "playerId", orderNumber = 1)
    private long playerId;
    @YamlSerializer.YamlSerialize(value = "amount-wins", orderNumber = 2)
    private int amountWins;
    @YamlSerializer.YamlSerialize(value = "amount-games", orderNumber = 3)
    private int amountGames;
    @YamlSerializer.YamlSerialize(value = "amount-goals", orderNumber = 4)
    private int amountGoals;

    /**
     * Returns the winRate of the player
     *
     * @return winRate
     */
    @Override
    public double getWinRate() {
        if (this.amountGames == 0)
            return 0;
        return ((double) this.amountWins) / ((double) this.amountGames);
    }

    /**
     * Returns the goals Per Game
     *
     * @return goalsPer Game
     */
    @Override
    public double getGoalsPerGame() {
        if (this.amountGames == 0)
            return 0;
        return ((double) this.amountGoals) / ((double) this.amountGames);
    }

    /**
     * Sets the amount of wins of the player
     *
     * @param amount amount
     */
    @Override
    public void setAmountOfWins(int amount) {
        this.amountWins = amount;
    }

    /**
     * Returns the amount of wins of the player
     *
     * @return amount amount
     */
    @Override
    public int getAmountOfWins() {
        return this.amountWins;
    }

    /**
     * Sets the amount of games played by the player
     *
     * @param amount amount
     */
    @Override
    public void setAmountOfGamesPlayed(int amount) {
        this.amountGames = amount;
    }

    /**
     * Returns the amount of games player by the player
     *
     * @return amount of game played
     */
    @Override
    public int getAmountOfGamesPlayed() {
        return this.amountGames;
    }

    /**
     * Sets the amount of goals of the player
     *
     * @param amount amount
     */
    @Override
    public void setAmountOfGoals(int amount) {
        this.amountGoals = amount;
    }

    /**
     * Returns the amount of goals of the player
     *
     * @return player
     */
    @Override
    public int getAmountOfGoals() {
        return this.amountGoals;
    }

    /**
     * Returns the playerId
     *
     * @return playerId
     */
    public long getPlayerId() {
        return this.playerId;
    }

    /**
     * Sets the playerId
     *
     * @param id id
     */
    public void setPlayerId(long id) {
        this.playerId = id;
    }
}
