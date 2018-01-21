package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.bukkit.event.GameJoinEvent;
import com.github.shynixn.blockball.api.bukkit.event.GameWinEvent;
import com.github.shynixn.blockball.api.bukkit.event.GoalShootEvent;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.controller.PlayerMetaController;
import com.github.shynixn.blockball.api.persistence.controller.StatsController;
import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.bukkit.logic.Factory;
import com.github.shynixn.blockball.bukkit.logic.business.entity.StatsScoreboard;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.stats.StatsData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
public class StatsListener extends SimpleListener implements Runnable {

    private final Map<Player, StatsScoreboard> statsScoreboards = new HashMap<>();
    private final StatsController statsController;
    private final PlayerMetaController playerMetaController;

    /**
     * Initializes a new listener by plugin.
     *
     * @param plugin plugin
     */
    public StatsListener(Plugin plugin) {
        super(plugin);
        this.statsController = Factory.createStatsController();
        this.playerMetaController = Factory.createPlayerDataController();
        this.setStatsForAllOnlinePlayers();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0, 20L * 60);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (final Player player : this.statsScoreboards.keySet()) {
            final Optional<Stats> optStats = StatsListener.this.statsController.getByPlayer(player);
            optStats.ifPresent(stats -> this.updateStats(player, stats));
        }
    }

    /**
     * Sets the stats scoreboard for a player when he joins the server.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        this.setStatsForPlayer(event.getPlayer());
    }

    /**
     * Removes the stats of a player.
     *
     * @param event event
     */
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) throws Exception {
        if (this.statsScoreboards.containsKey(event.getPlayer())) {
            final StatsScoreboard scoreboard = this.statsScoreboards.get(event.getPlayer());
            this.statsScoreboards.remove(event.getPlayer());
            scoreboard.close();
        }
    }

    /**
     * Updates the goals of a player when he shoots a goal.
     * @param event event
     */
    @EventHandler
    public void onPlayerShootGoalEvent(GoalShootEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                final Optional<Stats> optStats = StatsListener.this.statsController.getByPlayer(event.getPlayer());
                if (optStats.isPresent()) {
                    final Stats stats = optStats.get();
                    stats.setAmountOfGoals(stats.getAmountOfGoals() + 1);
                    this.updateStats(event.getPlayer(), stats);
                    this.statsController.store(stats);
                }
            }
        });
    }

    /**
     * Gets called when a player joins the match
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerJoinGameEvent(GameJoinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                final Optional<Stats> optStats = StatsListener.this.statsController.getByPlayer(event.getPlayer());
                if (optStats.isPresent()) {
                    final Stats stats = optStats.get();
                    stats.setAmountOfGamesPlayed(stats.getAmountOfGamesPlayed() + 1);
                    this.statsController.store(stats);
                    this.updateStats(event.getPlayer(), stats);
                }
            }
        });
    }

    /**
     * Gets called when a game gets won.
     *
     * @param event event
     */
    @EventHandler
    public void onTeamWinEvent(GameWinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                Object[] winningPlayers = event.getGame().getRedTeamPlayers();
                if (event.getTeam() == Team.BLUE) {
                    winningPlayers = event.getGame().getBlueTeamPlayers();
                }
                for (final Object player : winningPlayers) {
                    final Optional<Stats> optStats = StatsListener.this.statsController.getByPlayer(player);
                    if (optStats.isPresent()) {
                        final Stats stats = optStats.get();
                        stats.setAmountOfWins(stats.getAmountOfWins() + 1);
                        this.statsController.store(stats);
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.updateStats((Player) player, stats), 40L);
                    }
                }
            }
        });
    }

    private void updateStats(Player player, Stats stats) {
        if (this.statsScoreboards != null) {
            this.statsScoreboards.get(player).updateStats(player, stats);
        }
    }

    private void setStatsForPlayer(Player player) {
        final StatsScoreboard scoreboard = new StatsScoreboard(player);
        this.statsScoreboards.put(player, scoreboard);
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                final Optional<Stats> optStats = StatsListener.this.statsController.getByPlayer(player);
                final Stats stats;
                if (!optStats.isPresent()) {
                    final Optional<PlayerMeta> optPlayerMeta = this.playerMetaController.getByUUID(player.getUniqueId());
                    final PlayerMeta meta;
                    if (!optPlayerMeta.isPresent()) {
                        meta = this.playerMetaController.create(player);
                        this.playerMetaController.store(meta);
                    } else {
                        meta = optPlayerMeta.get();
                    }
                    stats = this.statsController.create();
                    ((StatsData) stats).setPlayerId(meta.getId());
                    this.statsController.store(stats);
                } else {
                    stats = optStats.get();
                }
                scoreboard.updateStats(player, stats);
            }
        }, 20 * 2L);
    }

    private void setStatsForAllOnlinePlayers() {
        for (final World world : Bukkit.getWorlds()) {
            for (final Player player : world.getPlayers()) {
                this.setStatsForPlayer(player);
            }
        }
    }
}

