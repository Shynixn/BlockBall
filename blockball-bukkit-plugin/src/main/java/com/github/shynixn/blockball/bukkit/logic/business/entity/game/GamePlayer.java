package com.github.shynixn.blockball.bukkit.logic.business.entity.game;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class GamePlayer {

    public Player player;
    public Team team;

    public ItemStack[] armorContents;
    public boolean flying;
    public boolean allowFlying;
    public float walkingSpeed;
    public Scoreboard scoreboard;

    public void storeForHubGame() {
        this.armorContents = this.player.getInventory().getArmorContents().clone();
        this.flying = this.player.isFlying();
        this.allowFlying = this.player.getAllowFlight();
        this.walkingSpeed = this.player.getWalkSpeed();
        this.scoreboard = this.player.getScoreboard();
    }

    public void restoreFromHubGame() {
        this.player.getInventory().setArmorContents(this.armorContents);
        this.player.setFlying(this.flying);
        this.player.setAllowFlight(this.allowFlying);
        this.player.setWalkSpeed(this.walkingSpeed);
        this.player.setScoreboard(this.scoreboard);
    }

    public GamePlayer(Player player) {
        this.player = player;
    }
}
