package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.business.service.StatsCollectingService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.StatsScoreboard
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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
class StatsCollectingServiceImpl @Inject constructor(private val plugin: Plugin, private val configurationService: ConfigurationService, private val persistenceStatsService: PersistenceStatsService) : StatsCollectingService, Runnable {
    private val statsScoreboards = HashMap<Player, StatsScoreboard>()

    init {
        Bukkit.getWorlds().forEach { w ->
            w.players.forEach { p ->
                setStatsScoreboard(p)
            }
        }

        plugin.server.scheduler.runTaskTimerAsynchronously(this.plugin, this, 0, 20L * 60)
    }

    /**
     * Cleans all allocated resources of the given [player] in this service.
     */
    override fun <P> cleanResources(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!this.statsScoreboards.containsKey(player)) {
            return
        }

        val scoreboard = this.statsScoreboards[player]
        this.statsScoreboards.remove(player)
        scoreboard!!.close()
    }

    /**
     * Updates the stats for the given [player].
     */
    override fun <P> updateStats(player: P, f: (Stats) -> Unit) {
        persistenceStatsService.getOrCreateFromPlayer(player).thenAccept { stats ->
            f.invoke(stats)
            persistenceStatsService.save(player, stats)
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
            setStatsScoreboard(player)
        }
    }

    /**
     * Sets and refreshes the scoreboard for the given [player].
     */
    override fun <P> setStatsScoreboard(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!configurationService.findValue<Boolean>("stats-scoreboard.enabled")) {
            return
        }

        if (!statsScoreboards.containsKey(player)) {
            val scoreboard = StatsScoreboard(player)
            this.statsScoreboards[player] = scoreboard
        }

        persistenceStatsService.getOrCreateFromPlayer(player).thenAccept { stats ->
            statsScoreboards[player]!!.updateStats(player, stats)
        }
    }
}