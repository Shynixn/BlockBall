package com.github.shynixn.blockball.enumeration

import com.github.shynixn.blockball.BlockBallPlugin
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.contract.SoccerMiniGame
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.LeaderBoardStats
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import org.bukkit.entity.Player

enum class PlaceHolder(val text: String, val f: (Player?, SoccerGame?, Map<String, Any>?) -> String?) {
    // Game PlaceHolders
    GAME_NAME("%blockball_game_name_[game]%", { _, game, _ -> game?.arena?.name }),

    GAME_DISPLAYNAME("%blockball_game_displayName_[game]%", { _, game, _ -> game?.arena?.displayName }),

    GAME_SUM_MAXPLAYERS("%blockball_game_maxPlayers_[game]%", { _, game, _ ->
        if (game != null) {
            (game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount).toString()
        } else {
            null
        }
    }),

    GAME_SUM_CURRENTPLAYERS(
        "%blockball_game_players_[game]%",
        { _, game, _ -> game?.ingamePlayersStorage?.size?.toString() }),

    GAME_RED_SCORE("%blockball_game_redScore_[game]%", { _, game, _ -> game?.redScore?.toString() }),

    GAME_RED_PLAYER_AMOUNT("%blockball_game_redPlayers_[game]%", { _, game, _ -> game?.redTeam?.size?.toString() }),

    GAME_RED_PLAYER_MAXAMOUNT(
        "%blockball_game_redMaxPlayers_[game]%",
        { _, game, _ -> game?.arena?.meta?.redTeamMeta?.maxAmount?.toString() }),

    GAME_RED_DISPLAYNAME(
        "%blockball_game_redDisplayName_[game]%",
        { _, game, _ -> game?.arena?.meta?.redTeamMeta?.displayName }),

    GAME_BLUE_SCORE("%blockball_game_blueScore_[game]%", { _, game, _ -> game?.blueScore?.toString() }),

    GAME_BLUE_PLAYER_AMOUNT("%blockball_game_bluePlayers_[game]%", { _, game, _ -> game?.blueTeam?.size?.toString() }),

    GAME_BLUE_PLAYER_MAXAMOUNT(
        "%blockball_game_blueMaxPlayers_[game]%",
        { _, game, _ -> game?.arena?.meta?.blueTeamMeta?.maxAmount?.toString() }),

    GAME_BLUE_DISPLAYNAME(
        "%blockball_game_blueDisplayName_[game]%",
        { _, game, _ -> game?.arena?.meta?.blueTeamMeta?.displayName }),

    GAME_REFEREE_DISPLAYNAME(
        "%blockball_game_refereeDisplayName_[game]%",
        { _, game, _ -> game?.arena?.meta?.refereeTeamMeta?.displayName }),

    GAME_TIME("%blockball_game_time_[game]%", { _, game, _ ->
        if (game is SoccerMiniGame) {
            game.gameCountdown.toString()
        } else {
            "∞"
        }
    }),

    GAME_LASTHITPLAYER_NAME("%blockball_game_lastHitPlayerName_[game]%", { _, game, _ ->
        if (game?.lastInteractedEntity != null) {
            game.lastInteractedEntity!!.name
        } else {
            null
        }
    }),

    GAME_LASTHITPLAYER_TEAM_NAME("%blockball_game_lastHitPlayerTeam_[game]%", { _, game, _ ->
        val player = game?.lastInteractedEntity
        if (player != null && true) {
            val language = game.language
            if (game.redTeam.contains(player)) {
                game.arena.meta.redTeamMeta.displayName
            } else if (game.blueTeam.contains(player)) {
                game.arena.meta.blueTeamMeta.displayName
            } else {
                ""
            }
        } else {
            null
        }
    }),

    GAME_STATE("%blockball_game_state_[game]%", { _, game, _ -> game?.status?.name }),

    GAME_STATE_DISPLAYNAME("%blockball_game_stateDisplayName_[game]%", { _, game, _ ->
        if (game != null) {
            val language = game.language
            if (game.status == GameState.JOINABLE) {
                language.gameStatusJoinAble.text
            } else if (game.status == GameState.DISABLED) {
                language.gameStatusDisabled.text
            } else {
                language.gameStatusRunning.text
            }
        } else {
            null
        }
    }),

