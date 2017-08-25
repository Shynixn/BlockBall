package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.lib.SimpleScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.Collections;

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
class GameScoreboard extends SimpleScoreboard {

    /**
     * Initializes a fresh new Scoreboard
     */
    GameScoreboard(Arena arena) {
        super();
        this.setDefaultObjective(SimpleScoreboard.DUMMY_TYPE);
        this.setDefaultTitle(arena.getTeamMeta().getScoreboardTitle());
        this.setDefaultDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Updates the scoreboard for all added players
     *
     * @param gameEntity gameEntity
     */
    void update(GameEntity gameEntity) {
        final String[] lines = gameEntity.getArena().getTeamMeta().getScoreboardLines();
        for (int i = 0, j = lines.length; i < lines.length; i++, j--) {
            final String line = lines[i];
            this.setDefaultLine(j, gameEntity.decryptText(line));
        }
    }
}
