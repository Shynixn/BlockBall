package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.PlaceholderService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.google.inject.Inject

/**
 * Created by Shynixn 2020.
 * <p>
 * Version 1.5
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2020 by Shynixn
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
class PlaceholderServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val proxyService: ProxyService
) : PlaceholderService {
    /**
     * Replaces the given text with properties from the given [game], optional [teamMeta] and optional size.
     */
    override fun replacePlaceHolders(text: String, game: Game, teamMeta: TeamMeta?, currentTeamSize: Int?): String {
        var cache = text.replace(PlaceHolder.TEAM_RED.placeHolder, game.arena.meta.redTeamMeta.displayName)
            .replace(PlaceHolder.ARENA_DISPLAYNAME.placeHolder, game.arena.displayName)
            .replace(PlaceHolder.TEAM_BLUE.placeHolder, game.arena.meta.blueTeamMeta.displayName)
            .replace(PlaceHolder.RED_COLOR.placeHolder, game.arena.meta.redTeamMeta.prefix)
            .replace(PlaceHolder.BLUE_COLOR.placeHolder, game.arena.meta.blueTeamMeta.prefix)
            .replace(PlaceHolder.RED_GOALS.placeHolder, game.redScore.toString())
            .replace(PlaceHolder.BLUE_GOALS.placeHolder, game.blueScore.toString())
            .replace(PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder, game.ingamePlayersStorage.size.toString())
            .replace(
                PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder,
                (game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount).toString()
            )


        if (teamMeta != null) {
            cache = cache.replace(PlaceHolder.ARENA_TEAMCOLOR.placeHolder, teamMeta.prefix)
                .replace(PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder, teamMeta.displayName)
                .replace(PlaceHolder.ARENA_MAX_PLAYERS_ON_TEAM.placeHolder, teamMeta.maxAmount.toString())
        }

        if (currentTeamSize != null) {
            cache = cache.replace(PlaceHolder.ARENA_PLAYERS_ON_TEAM.placeHolder, currentTeamSize.toString())
        }

        val stateSignEnabled = configurationService.findValue<String>("messages.state-sign-enabled")
        val stateSignDisabled = configurationService.findValue<String>("messages.state-sign-disabled")
        val stateSignRunning = configurationService.findValue<String>("messages.state-sign-running")

        cache = when (game.status) {
            GameStatus.RUNNING -> cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignRunning)
            GameStatus.ENABLED -> cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignEnabled)
            GameStatus.DISABLED -> cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignDisabled)
        }

        if (game.arena.gameType == GameType.HUBGAME) {
            cache = cache.replace(PlaceHolder.TIME.placeHolder, "∞")
        } else if (game is MiniGame) {
            cache = cache.replace(PlaceHolder.TIME.placeHolder, game.gameCountdown.toString())
                .replace(
                    PlaceHolder.REMAINING_PLAYERS_TO_START.placeHolder,
                    (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size).toString()
                )
        }

        if (game.lastInteractedEntity != null && proxyService.isPlayerInstance(game.lastInteractedEntity)) {
            cache = cache.replace(
                PlaceHolder.LASTHITBALL.placeHolder,
                proxyService.getPlayerName(game.lastInteractedEntity)
            )
        }

        return cache.translateChatColors()
    }
}