package com.github.shynixn.blockball.bukkit.logic.persistence.repository

import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta
import com.github.shynixn.blockball.api.persistence.repository.PlayerRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.context.SqlDbContextImpl
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PlayerMetaEntity
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
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
class PlayerSqlRepository @Inject constructor(dbContext: SqlDbContextImpl, private val plugin: Plugin) : DatabaseRepository<PlayerMeta>(dbContext, "player"), PlayerRepository {
    /**
     * Returns the amount of items in this repository.
     */
    override fun size(): Int {
        return this.count
    }

    /**
     * Returns the [PlayerMeta] from the given [player] or allocates a new one.
     */
    override fun <P> getOrCreateFromPlayer(player: P): PlayerMeta {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredQuery("player/selectbyuuid", connection,
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

        val playerMeta = PlayerMetaEntity(player)
        save(player, playerMeta)

        return playerMeta
    }

    /**
     * Saves the given [PlayerMeta] to the storage.
     */
    override fun <P> save(player: P, playerMeta: PlayerMeta) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        playerMeta.name = player.name

        this.store(playerMeta)
    }

    /**
     * Updates the item inside of the database.
     *
     * @param item item
     */
    override fun update(item: PlayerMeta) {
        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredUpdate("player/update", connection,
                        item.uuid.toString(),
                        item.name,
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
    override fun insert(item: PlayerMeta) {
        try {
            if (item.name.isEmpty()) {
                throw IllegalArgumentException("Name cannot be empty!")
            }

            this.dbContext.connection.use { connection ->
                val id = this.dbContext.executeStoredInsert("player/insert", connection,
                        item.name, item.uuid.toString()).toLong()
                (item as PlayerMetaEntity).id = id
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
    override fun from(resultSet: ResultSet): PlayerMeta {
        val playerStats = PlayerMetaEntity()
        playerStats.id = resultSet.getLong("id")
        playerStats.name = resultSet.getString("name")
        playerStats.uuid = UUID.fromString(resultSet.getString("uuid"))
        return playerStats
    }
}