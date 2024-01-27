package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.api.business.enumeration.GameState
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.contract.PlaceHolderService
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player

class PlaceHolderServiceImpl @Inject constructor(private val gameService: GameService) : PlaceHolderService {
    private val gamePlayerHolderFunctions = HashMap<PlaceHolder, ((Game) -> String)>()
    private val teamPlaceHolderFunctions = HashMap<PlaceHolder, ((Game, TeamMeta, Int) -> String)>()
    private val playerPlaceHolderFunctions = HashMap<PlaceHolder, ((Player) -> String)>()
    private val langPlaceHolderFunctions = HashMap<String, (() -> String)>()
    private val placeHolders = HashMap<String, PlaceHolder>()

    init {
        for (placeHolder in PlaceHolder.values()) {
            placeHolders[placeHolder.fullPlaceHolder] = placeHolder
        }

        for (field in BlockBallLanguage::class.java.declaredFields) {
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
        gamePlayerHolderFunctions[PlaceHolder.GAME_TEAM_RED_NAME] = { game -> game.arena.meta.redTeamMeta.displayName }
        gamePlayerHolderFunctions[PlaceHolder.GAME_TEAM_BLUE_NAME] =
            { game -> game.arena.meta.blueTeamMeta.displayName }
        gamePlayerHolderFunctions[PlaceHolder.GAME_TEAM_RED_COLOR] = { game -> game.arena.meta.redTeamMeta.prefix }
        gamePlayerHolderFunctions[PlaceHolder.GAME_TEAM_BLUE_COLOR] = { game -> game.arena.meta.blueTeamMeta.prefix }
        gamePlayerHolderFunctions[PlaceHolder.GAME_TIME] = { game ->
            if (game is MiniGame) {
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
        gamePlayerHolderFunctions[PlaceHolder.GAME_STATE] = { game -> game.status.name }
        gamePlayerHolderFunctions[PlaceHolder.GAME_STATE_DISPLAYNAME] = { game ->
            if (game.status == GameState.JOINABLE) {
                BlockBallLanguage.gameStatusJoinAble
            } else if (game.status == GameState.DISABLED) {
                BlockBallLanguage.gameStatusDisabled
            } else {
                BlockBallLanguage.gameStatusRunning
            }
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_REMAININGPLAYERS_TO_START] = { game ->
            (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size).toString()
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_IS_ENABLED] = { game ->
            game.arena.enabled.toString()
        }
        gamePlayerHolderFunctions[PlaceHolder.GAME_IS_JOINABLE] = { game ->
            (game.arena.enabled && game.status == GameState.JOINABLE).toString()
        }

        // Team PlaceHolders
        teamPlaceHolderFunctions[PlaceHolder.TEAM_NAME] = { game, team, teamSize ->
            team.displayName
        }
        teamPlaceHolderFunctions[PlaceHolder.TEAM_COLOR] = { game, team, teamSize ->
            team.prefix
        }
        teamPlaceHolderFunctions[PlaceHolder.TEAM_MAX_PLAYERS] = { game, team, teamSize ->
            team.maxAmount.toString()
        }
        teamPlaceHolderFunctions[PlaceHolder.TEAM_PLAYERS] = { game, team, teamSize ->
            teamSize.toString()
        }

        // Player PlaceHolders
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_INGAME] = { player ->
            val game = gameService.getGameFromPlayer(player)
            game.isPresent.toString()
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_IN_TEAM_BLUE] = { player ->
            val game = gameService.getGameFromPlayer(player)
            (game.isPresent && game.get().blueTeam.contains(player)).toString()
        }
        playerPlaceHolderFunctions[PlaceHolder.PLAYER_IS_IN_TEAM_RED] = { player ->
            val game = gameService.getGameFromPlayer(player)
            (game.isPresent && game.get().redTeam.contains(player)).toString()
        }
    }

    /**
     * Replaces the given text with properties from the given [game], optional [teamMeta] and optional size.
     */
    override fun replacePlaceHolders(
        text: String,
        player: Player?,
        game: Game?,
        teamMeta: TeamMeta?,
        currentTeamSize: Int?
    ): String {
        var output = text
        for(i in 0 until 4){
            if(!output.contains("%")){
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
                        locatedPlaceHolders[evaluatedPlaceHolder] = langPlaceHolderFunctions[evaluatedPlaceHolder]!!.invoke()
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
