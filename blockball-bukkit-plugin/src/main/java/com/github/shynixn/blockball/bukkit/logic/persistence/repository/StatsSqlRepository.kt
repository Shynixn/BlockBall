package com.github.shynixn.blockball.bukkit.logic.persistence.repository

import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.api.persistence.repository.PlayerRepository
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ConnectionContextService
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.StatsData
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Level

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
class StatsSqlRepository @Inject constructor(dbContext: ConnectionContextService, private val plugin: Plugin, private val playerRepository: PlayerRepository) : DatabaseRepository<Stats>(dbContext, "stats"), StatsRepository {
    /**
     * Returns the amount of items in this repository.
     */
    override fun size(): Int {
        return this.count
    }

    /**
     * Returns the [Stats] from the given [player] or allocates a new one.
     */
    override fun <P> getOrCreateFromPlayer(player: P): Stats {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredQuery("stats/selectbyplayer", connection,
                        player.uniqueId.toString()).use { preparedStatement ->
                    preparedStatement.executeQuery().use { resultSet ->
                        if (resultSet.next()) {
                            return this.from(resultSet)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            plugin.logger.log(Level.WARNING, "Database error occurred.", e)
        }

        val playerInfo = playerRepository.getOrCreateFromPlayer(player)
        val stats = StatsData()
        stats.playerId = playerInfo.id
        save(player, stats)

        return stats
    }

    /**
     * Saves the given [Stats] to the storage.
     */
    override fun <P> save(player: P, stats: Stats) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        this.store(stats)
    }

    /**
     * Updates the item inside of the database.
     *
     * @param item item
     */
    override fun update(item: Stats) {
        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredUpdate("stats/update", connection,
                        item.amountOfWins,
                        item.amountOfPlayedGames,
                        item.amountOfGoals,
                        item.id)
            }
        } catch (e: SQLException) {
            plugin.logger.log(Level.WARNING, "Database error occurred.", e)
        }
    }

    /**
     * Inserts the item into the database and sets the id.
     *
     * @param item item
     */
    override fun insert(item: Stats) {
        try {
            this.dbContext.connection.use { connection ->
                val id = this.dbContext.executeStoredInsert("stats/insert", connection,
                        (item as StatsData).playerId,
                        item.amountOfWins,
                        item.amountOfPlayedGames,
                        item.amountOfGoals).toLong()
                item.id = id
            }
        } catch (e: SQLException) {
            plugin.logger.log(Level.WARNING, "Database error occurred.", e)
        }
    }

    /**
     * Generates the entity from the given resultSet.
     *
     * @param resultSet resultSet
     * @return entity
     * @throws SQLException exception
     */
    override fun from(resultSet: ResultSet): Stats {
        val stats = StatsData()

        with(stats) {
            id = resultSet.getLong("id")
            playerId = resultSet.getLong("shy_player_id")
            amountOfWins = resultSet.getInt("wins")
            amountOfPlayedGames = resultSet.getInt("games")
            amountOfGoals = resultSet.getInt("goals")
        }

        return stats
    }
}