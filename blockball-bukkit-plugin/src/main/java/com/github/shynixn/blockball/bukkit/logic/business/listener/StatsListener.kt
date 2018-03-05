package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.business.event.GameJoinEvent
import com.github.shynixn.blockball.api.bukkit.business.event.GameWinEvent
import com.github.shynixn.blockball.api.bukkit.business.event.GoalShootEvent
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.meta.stats.PlayerMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.stats.Stats
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.StatsScoreboard
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.PlayerInfoController
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.StatsRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.stats.StatsData
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
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
class StatsListener @Inject constructor(plugin: Plugin, private val statsController: StatsRepository, private val playerMetaController: PlayerInfoController) : SimpleListener(plugin), Runnable {

    private val statsScoreboards = HashMap<Player, StatsScoreboard>()

    init {
        if(Config.statsScoreboardEnabled!!) {
            this.setStatsForAllOnlinePlayers()
            this.plugin.server.scheduler.runTaskTimerAsynchronously(this.plugin, this, 0, 20L * 60)
        }
    }

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        for (player in this.statsScoreboards.keys) {
            val optStats = this@StatsListener.statsController.getByPlayer(player)
            optStats.ifPresent({ stats -> this.updateStats(player, stats) })
        }
    }


    /**
     * Sets the stats scoreboard for a player when he joins the server.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        if(Config.statsScoreboardEnabled!!) {
            this.setStatsForPlayer(event.player)
        }
    }

    /**
     * Removes the stats of a player.
     *
     * @param event event
     */
    @EventHandler
    @Throws(Exception::class)
    fun playerQuitEvent(event: PlayerQuitEvent) {
        if (this.statsScoreboards.containsKey(event.player)) {
            val scoreboard = this.statsScoreboards[event.player]
            this.statsScoreboards.remove(event.player)
            scoreboard!!.close()
        }
    }

    /**
     * Updates the goals of a player when he shoots a goal.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerShootGoalEvent(event: GoalShootEvent) {
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin) {
            synchronized(this.statsController) {
                val optStats = this@StatsListener.statsController.getByPlayer(event.player)
                if (optStats.isPresent) {
                    val stats = optStats.get()
                    stats.amountOfGoals = stats.amountOfGoals + 1
                    this.updateStats(event.player, stats)
                    this.statsController.store(stats)
                }
            }
        }
    }

    /**
     * Gets called when a player joins the match
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerJoinGameEvent(event: GameJoinEvent) {
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin) {
            synchronized(this.statsController) {
                val optStats = this@StatsListener.statsController.getByPlayer(event.player)
                if (optStats.isPresent) {
                    val stats = optStats.get()
                    stats.amountOfPlayedGames = stats.amountOfPlayedGames + 1
                    this.statsController.store(stats)
                    this.updateStats(event.player, stats)
                }
            }
        }
    }

    /**
     * Gets called when a game gets won.
     *
     * @param event event
     */
    @EventHandler
    fun onTeamWinEvent(event: GameWinEvent) {
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin, {
            synchronized(this.statsController) {
                var winningPlayers = event.game.redTeam
                if (event.team == Team.BLUE) {
                    winningPlayers = event.game.blueTeam
                }
                winningPlayers.forEach { p ->
                    val optStats = statsController.getByPlayer(p as Player)
                    if (optStats.isPresent) {
                        val stats = optStats.get()
                        stats.amountOfWins = (stats.amountOfWins + 1)
                        this.statsController.store(stats)
                        this.plugin.server.scheduler.runTaskLater(plugin, {
                            this.updateStats(p, stats)
                        }, 40L)
                    }
                }
            }
        })
    }

    private fun updateStats(player: Player, stats: Stats) {
        this.statsScoreboards[player]!!.updateStats(player, stats)
    }

    private fun setStatsForPlayer(player: Player) {
        val scoreboard = StatsScoreboard(player)
        this.statsScoreboards[player] = scoreboard
        this.plugin.server.scheduler.runTaskLaterAsynchronously(this.plugin, {
            synchronized(this.statsController) {
                val optStats = this@StatsListener.statsController.getByPlayer(player)
                val stats: Stats
                if (!optStats.isPresent) {
                    val optPlayerMeta = this.playerMetaController.getByUUID(player.uniqueId)
                    val meta: PlayerMeta<*>
                    if (!optPlayerMeta.isPresent) {
                        meta = this.playerMetaController.create(player)
                        this.playerMetaController.store(meta)
                    } else {
                        meta = optPlayerMeta.get()
                    }
                    stats = this.statsController.create()
                    (stats as StatsData).playerId = meta.id
                    this.statsController.store(stats)
                } else {
                    stats = optStats.get()
                }
                scoreboard.updateStats(player, stats)
            }
        }, 20 * 2L)
    }

    private fun setStatsForAllOnlinePlayers() {
        Bukkit.getWorlds()
                .flatMap { it.players }
                .forEach { this.setStatsForPlayer(it) }
    }
}