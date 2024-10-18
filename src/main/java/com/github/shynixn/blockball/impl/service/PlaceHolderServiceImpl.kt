package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallDependencyInjectionModule
import com.github.shynixn.blockball.BlockBallLanguageImpl
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.blockball.enumeration.PlaceHolderLeaderBoard
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.google.inject.Inject
import org.bukkit.entity.Player

class PlaceHolderServiceImpl @Inject constructor(
    private val gameService: GameService, private val playerDataRepository: CachePlayerRepository<PlayerInformation>,
    private val statsService: StatsService
) : PlaceHolderService {
    private val gamePlayerHolderFunctions = HashMap<PlaceHolder, ((SoccerGame) -> String)>()
    private val teamPlaceHolderFunctions = HashMap<PlaceHolder, ((SoccerGame, TeamMeta, Int) -> String)>()
    private val playerPlaceHolderFunctions = HashMap<PlaceHolder, ((Player) -> String)>()
    private val langPlaceHolderFunctions = HashMap<String, (() -> String)>()
    private val placeHolders = HashMap<String, PlaceHolder>()

    init {
        for (placeHolder in PlaceHolder.values()) {
            placeHolders[placeHolder.fullPlaceHolder] = placeHolder
        }

        for (field in BlockBallLanguageImpl::class.java.declaredFields) {
            field.isAccessible = true
            langPlaceHolderFunctions["%blockball_lang_${field.name}%"] = { field.get(null) as String }
        }

        // Game PlaceHolders
        gamePlayerHolderFunctions[PlaceHolder.GAME_NAME] = { game -> game.arena.name }
        gamePlayerHolderFunctions[PlaceHolder.GAME_DISPLAYNAME] = { game -> game.arena.displayName }
        gamePlayerHolderFunctions[PlaceHolder.GAME_SUM_MAXPLAYERS] =
            { game -> (game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount).toString() }
        gamePlayerHolderFunctions[PlaceHolder.GAME_SUM_CURRENTPLAYERS] =
            { game -> (game.ingamePlayersStorage.size).toString() }
        gamePlayerHolderFunctions[PlaceHolder.GAME_RED_SCORE] = { game -> game.redScore.toString() }
        gamePlayerHolderFunctions[PlaceHolder.GAME_BLUE_SCORE] = { game -> game.blueScore.toString() }
        gamePlayerHolderFunctions[PlaceHolder.GAME_TIME] = { game ->
            if (game is SoccerMiniGame) {
                game.gameCountdown.toString()
            } else {
                "∞"
            }
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_LASTHITPLAYER_NAME] = { game ->
            val player = game.lastInteractedEntity
            if (player != null && player is Player) {
                player.name
            } else {
                ""
            }
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_LASTHITPLAYER_TEAM_NAME] = { game ->
            val player = game.lastInteractedEntity
            if (player != null && player is Player) {
                if (game.redTeam.contains(player)) {
                    BlockBallLanguageImpl.teamRedDisplayName
                } else if (game.blueTeam.contains(player)) {
                    BlockBallLanguageImpl.teamBlueDisplayName
                } else {
                    ""
                }
            } else {
                ""
            }
        }

        gamePlayerHolderFunctions[PlaceHolder.GAME_STATE] = { game -> game.status.name }
        gamePlayerHolderFunctions[PlaceHolder.GAME_STATE_DISPLAYNAME] = { game ->
            if (game.status == GameState.JOINABLE) {
                BlockBallLanguageImpl.gameStatusJoinAble
            } else if (game.status == GameState.DISABLED) {
                BlockBallLanguageImpl.gameStatusDisabled
            } else {
                BlockBallLanguageImpl.gameStatusRunning
            }
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_REMAININGPLAYERS_TO_START] = { game ->
            val r =
                (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size)
            if (r < 0) {
                "0"
            } else {
                r.toString()
            }
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_IS_ENABLED] = { game ->
            game.arena.enabled.toString()
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_IS_JOINABLE] = { game ->
            (game.arena.enabled && game.status == GameState.JOINABLE).toString()
        }

        // Player PlaceHolders
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_NAME] = { player ->
            player.name
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_INGAME] = { player ->
            val game = gameService.getByPlayer(player)
            game?.ingamePlayersStorage?.containsKey(player).toString()
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_IN_TEAM_BLUE] = { player ->
            val game = gameService.getByPlayer(player)
            game?.blueTeam?.contains(player).toString()
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_IN_TEAM_RED] = { player ->
            val game = gameService.getByPlayer(player)
            game?.redTeam?.contains(player).toString()
        }

        // Stats PlaceHolders
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GOALS] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.scoredGoals?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GOALSFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.scoredGoalsFull?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_OWNGOALS] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.scoredOwnGoals?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_OWNGOALSFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.scoredOwnGoalsFull?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_TOTALGOALS] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)

                if (playerData != null) {
                    (playerData.statsMeta.scoredGoals + playerData.statsMeta.scoredOwnGoals).toString()
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_TOTALGOALSFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)

                if (playerData != null) {
                    (playerData.statsMeta.scoredGoalsFull + playerData.statsMeta.scoredOwnGoalsFull).toString()
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GAMES] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.joinedGames?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GAMESFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.playedGames?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_WINS] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.winsAmount?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_DRAWS] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                playerData?.statsMeta?.drawsAmount?.toString() ?: ""
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_LOSSES] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    (playerData.statsMeta.playedGames - playerData.statsMeta.winsAmount - playerData.statsMeta.drawsAmount).toString()
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_WINRATE] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.joinedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            playerData.statsMeta.winsAmount.toDouble() / playerData.statsMeta.joinedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_WINRATEFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.playedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            playerData.statsMeta.winsAmount.toDouble() / playerData.statsMeta.playedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GOALSPERGAME] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.joinedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            playerData.statsMeta.scoredGoals.toDouble() / playerData.statsMeta.joinedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_GOALSPERGAMEFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.playedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            playerData.statsMeta.scoredGoals.toDouble() / playerData.statsMeta.playedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_OWNGOALSPERGAME] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
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
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_OWNGOALSPERGAMEFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.playedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            playerData.statsMeta.scoredOwnGoalsFull.toDouble() / playerData.statsMeta.playedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_TOTALGOALSPERGAME] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.joinedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            (playerData.statsMeta.scoredOwnGoals + playerData.statsMeta.scoredGoals).toDouble() / playerData.statsMeta.joinedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_STATS_TOTALGOALSPERGAMEFULL] = { player ->
            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                "PatreonOnly"
            } else {
                val playerData = playerDataRepository.getCachedByPlayer(player)
                if (playerData != null) {
                    if (playerData.statsMeta.playedGames == 0) {
                        "0.00"
                    } else {
                        val result =
                            (playerData.statsMeta.scoredOwnGoalsFull + playerData.statsMeta.scoredGoalsFull).toDouble() / playerData.statsMeta.playedGames.toDouble()
                        String.format("%.2f", result)
                    }
                } else {
                    ""
                }
            }
        }
    }

    /**
     * Replaces the given text with properties from the given [game], optional [teamMeta] and optional size.
     */
    override fun replacePlaceHolders(
        text: String, player: Player?, game: SoccerGame?, teamMeta: TeamMeta?, currentTeamSize: Int?
    ): String {
        var output = text
        for (i in 0 until 4) {
            if (!output.contains("%")) {
                break
            }

            val locatedPlaceHolders = HashMap<String, String>()
            val characterCache = StringBuilder()

            for (character in output) {
                characterCache.append(character)

                if (character == '%') {
                    val evaluatedPlaceHolder = characterCache.toString()
                    if (placeHolders.containsKey(evaluatedPlaceHolder)) {
                        val placeHolder = placeHolders[evaluatedPlaceHolder]!!
                        if (!locatedPlaceHolders.containsKey(placeHolder.fullPlaceHolder)) {
                            if (game != null && gamePlayerHolderFunctions.containsKey(placeHolder)) {
                                locatedPlaceHolders[placeHolder.fullPlaceHolder] =
                                    gamePlayerHolderFunctions[placeHolder]!!.invoke(game)
                            } else if (player != null && playerPlaceHolderFunctions.containsKey(placeHolder)) {
                                locatedPlaceHolders[placeHolder.fullPlaceHolder] =
                                    playerPlaceHolderFunctions[placeHolder]!!.invoke(player)
                            } else if (game != null && teamMeta != null && currentTeamSize != null && teamPlaceHolderFunctions.containsKey(
                                    placeHolder
                                )
                            ) {
                                locatedPlaceHolders[placeHolder.fullPlaceHolder] =
                                    teamPlaceHolderFunctions[placeHolder]!!.invoke(game, teamMeta, currentTeamSize)
                            }
                        }
                    } else if (langPlaceHolderFunctions.containsKey(evaluatedPlaceHolder)) {
                        locatedPlaceHolders[evaluatedPlaceHolder] =
                            langPlaceHolderFunctions[evaluatedPlaceHolder]!!.invoke()
                    } else if (evaluatedPlaceHolder.startsWith("%blockball_leaderboard")) {
                        val parts = evaluatedPlaceHolder.split("_")
                        val changedPlaceHolder = parts.dropLast(2).joinToString("_")
                        val leaderBoardPlaceholder = PlaceHolderLeaderBoard.mapping[changedPlaceHolder]
                        if (leaderBoardPlaceholder != null) {
                            val leaderBoard = statsService.getLeaderBoard()

                            if (leaderBoard != null) {
                                locatedPlaceHolders[evaluatedPlaceHolder] = leaderBoardPlaceholder.f.invoke(leaderBoard, parts[parts.size - 1].substringBefore("%").toInt()-1)
                            }
                        }
                    }

                    characterCache.clear()
                    characterCache.append(character)
                }
            }

            for (locatedPlaceHolder in locatedPlaceHolders.keys) {
                output = output.replace(locatedPlaceHolder, locatedPlaceHolders[locatedPlaceHolder]!!)
            }
        }

        return output.translateChatColors()
    }
}
