package com.github.shynixn.blockball.core.logic.persistence.repository

import com.github.shynixn.blockball.api.persistence.context.SqlDbContext
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.core.logic.persistence.entity.PlayerMetaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.StatsEntity
import com.google.inject.Inject

/**
 * Handles storing and retrieving arena from a stats medium.
 */
class StatsSqlRepository @Inject constructor(private val sqlDbContext: SqlDbContext) : StatsRepository {
    /**
     * Returns the [Stats] for the given player uniqueId. Creates
     * a new one if it does not exist yet.
     */
    override fun getOrCreateFromPlayer(name: String, uuid: String): Stats {
        return sqlDbContext.transaction<Stats, Any> { connection ->
            getStats(connection, uuid) ?: insert(connection, StatsEntity(PlayerMetaEntity(uuid, name)))
        }
    }

    /**
     * Saves the given [item] to the storage.
     */
    override fun save(item: Stats): Stats {
        return sqlDbContext.transaction<Stats, Any> { connection ->
            if (item.id == 0L) {
                val optStats = getStats(connection, item.playerMeta.uuid)

                if (optStats == null) {
                    insert(connection, item)
                } else {
                    item.id = optStats.id
                    item.playerMeta.id = optStats.playerMeta.id

                    update(connection, item)
                }
            } else {
                update(connection, item)
            }
        }
    }


    /**
     * Inserts the [stats] into the database.
     */
    private fun insert(connection: Any, stats: Stats): Stats {
        val playerMeta = stats.playerMeta

        sqlDbContext.singleQuery(connection, "SELECT * from SHY_PLAYER WHERE uuid = ?", { resultSet ->
            playerMeta.id = (resultSet["id"] as Int).toLong()
        }, playerMeta.uuid)

        if (playerMeta.id == 0L) {
            playerMeta.id = sqlDbContext.insert(
                connection, "SHY_PLAYER"
                , "uuid" to playerMeta.uuid
                , "name" to playerMeta.name
            )
        }

        stats.id = sqlDbContext.insert(
            connection, "SHY_BLOCKBALL_STATS"
            , "shy_player_id" to playerMeta.id
            , "wins" to stats.amountOfWins
            , "games" to stats.amountOfPlayedGames
            , "goals" to stats.amountOfGoals
        )

        return stats
    }

    /**
     * Updates the [stats] in the database.
     */
    private fun update(connection: Any, stats: Stats): Stats {
        val playerMeta = stats.playerMeta
        sqlDbContext.update(
            connection, "SHY_PLAYER", "WHERE id=" + playerMeta.id
            , "uuid" to playerMeta.uuid
            , "name" to playerMeta.name
        )

        sqlDbContext.update(
            connection, "SHY_BLOCKBALL_STATS", "WHERE id=" + stats.id
            , "wins" to stats.amountOfWins
            , "games" to stats.amountOfPlayedGames
            , "goals" to stats.amountOfGoals
        )

        return stats
    }

    /**
     * Gets the stats from the database.
     */
    private fun getStats(connection: Any, uuid: String): Stats? {
        val statement = "SELECT * " +
                "FROM SHY_BLOCKBALL_STATS stats, SHY_PLAYER player " +
                "WHERE player.uuid = ? " +
                "AND stats.shy_player_id = player.id "

        return sqlDbContext.singleQuery(connection, statement, { resultSet ->
            val playerMeta = PlayerMetaEntity(resultSet["uuid"] as String, resultSet["name"] as String)
            playerMeta.id = (resultSet["shy_player_id"] as Int).toLong()

            val stats = StatsEntity(playerMeta)

            with(stats) {
                id = (resultSet["id"] as Int).toLong()
                amountOfGoals = resultSet["goals"] as Int
                amountOfWins = resultSet["wins"] as Int
                amountOfPlayedGames = resultSet["games"] as Int
            }

            stats
        }, uuid)
    }

}