    GAME_IS_ENABLED("%blockball_game_isEnabled_[game]%", { _, game, _ ->
        game?.arena?.enabled?.toString()
    }),

    GAME_IS_JOINABLE("%blockball_game_isJoinAble_[game]%", { _, game, _ ->
        if (game != null) {
            (game.arena.enabled && game.status == GameState.JOINABLE).toString()
        } else {
            null
        }
    }),

    GAME_REMAININGPLAYERS_TO_START("%blockball_game_remainingPlayers_[game]%", { _, game, _ ->
        if (game != null) {
            val r =
                (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size)
            if (r < 0) {
                "0"
            } else {
                r.toString()
            }
        } else {
            null
        }
    }),

    // Player PlaceHolders

    PLAYER_NAME("%blockball_player_name%", { player, _, _ -> player?.name }),

    PLAYER_TEAM("%blockball_player_team%", { player, game, _ ->
        if (game?.blueTeam != null && game.blueTeam.contains(player)) {
            "blue"
        } else if (game?.redTeam != null && game.redTeam.contains(player)) {
            "red"
        } else if (game?.refereeTeam != null && game.refereeTeam.contains(player)) {
            "referee"
        } else {
            null
        }
    }),

    PLAYER_TEAM_DISPLAY("%blockball_player_teamDisplayName%", { player, game, _ ->
        if (game?.blueTeam != null && game.blueTeam.contains(player)) {
            game.arena.meta.blueTeamMeta.displayName
        } else if (game?.redTeam != null && game.redTeam.contains(player)) {
            game.arena.meta.redTeamMeta.displayName
        } else if (game?.refereeTeam != null && game.refereeTeam.contains(player)) {
            game.arena.meta.refereeTeamMeta.displayName
        } else {
            null
        }
    }),

    PLAYER_IS_INGAME("%blockball_player_isInGame%", { player, game, _ ->
        game?.ingamePlayersStorage?.containsKey(player)?.toString()
    }),

    PLAYER_IS_IN_TEAM_RED("%blockball_player_isInTeamRed%", { player, game, _ ->
        game?.redTeam?.contains(player)?.toString()
    }),

    PLAYER_IS_IN_TEAM_BLUE("%blockball_player_isInTeamBlue%", { player, game, _ ->
        game?.blueTeam?.contains(player)?.toString()
    }),

    // Play Stats PlaceHolders

