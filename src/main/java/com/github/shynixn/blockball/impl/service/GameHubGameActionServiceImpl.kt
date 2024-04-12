package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.GameExecutionService
import com.github.shynixn.blockball.contract.GameHubGameActionService
import com.github.shynixn.blockball.contract.PlaceHolderService
import com.github.shynixn.blockball.entity.GameStorage
import com.github.shynixn.blockball.entity.HubGame
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.Team
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

class GameHubGameActionServiceImpl @Inject constructor(
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
        player.gameMode = stats.gameMode
        player.allowFlight =  stats.allowedFlying
        player.isFlying = player.allowFlight
        player.walkSpeed = stats.walkingSpeed.toFloat()
        player.scoreboard = stats.scoreboard as Scoreboard

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents.clone()
            player.inventory.setArmorContents(stats.armorContents.clone())
            player.updateInventory()
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
        val stats = GameStorage()
        game.ingamePlayersStorage[player] = stats

        stats.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        stats.team = team
        stats.goalTeam = team
        stats.gameMode = player.gameMode
        stats.flying = player.isFlying
        stats.allowedFlying = player.allowFlight
        stats.walkingSpeed = player.walkSpeed.toDouble()
        stats.scoreboard = player.scoreboard
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
        stats.level = player.level
        stats.exp = player.exp.toDouble()
        stats.maxHealth = player.maxHealth
        stats.health = player.health
        stats.hunger = player.foodLevel

        player.gameMode = game.arena.meta.lobbyMeta.gamemode
        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = teamMeta.inventory.map {
                if (it != null) {
                    val configuration = org.bukkit.configuration.file.YamlConfiguration()
                    configuration.loadFromString(it)
                    configuration.getItemStack("item")
                } else {
                    null
                }
            }.toTypedArray()
            player.inventory.setArmorContents(
                teamMeta.armor.map {
                    if (it != null) {
                        val configuration = org.bukkit.configuration.file.YamlConfiguration()
                        configuration.loadFromString(it)
                        configuration.getItemStack("item")
                    } else {
                        null
                    }
                }.toTypedArray()
            )
            player.updateInventory()
        }

        if (game.arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.gameExecutionService.respawn(game, player)
        } else {
            val velocityIntoArena = player.location.direction.normalize().multiply(0.5)
            player.velocity = velocityIntoArena
        }

        val message = placeholderService.replacePlaceHolders(teamMeta.joinMessage, player, game, teamMeta, 0)
        player.sendMessage(message)
    }
}
