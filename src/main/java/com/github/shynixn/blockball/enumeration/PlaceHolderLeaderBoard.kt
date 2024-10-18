package com.github.shynixn.blockball.enumeration

import com.github.shynixn.blockball.entity.LeaderBoardStats
import com.github.shynixn.blockball.impl.service.StatsServiceImpl

enum class PlaceHolderLeaderBoard(
    /**
     * Placeholder value.
     */
    val fullPlaceHolder: String,
    /**
     * Function.
     */
    val f: (LeaderBoardStats, Int) -> String
) {
    LEADERBOARD_GOALS_NAME(
        "%blockball_leaderboard_goals_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGoals, i) }),
    LEADERBOARD_GOALS_VALUE("%blockball_leaderboard_goals_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenGoals, i) }),

    LEADERBOARD_GOALS_FULL_NAME("%blockball_leaderboard_goalsFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGoalsFull, i) }),
    LEADERBOARD_GOALS_FULL_VALUE("%blockball_leaderboard_goalsFull_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenGoalsFull, i) }),

    LEADERBOARD_OWN_GOALS_NAME("%blockball_leaderboard_ownGoals_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenOwnGoals, i) }),
    LEADERBOARD_OWN_GOALS_VALUE("%blockball_leaderboard_ownGoals_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenOwnGoals, i) }),

    LEADERBOARD_OWN_GOALS_FULL_NAME(
        "%blockball_leaderboard_ownGoalsFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenOwnGoalsFull, i) }),
    LEADERBOARD_OWN_GOALS_FULL_VALUE(
        "%blockball_leaderboard_ownGoalsFull_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenOwnGoalsFull, i) }),

    LEADERBOARD_TOTAL_GOALS_NAME(
        "%blockball_leaderboard_totalGoals_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenTotalGoals, i) }),
    LEADERBOARD_TOTAL_GOALS_VALUE(
        "%blockball_leaderboard_totalGoals_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenTotalGoals, i) }),

    LEADERBOARD_TOTAL_GOALS_FULL_NAME(
        "%blockball_leaderboard_totalGoalsFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenTotalGoalsFull, i) }),
    LEADERBOARD_TOTAL_GOALS_FULL_VALUE("%blockball_leaderboard_totalGoalsFull_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenTotalGoalsFull, i) }),

    LEADERBOARD_GAMES_NAME(
        "%blockball_leaderboard_games_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGames, i) }),
    LEADERBOARD_GAMES_VALUE(
        "%blockball_leaderboard_games_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenGames, i) }),

    LEADERBOARD_GAMES_FULL_NAME(
        "%blockball_leaderboard_gamesFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGamesFull, i) }),
    LEADERBOARD_GAMES_FULL_VALUE(
        "%blockball_leaderboard_gamesFull_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenGamesFull, i) }),

    LEADERBOARD_WINS_NAME(
        "%blockball_leaderboard_wins_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenWins, i) }),
    LEADERBOARD_WINS_VALUE(
        "%blockball_leaderboard_wins_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenWins, i) }),
    LEADERBOARD_LOSSES_NAME(
        "%blockball_leaderboard_losses_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenLosses, i) }),
    LEADERBOARD_LOSSES_VALUE(
        "%blockball_leaderboard_losses_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenLosses, i) }),

    LEADERBOARD_DRAWS_NAME(
        "%blockball_leaderboard_draws_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenDraws, i) }),
    LEADERBOARD_DRAWS_VALUE(
        "%blockball_leaderboard_draws_value",
        { b, i -> StatsServiceImpl.getValueIntOrEmpty(b.topTenDraws, i) }),
    LEADERBOARD_WINRATE_NAME(
        "%blockball_leaderboard_winrate_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenWinRate, i) }),
    LEADERBOARD_WINRATE_VALUE(
        "%blockball_leaderboard_winrate_value",
        { b, i -> StatsServiceImpl.getValueFloatOrEmpty(b.topTenWinRate, i) }),

    LEADERBOARD_GOALS_PER_GAME_NAME(
        "%blockball_leaderboard_goalsPerGame_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGoalsPerGame, i) }),
    LEADERBOARD_GOALS_PER_GAME_VALUE(
        "%blockball_leaderboard_goalsPerGame_value",
        { b, i -> StatsServiceImpl.getValueFloatOrEmpty(b.topTenGoalsPerGame, i) }),
    LEADERBOARD_GOALS_PER_GAME_FULL_NAME(
        "%blockball_leaderboard_goalsPerGameFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenGoalsPerGameFull, i) }),
    LEADERBOARD_GOALS_PER_GAME_FULL_VALUE(
        "%blockball_leaderboard_goalsPerGameFull_value",
        { b, i -> StatsServiceImpl.getValueFloatOrEmpty(b.topTenGoalsPerGameFull, i) }),
    LEADERBOARD_OWN_GOALS_PER_GAME_NAME(
        "%blockball_leaderboard_ownGoalsPerGame_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenOwnGoalsPerGame, i) }),
    LEADERBOARD_OWN_GOALS_PER_GAME_VALUE(
        "%blockball_leaderboard_ownGoalsPerGame_value",
        { b, i -> StatsServiceImpl.getValueFloatOrEmpty(b.topTenOwnGoalsPerGame, i) }),
    LEADERBOARD_OWN_GOALS_PER_GAME_FULL_NAME(
        "%blockball_leaderboard_ownGoalsPerGameFull_name",
        { b, i -> StatsServiceImpl.getNameOrEmpty(b.topTenOwnGoalsPerGameFull, i) }),
    LEADERBOARD_OWN_GOALS_PER_GAME_FULL_VALUE(
        "%blockball_leaderboard_ownGoalsPerGameFull_value",
        { b, i -> StatsServiceImpl.getValueFloatOrEmpty(b.topTenOwnGoalsPerGameFull, i) });

    companion object {
        val mapping: MutableMap<String, PlaceHolderLeaderBoard> = HashMap()

        init {
            for (placeholder in PlaceHolderLeaderBoard.values()) {
                mapping[placeholder.fullPlaceHolder] = placeholder
            }
        }
    }
}
