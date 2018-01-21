package com.github.shynixn.blockball.api.bukkit.event;

import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import org.bukkit.entity.Player;

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
public class GoalShootEvent extends GameEvent {
    private final Player player;
    private final Team team;

    /**
     * Initializes  a new goalShootEvent
     *
     * @param game   game
     * @param player player
     * @param team   team
     */
    public GoalShootEvent(Game game, Player player, Team team) {
        super(game);
        this.player = player;
        this.team = team;
    }

    /**
     * Returns the player who scorred a goal
     *
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the team of the player
     *
     * @return player
     */
    public Team getTeam() {
        return this.team;
    }
}
