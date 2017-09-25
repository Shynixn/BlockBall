package com.github.shynixn.blockball.bukkit.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.bungeecord.game.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

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
class BungeeGameEntity extends HelperGameEntity {
    /**
     * Initializes a new bungee game
     *
     * @param arena arena
     */
    BungeeGameEntity(Arena arena) {
        super(arena);
        BungeeCord.setModt(BungeeCord.MOD_WAITING_FOR_PLAYERS);
    }

    /**
     * Starts the game
     */
    @Override
    public void startGame() {
        BungeeCord.setModt(BungeeCord.MOD_INGAME);
        super.startGame();
    }

    /**
     * Lets a player leave the game
     *
     * @param player player
     * @return success
     */
    @Override
    public synchronized boolean leave(Player player) {
        final boolean success = super.leave(player);
        player.kickPlayer(this.arena.getTeamMeta().getLeaveMessage());
        return success;
    }

    /**
     * Resets the game and restarts the server
     */
    @Override
    public void reset() {
        BungeeCord.setModt(BungeeCord.MOD_RESTARTING);
        super.reset();
        if (this.arena.isEnabled()) {
            this.restartServer();
        }
    }

    /**
     * Restarts the server
     */
    private void restartServer() {
        try {
            Bukkit.getServer().shutdown();
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.INFO, "Failed shutdown server.", ex);
        }
    }
}
