@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.GameExecutionService
import com.github.shynixn.blockball.api.business.service.GameHubGameActionService
import com.github.shynixn.blockball.api.persistence.entity.HubGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toGameMode
import com.github.shynixn.blockball.bukkit.logic.business.extension.updateInventory
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Scoreboard

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
class GameHubGameActionServiceImpl @Inject constructor(configurationService: ConfigurationService, private val gameExecutionService: GameExecutionService) :
    GameHubGameActionService {
    private val prefix = configurationService.findValue<String>("messages.prefix")

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: HubGame, player: P, team: Team?): Boolean {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

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
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!game.ingamePlayersStorage.containsKey(player)) {
            return
        }

        val stats = game.ingamePlayersStorage[player]!!

        with(player as Player) {
            gameMode = stats.gameMode as GameMode
            allowFlight = stats.allowedFlying
            isFlying = stats.flying
            walkSpeed = stats.walkingSpeed
            scoreboard = stats.scoreboard as Scoreboard
        }

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents as Array<out ItemStack>
            player.inventory.setArmorContents(stats.armorContents as Array<out ItemStack>)
            player.inventory.updateInventory()
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
    private fun prepareLobbyStorageForPlayer(game: HubGame, player: Player, team: Team, teamMeta: TeamMeta) {
        val stats = GameStorageEntity(player.uniqueId, Bukkit.getScoreboardManager()!!.newScoreboard)

        with(stats) {
            this.team = team
            gameMode = player.gameMode
            armorContents = player.inventory.armorContents.clone() as Array<Any?>
            flying = player.isFlying
            allowedFlying = player.allowFlight
            walkingSpeed = player.walkSpeed
            scoreboard = player.scoreboard
            inventoryContents = player.inventory.contents.clone() as Array<Any?>
            level = player.level
            exp = player.exp
            @Suppress("DEPRECATION")
            maxHealth = player.maxHealth
            health = player.health
            hunger = player.foodLevel
        }

        game.ingamePlayersStorage[player] = stats

        player.gameMode = game.arena.meta.lobbyMeta.gamemode.toGameMode()
        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = teamMeta.inventoryContents.clone().map { d -> d as ItemStack? }.toTypedArray()
            player.inventory.setArmorContents(teamMeta.armorContents.clone().map { d -> d as ItemStack? }.toTypedArray())
            player.inventory.updateInventory()
        }

        if (game.arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.gameExecutionService.respawn(game, player)
        } else {
            player.velocity = player.location.direction.normalize().multiply(0.5)
        }

        player.sendMessage(prefix + teamMeta.joinMessage.replaceGamePlaceholder(game, teamMeta))
    }
}