package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.GameExecutionService
import com.github.shynixn.blockball.contract.GameHubGameActionService
import com.github.shynixn.blockball.contract.PlaceHolderService
import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.entity.GameStorage
import com.github.shynixn.blockball.entity.HubGame
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.Team
import com.google.inject.Inject
import org.bukkit.entity.Player
import java.util.*

class GameHubGameActionServiceImpl @Inject constructor(
    private val proxyService: ProxyService,
    private val gameExecutionService: GameExecutionService,
    private val placeholderService: PlaceHolderService
) :
    GameHubGameActionService {
    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun joinGame(game: HubGame, player: Player, team: Team?): Boolean {
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
    override fun leaveGame(game: HubGame, player: Player) {
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
    private fun prepareLobbyStorageForPlayer(game: HubGame, player: Player, team: Team, teamMeta: TeamMeta) {
        val uuid = proxyService.getPlayerUUID(player)
        val stats = GameStorage(UUID.fromString(uuid))
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

        val message = placeholderService.replacePlaceHolders(teamMeta.joinMessage, player, game, teamMeta, 0)
        proxyService.sendMessage(player, message)
    }
}
