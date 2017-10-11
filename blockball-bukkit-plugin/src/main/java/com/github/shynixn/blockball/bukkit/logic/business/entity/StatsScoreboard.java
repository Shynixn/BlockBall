package com.github.shynixn.blockball.bukkit.logic.business.entity;

import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.lib.SimpleScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.List;

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
class StatsScoreboard extends SimpleScoreboard {

    /**
     * Initializes a fresh new Scoreboard.
     */
    StatsScoreboard(Player player) {
        super();
        this.setDefaultObjective(SimpleScoreboard.DUMMY_TYPE);
        this.setDefaultTitle(Config.getInstance().getStatsScoreboardTitle());
        this.setDefaultDisplaySlot(DisplaySlot.SIDEBAR);
        this.addPlayer(player);
    }

    /**
     * Updates the stats on the scoreboard.
     *
     * @param stats stats
     */
    void updateStats(Player player, Stats stats) {
        final List<String> lines = Config.getInstance().getStatsScoreboardLines();
        for (int i = 0, j = lines.size(); i < lines.size(); i++, j--) {
            final String line = lines.get(i)
                    .replace("<player>", player.getName())
                    .replace("<winrate>", String.format("%.2f", stats.getWinRate()))
                    .replace("<playedgames>", String.valueOf(stats.getAmountOfGamesPlayed()))
                    .replace("<goalspergame>", String.format("%.2f", stats.getGoalsPerGame()));
            this.setDefaultLine(j, line);
        }
    }
}