    PLAYER_STATS_GOALS("%blockball_player_goals%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.scoredGoals?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_GOALSFULL("%blockball_player_goalsFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.scoredGoalsFull?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_GOALSCURRENT("%blockball_player_goalsCurrent%", { player, game, _ ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (game != null && player != null && game.ingamePlayersStorage.containsKey(player)) {
            val storage = game.ingamePlayersStorage[player]!!
            (storage.scoredGoals).toString()
        } else {
            "0"
        }
    }),

    PLAYER_STATS_OWNGOALS("%blockball_player_ownGoals%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.scoredOwnGoals?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_OWNGOALSFULL("%blockball_player_ownGoalsFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.scoredOwnGoalsFull?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_OWNGOALSCURRENT("%blockball_player_ownGoalsCurrent%", { player, game, _ ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (game != null && player != null && game.ingamePlayersStorage.containsKey(player)) {
            val storage = game.ingamePlayersStorage[player]!!
            (storage.scoredOwnGoals).toString()
        } else {
            "0"
        }
    }),

    PLAYER_STATS_TOTALGOALS("%blockball_player_totalGoals%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                (playerData.statsMeta.scoredGoals + playerData.statsMeta.scoredOwnGoals).toString()
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_TOTALGOALSFULL("%blockball_player_totalGoalsFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                (playerData.statsMeta.scoredGoalsFull + playerData.statsMeta.scoredOwnGoalsFull).toString()
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_TOTALGOALSCURRENT("%blockball_player_totalGoalsCurrent%", { player, game, _ ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (game != null && player != null && game.ingamePlayersStorage.containsKey(player)) {
            val storage = game.ingamePlayersStorage[player]!!
            (storage.scoredGoals + storage.scoredOwnGoals).toString()
        } else {
            "0"
        }
    }),

    PLAYER_STATS_GAMES("%blockball_player_games%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.joinedGames?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_GAMESFULL("%blockball_player_gamesFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.playedGames?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_WINS("%blockball_player_wins%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.winsAmount?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_LOSSES("%blockball_player_losses%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                (playerData.statsMeta.playedGames - playerData.statsMeta.winsAmount - playerData.statsMeta.drawsAmount).toString()
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_DRAWS("%blockball_player_draws%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            playerData?.statsMeta?.drawsAmount?.toString() ?: ""
        } else {
            null
        }
    }),

    PLAYER_STATS_WINRATE("%blockball_player_winrate%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.joinedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.winsAmount.toDouble() / playerData.statsMeta.joinedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_WINRATEFULL("%blockball_player_winrateFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.playedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.winsAmount.toDouble() / playerData.statsMeta.playedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_GOALSPERGAME("%blockball_player_goalsPerGame%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.joinedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.scoredGoals.toDouble() / playerData.statsMeta.joinedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_GOALSPERGAMEFULL("%blockball_player_goalsPerGameFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.playedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.scoredGoals.toDouble() / playerData.statsMeta.playedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_OWNGOALSPERGAME("%blockball_player_ownGoalsPerGame%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.joinedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.scoredOwnGoals.toDouble() / playerData.statsMeta.joinedGames.toDouble()
                    String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_OWNGOALSPERGAMEFULL("%blockball_player_ownGoalsPerGameFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.playedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        playerData.statsMeta.scoredOwnGoalsFull.toDouble() / playerData.statsMeta.playedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_TOTALGOALSPERGAME("%blockball_player_totalGoalsPerGame%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.joinedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        (playerData.statsMeta.scoredOwnGoals + playerData.statsMeta.scoredGoals).toDouble() / playerData.statsMeta.joinedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    PLAYER_STATS_TOTALGOALSPERGAMEFULL("%blockball_player_totalGoalsPerGameFull%", { _, _, context ->
        if (!com.github.shynixn.blockball.BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            "PatreonOnly"
        } else if (context != null) {
            val playerData = context[BlockBallPlugin.playerDataKey] as PlayerInformation?
            if (playerData != null) {
                if (playerData.statsMeta.playedGames == 0) {
                    "0.00"
                } else {
                    val result =
                        (playerData.statsMeta.scoredOwnGoalsFull + playerData.statsMeta.scoredGoalsFull).toDouble() / playerData.statsMeta.playedGames.toDouble()
                    kotlin.String.format("%.2f", result)
                }
            } else {
                ""
            }
        } else {
            null
        }
    }),

    // LeaderBoard

    LEADERBOARD_GOALS_NAME(
        "%blockball_leaderboard_goals_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGoals, index)
            } else {
                null
            }
        }),
    LEADERBOARD_GOALS_VALUE("%blockball_leaderboard_goals_value_top_[index]%", { _, _, context ->
        if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
            val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
            val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
            leaderBoard.getValueIntOrEmpty(leaderBoard.topTenGoals, index)
        } else {
            null
        }
    }),

    LEADERBOARD_GOALS_FULL_NAME(
        "%blockball_leaderboard_goalsFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GOALS_FULL_VALUE(
        "%blockball_leaderboard_goalsFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_NAME(
        "%blockball_leaderboard_ownGoals_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenOwnGoals, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_VALUE(
        "%blockball_leaderboard_ownGoals_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenOwnGoals, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_FULL_NAME(
        "%blockball_leaderboard_ownGoalsFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenOwnGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_FULL_VALUE(
        "%blockball_leaderboard_ownGoalsFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenOwnGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_TOTAL_GOALS_NAME(
        "%blockball_leaderboard_totalGoals_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenTotalGoals, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_TOTAL_GOALS_VALUE(
        "%blockball_leaderboard_totalGoals_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenTotalGoals, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_TOTAL_GOALS_FULL_NAME(
        "%blockball_leaderboard_totalGoalsFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenTotalGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_TOTAL_GOALS_FULL_VALUE(
        "%blockball_leaderboard_totalGoalsFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenTotalGoalsFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GAMES_NAME(
        "%blockball_leaderboard_games_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGames, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GAMES_VALUE(
        "%blockball_leaderboard_games_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenGames, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GAMES_FULL_NAME(
        "%blockball_leaderboard_gamesFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGamesFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GAMES_FULL_VALUE(
        "%blockball_leaderboard_gamesFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenGamesFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_WINS_NAME(
        "%blockball_leaderboard_wins_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenWins, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_WINS_VALUE(
        "%blockball_leaderboard_wins_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenWins, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_LOSSES_NAME(
        "%blockball_leaderboard_losses_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenLosses, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_LOSSES_VALUE(
        "%blockball_leaderboard_losses_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenLosses, index)
            } else {
                null
            }
        }
    ),
    LEADERBOARD_DRAWS_NAME(
        "%blockball_leaderboard_draws_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenDraws, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_DRAWS_VALUE(
        "%blockball_leaderboard_draws_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueIntOrEmpty(leaderBoard.topTenDraws, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_WINRATE_NAME(
        "%blockball_leaderboard_winrate_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenWinRate, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_WINRATE_VALUE(
        "%blockball_leaderboard_winrate_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueFloatOrEmpty(leaderBoard.topTenWinRate, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GOALS_PER_GAME_NAME(
        "%blockball_leaderboard_goalsPerGame_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGoalsPerGame, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GOALS_PER_GAME_VALUE(
        "%blockball_leaderboard_goalsPerGame_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueFloatOrEmpty(leaderBoard.topTenGoalsPerGame, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GOALS_PER_GAME_FULL_NAME(
        "%blockball_leaderboard_goalsPerGameFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenGoalsPerGameFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_GOALS_PER_GAME_FULL_VALUE(
        "%blockball_leaderboard_goalsPerGameFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueFloatOrEmpty(leaderBoard.topTenGoalsPerGameFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_PER_GAME_NAME(
        "%blockball_leaderboard_ownGoalsPerGame_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenOwnGoalsPerGame, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_PER_GAME_VALUE(
        "%blockball_leaderboard_ownGoalsPerGame_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueFloatOrEmpty(leaderBoard.topTenOwnGoalsPerGame, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_PER_GAME_FULL_NAME(
        "%blockball_leaderboard_ownGoalsPerGameFull_name_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getNameOrEmpty(leaderBoard.topTenOwnGoalsPerGameFull, index)
            } else {
                null
            }
        }
    ),

    LEADERBOARD_OWN_GOALS_PER_GAME_FULL_VALUE(
        "%blockball_leaderboard_ownGoalsPerGameFull_value_top_[index]%", { _, _, context ->
            if (context != null && context.containsKey(BlockBallPlugin.leaderBoardKey)) {
                val leaderBoard = context[BlockBallPlugin.leaderBoardKey] as LeaderBoardStats
                val index = (context[BlockBallPlugin.indexKey] as String).toInt() - 1
                leaderBoard.getValueFloatOrEmpty(leaderBoard.topTenOwnGoalsPerGameFull, index)
            } else {
                null
            }
        }
    );

    companion object {
        /**
         * Registers all placeHolder. Overrides previously registered placeholders.
         */
        fun registerAll(
            placeHolderService: PlaceHolderService,
            playerDataRepository: CachePlayerRepository<PlayerInformation>,
            gameService: GameService,
            statsService: StatsService
        ) {
            for (placeHolder in PlaceHolder.values()) {
                placeHolderService.register(placeHolder.text) { player, context ->
                    val newContext = context.toMutableMap()
                    val gameNameReference = newContext[BlockBallPlugin.gameKey] as String?
                    val game = if (gameNameReference != null) {
                        gameService.getByName(gameNameReference)
                    } else if (player != null) {
                        gameService.getByPlayer(player)
                    } else {
                        null
                    }

                    if (player != null) {
                        val playerInformation = playerDataRepository.getCachedByPlayer(player)
                        if (playerInformation != null) {
                            newContext[BlockBallPlugin.playerDataKey] = playerInformation
                        }
                    }

                    val leaderBoard = statsService.getLeaderBoard()
                    if (leaderBoard != null) {
                        newContext[BlockBallPlugin.leaderBoardKey] = leaderBoard
                    }

                    placeHolder.f.invoke(player, game, newContext)
                }
            }
        }
    }
}
