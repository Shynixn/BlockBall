package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallDependencyInjectionModule
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.LeaderBoardStats
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.StatsMeta
import com.github.shynixn.mccoroutine.bukkit.CoroutineTimings
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import kotlinx.coroutines.delay
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class StatsServiceImpl(
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin
) : StatsService {
    private var aggregationLeaderBoardKey = "leaderBoardAg"
    private var leaderBoardStats: LeaderBoardStats? = null
    private var isDisposed: Boolean = false

    /**
     * Registers tracking the stats.
     */
    override fun register() {
        val refreshTime = plugin.config.getInt("leaderboard.intervalMinutes")
        val isEnabled = plugin.config.getBoolean("leaderboard.enabled")

        if (isEnabled) {
            plugin.logger.log(Level.INFO, "Enabled LeaderBoard tracking.")

            playerDataRepository.registerAggregation(
                aggregationLeaderBoardKey,
                { LeaderBoardStats() }) { playerInfo, leaderBoard ->
                computeLeaderBoard(playerInfo, leaderBoard)
            }

            plugin.launch(object : CoroutineTimings() {}) {
                while (!isDisposed) {
                    leaderBoardStats =
                        playerDataRepository.getAggregationResult<LeaderBoardStats>(aggregationLeaderBoardKey)
                    delay(1000L * 60 * refreshTime)
                }
            }
        }
    }

    /**
     * Gets the leaderBoardStats.
     */
    override fun getLeaderBoard(): LeaderBoardStats? {
        if (BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            return leaderBoardStats
        }

        return null
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        isDisposed = true
    }

    private fun computeLeaderBoard(playerInfo: PlayerInformation, leaderBoardStats: LeaderBoardStats) {
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenWins,
            playerInfo
        ) { statsMeta -> statsMeta.winsAmount }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenLosses,
            playerInfo
        ) { statsMeta -> (statsMeta.playedGames - statsMeta.winsAmount - statsMeta.drawsAmount) }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenDraws,
            playerInfo
        ) { statsMeta -> (statsMeta.drawsAmount) }

        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenGoals,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredGoals) }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenGoalsFull,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredGoalsFull) }

        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenOwnGoals,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredOwnGoals) }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenOwnGoalsFull,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredOwnGoalsFull) }

        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenTotalGoals,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredGoals + statsMeta.scoredOwnGoals) }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenTotalGoalsFull,
            playerInfo
        ) { statsMeta -> (statsMeta.scoredGoalsFull + statsMeta.scoredOwnGoalsFull) }

        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenGames,
            playerInfo
        ) { statsMeta -> (statsMeta.joinedGames) }
        executeLeaderBoardOrderingInt(
            leaderBoardStats.topTenGamesFull,
            playerInfo
        ) { statsMeta -> (statsMeta.playedGames) }

        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenWinRate, playerInfo) { statsMeta ->
            if (statsMeta.joinedGames == 0) {
                0.0F
            } else {
                (statsMeta.winsAmount.toFloat() / statsMeta.joinedGames.toFloat())
            }
        }
        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenWinRate, playerInfo) { statsMeta ->
            if (statsMeta.playedGames == 0) {
                0.0F
            } else {
                statsMeta.winsAmount.toFloat() / statsMeta.playedGames.toFloat()
            }
        }

        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenGoalsPerGame, playerInfo) { statsMeta ->
            if (statsMeta.joinedGames == 0) {
                0.0F
            } else {
                statsMeta.scoredGoals.toFloat() / statsMeta.joinedGames.toFloat()
            }
        }
        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenGoalsPerGameFull, playerInfo) { statsMeta ->
            if (statsMeta.playedGames == 0) {
                0.0F
            } else {
                statsMeta.scoredGoals.toFloat() / statsMeta.playedGames.toFloat()
            }
        }

        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenOwnGoalsPerGame, playerInfo) { statsMeta ->
            if (statsMeta.joinedGames == 0) {
                0.0F
            } else {
                statsMeta.scoredOwnGoals.toFloat() / statsMeta.joinedGames.toFloat()
            }
        }
        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenOwnGoalsPerGameFull, playerInfo) { statsMeta ->
            if (statsMeta.playedGames == 0) {
                0.0F
            } else {
                statsMeta.scoredOwnGoals.toFloat() / statsMeta.playedGames.toFloat()
            }
        }

        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenTotalGoalsPerGame, playerInfo) { statsMeta ->
            if (statsMeta.joinedGames == 0) {
                0.0F
            } else {
                (statsMeta.scoredOwnGoals.toFloat() + statsMeta.scoredGoals.toFloat()) / statsMeta.joinedGames.toFloat()
            }
        }
        executeLeaderBoardOrderingFloat(leaderBoardStats.topTenTotalGoalsPerGameFull, playerInfo) { statsMeta ->
            if (statsMeta.playedGames == 0) {
                0.0F
            } else {
                (statsMeta.scoredOwnGoals.toFloat() + statsMeta.scoredGoals.toFloat()) / statsMeta.playedGames.toFloat()
            }
        }
    }

    private fun executeLeaderBoardOrderingInt(
        list: MutableList<Pair<String, Int>>,
        playerInfo: PlayerInformation,
        evaluateFun: (StatsMeta) -> Int,
    ) {
        val statsMeta = playerInfo.statsMeta
        var found = false
        val value = evaluateFun(statsMeta)

        for (i in 0 until list.size) {
            if (value > list[i].second) {
                list.add(i, Pair(playerInfo.playerName, value))
                found = true
                break
            }
        }

        if (!found) {
            list.add(Pair(playerInfo.playerName, value))
        }

        while (list.size > 10) {
            list.removeLast()
        }
    }

    private fun executeLeaderBoardOrderingFloat(
        list: MutableList<Pair<String, Float>>,
        playerInfo: PlayerInformation,
        evaluateFun: (StatsMeta) -> Float,
    ) {
        val statsMeta = playerInfo.statsMeta
        var found = false
        val value = evaluateFun(statsMeta)

        for (i in 0 until list.size) {
            if (value > list[i].second) {
                list.add(i, Pair(playerInfo.playerName, value))
                found = true
                break
            }
        }

        if (!found) {
            list.add(Pair(playerInfo.playerName, value))
        }

        while (list.size > 10) {
            list.removeLast()
        }
    }
}
