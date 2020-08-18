@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.HubGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.google.inject.Inject
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
class GameHubGameActionServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val proxyService: ProxyService,
    private val gameExecutionService: GameExecutionService,
    private val placeholderService: PlaceholderService
) :
    GameHubGameActionService {
    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: HubGame, player: P, team: Team?): Boolean {
        require(player is Any)
        var joiningTeam = team

        if (game.arena.meta.lobbyMeta.onlyAllowEventTeams) {
            if (joiningTeam == Team.RED && game.redTeam.size > game.blueTeam.size) {
                joiningTeam = null
            } else if (joiningTeam == Team.BLUE && game.blueTeam.size > game.redTeam.size) {
                joiningTeam = null
            }
        }

        if (joiningTeam == null) {
            joiningTeam = Team.BLUE
            if (game.redTeam.size < game.blueTeam.size) {
                joiningTeam = Team.RED
            }
        }

        if (joiningTeam == Team.RED && game.redTeam.size < game.arena.meta.redTeamMeta.maxAmount) {
            this.prepareLobbyStorageForPlayer(game, player, joiningTeam, game.arena.meta.redTeamMeta)
            return true

        } else if (joiningTeam == Team.BLUE && game.blueTeam.size < game.arena.meta.blueTeamMeta.maxAmount) {
            this.prepareLobbyStorageForPlayer(game, player, joiningTeam, game.arena.meta.blueTeamMeta)
            return true
        }

        return false
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun <P> leaveGame(game: HubGame, player: P) {
        require(player is Any)

        if (!game.ingamePlayersStorage.containsKey(player)) {
            return
        }

        val stats = game.ingamePlayersStorage[player]!!
        proxyService.setGameMode(player, stats.gameMode)
        proxyService.setPlayerAllowFlying(player, stats.allowedFlying)
        proxyService.setPlayerFlying(player, stats.flying)
        proxyService.setPlayerWalkingSpeed(player, stats.walkingSpeed)
        proxyService.setPlayerScoreboard(player, stats.scoreboard)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            proxyService.setInventoryContents(player, stats.inventoryContents, stats.armorContents)
        }
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: HubGame, ticks: Int) {
        if (ticks < 20) {
            return
        }

        if (game.arena.meta.hubLobbyMeta.resetArenaOnEmpty && game.ingamePlayersStorage.isEmpty() && (game.redScore > 0 || game.blueScore > 0)) {
            game.closing = true
        }
    }

    /**
     * Prepares the storage for a hubgame.
     */
    private fun prepareLobbyStorageForPlayer(game: HubGame, player: Any, team: Team, teamMeta: TeamMeta) {
        val uuid = proxyService.getPlayerUUID(player)
        val stats = GameStorageEntity(UUID.fromString(uuid))
        game.ingamePlayersStorage[player] = stats

        stats.scoreboard = proxyService.generateNewScoreboard()
        stats.team = team
        stats.goalTeam = team
        stats.gameMode = proxyService.getPlayerGameMode(player)
        stats.flying = proxyService.getPlayerFlying(player)
        stats.allowedFlying = proxyService.getPlayerAllowFlying(player)
        stats.walkingSpeed = proxyService.getPlayerWalkingSpeed(player)
        stats.scoreboard = proxyService.getPlayerScoreboard(player)
        stats.armorContents = proxyService.getPlayerInventoryArmorCopy(player)
        stats.inventoryContents = proxyService.getPlayerInventoryCopy(player)
        stats.level = proxyService.getPlayerLevel(player)
        stats.exp = proxyService.getPlayerExp(player)
        stats.maxHealth = proxyService.getPlayerMaxHealth(player)
        stats.health = proxyService.getPlayerHealth(player)
        stats.hunger = proxyService.getPlayerHunger(player)

        proxyService.setGameMode(player, game.arena.meta.lobbyMeta.gamemode)
        proxyService.setPlayerAllowFlying(player, false)
        proxyService.setPlayerFlying(player, false)
        proxyService.setPlayerWalkingSpeed(player, teamMeta.walkingSpeed)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            proxyService.setInventoryContents(
                player,
                teamMeta.inventoryContents,
                teamMeta.armorContents
            )
        }

        if (game.arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.gameExecutionService.respawn(game, player)
        } else {
            val velocityIntoArena = proxyService.getPlayerDirection(player).normalize().multiply(0.5)
            proxyService.setEntityVelocity(player, velocityIntoArena)
        }

        val prefix = configurationService.findValue<String>("messages.prefix")
        val message = prefix + placeholderService.replacePlaceHolders(teamMeta.joinMessage, game, teamMeta)
        proxyService.sendMessage(player, message)
    }
}