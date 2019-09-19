@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ChatClickAction
import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.Permission
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toGameMode
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.updateInventory
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
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
class GameMiniGameActionServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val configurationService: ConfigurationService,
    private val screenMessageService: ScreenMessageService,
    private val soundService: SoundService,
    private val proxyService: ProxyService,
    private val gameSoccerService: GameSoccerService,
    private val gameExecutionService: GameExecutionService,
    private val loggingService: LoggingService
) : GameMiniGameActionService {
    private val prefix = configurationService.findValue<String>("messages.prefix")

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: MiniGame) {
        game.spectatorPlayers.map { p -> p as Player }.forEach { p ->
            leaveGame(game, p)
        }
    }

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: MiniGame, player: P, team: Team?): Boolean {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        loggingService.debug("Player " + player.name + " has joined game " + game.arena.name + " " + game.arena.displayName)

        if (game.playing || game.endGameActive || game.isLobbyFull) {
            val b = ChatBuilderEntity().text(prefix + game.arena.meta.spectatorMeta.spectateStartMessage[0].replaceGamePlaceholder(game))
                .nextLine()
                .component(prefix + game.arena.meta.spectatorMeta.spectateStartMessage[1].replaceGamePlaceholder(game))
                .setClickAction(
                    ChatClickAction.RUN_COMMAND
                    , "/" + plugin.config.getString("global-spectate.command") + " " + game.arena.name
                )
                .setHoverText(" ")
                .builder()

            proxyService.sendMessage(player, b)

            return false
        }

        if (game.ingamePlayersStorage.containsKey(player) && team != null) {
            var targetTeam = team
            val amount = getAmountOfQueuedPlayersInThisTeam(game, targetTeam)

            if (game.arena.meta.lobbyMeta.onlyAllowEventTeams) {
                val blueTeamAmount = getAmountOfQueuedPlayersInThisTeam(game, Team.BLUE)
                val redTeamAmount = getAmountOfQueuedPlayersInThisTeam(game, Team.RED)

                if (blueTeamAmount > redTeamAmount) {
                    targetTeam = Team.RED
                } else if (blueTeamAmount < redTeamAmount) {
                    targetTeam = Team.BLUE
                }
            }

            if (targetTeam == Team.RED) {
                if (amount >= game.arena.meta.redTeamMeta.maxAmount) {
                    return false
                }

                joinTeam(game, player, Team.RED, game.arena.meta.redTeamMeta)
            } else if (targetTeam == Team.BLUE) {
                if (amount >= game.arena.meta.blueTeamMeta.maxAmount) {
                    return false
                }

                joinTeam(game, player, Team.BLUE, game.arena.meta.blueTeamMeta)
            }

            game.ingamePlayersStorage[player]!!.team = targetTeam
            return true
        }

        val storage = this.createPlayerStorage(game, player)
        @Suppress("USELESS_CAST")
        game.ingamePlayersStorage[player as Player] = storage

        if (team != null) {
            joinGame(game, player, team)
        }

        return true
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun <P> leaveGame(game: MiniGame, player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        loggingService.debug("Player " + player.name + " tries to leave game " + game.arena.name + " " + game.arena.displayName)

        if (game.spectatorPlayers.contains(player)) {
            resetStorage(player, game, game.spectatorPlayersStorage[player]!!)
            player.teleport(game.arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
            game.spectatorPlayersStorage.remove(player)

            loggingService.debug("Player " + player.name + " has left as spectator.")

            return
        }

        if (!game.ingamePlayersStorage.containsKey(player)) {
            loggingService.debug("Player " + player.name + " has left without restorring storage.")
            return
        }

        val stats = game.ingamePlayersStorage[player]!!
        resetStorage(player, game, stats)
    }

    /**
     * Lets the given [player] leave spectate the given [game].
     * Does nothing if the player is already spectating a Game.
     */
    override fun <P> spectateGame(game: MiniGame, player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (game.spectatorPlayers.contains(player)) {
            return
        }

        if (!isAllowedToSpectateWithPermissions(game, player)) {
            return
        }

        if (game.arena.meta.spectatorMeta.spectateSpawnpoint != null) {
            player.teleport(game.arena.meta.spectatorMeta.spectateSpawnpoint!!.toLocation())
        } else {
            player.teleport(game.arena.meta.ballMeta.spawnpoint!!.toLocation())
        }

        val storage = createPlayerStorage(game, player)
        @Suppress("USELESS_CAST")
        game.spectatorPlayersStorage[player as Player] = storage

        player.gameMode = GameMode.SPECTATOR
    }

    /**
     * Gets called when the given [game] ends with a draw.
     */
    override fun onDraw(game: MiniGame) {
        val additionalPlayers = getNofifiedPlayers(game).filter { pair -> pair.second }.map { p -> p.first }
        additionalPlayers.forEach { p ->
            screenMessageService.setTitle(
                p,
                game.arena.meta.redTeamMeta.drawMessageTitle.replaceGamePlaceholder(game),
                game.arena.meta.redTeamMeta.drawMessageSubTitle.replaceGamePlaceholder(game)
            )
        }

        game.redTeam.forEach { p ->
            screenMessageService.setTitle(
                p,
                game.arena.meta.redTeamMeta.drawMessageTitle.replaceGamePlaceholder(game),
                game.arena.meta.redTeamMeta.drawMessageSubTitle.replaceGamePlaceholder(game)
            )
        }
        game.blueTeam.forEach { p ->
            screenMessageService.setTitle(
                p,
                game.arena.meta.blueTeamMeta.drawMessageTitle.replaceGamePlaceholder(game),
                game.arena.meta.blueTeamMeta.drawMessageSubTitle.replaceGamePlaceholder(game)
            )
        }
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: MiniGame, ticks: Int) {
        if (ticks < 20) {
            return
        }

        if (game.endGameActive) {
            if (game.ball != null) {
                game.ball!!.remove()
                game.ball = null
            }

            game.gameCountdown--

            game.ingamePlayersStorage.keys.toTypedArray().map { p -> p as Player }.forEach { p ->
                if (game.gameCountdown <= 10) {
                    p.exp = (game.gameCountdown.toFloat() / 10.0F)
                }

                p.level = game.gameCountdown
            }

            if (game.gameCountdown <= 0) {
                game.closing = true
            }

            return
        }

        if (game.lobbyCountDownActive) {

            if (game.lobbyCountdown > 10) {
                val amountPlayers = game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount

                if (game.ingamePlayersStorage.size >= amountPlayers) {
                    game.lobbyCountdown = 10
                }
            }

            game.lobbyCountdown--

            game.ingamePlayersStorage.keys.toTypedArray().map { p -> p as Player }.forEach { p ->
                if (game.lobbyCountdown <= 10) {
                    p.exp = 1.0F - (game.lobbyCountdown.toFloat() / 10.0F)
                }

                p.level = game.lobbyCountdown
            }

            if (game.lobbyCountdown < 5) {
                game.ingamePlayersStorage.keys.map { p -> p as Player }.forEach { p ->
                    soundService.playSound(p.location, game.blingSound, arrayListOf(p))
                }
            }

            if (game.lobbyCountdown <= 0) {
                game.ingamePlayersStorage.keys.map { p -> p as Player }.toTypedArray().forEach { p ->
                    if (game.lobbyCountdown <= 10) {
                        p.exp = 1.0F
                    }

                    p.level = 0
                }

                game.gameCountdown = game.arena.meta.minigameMeta.matchDuration
                game.lobbyCountDownActive = false
                game.playing = true
                startGame(game)
            }
        }

        if (!game.lobbyCountDownActive) {
            if (canStartLobbyCountdown(game)) {
                game.lobbyCountDownActive = true
                game.lobbyCountdown = game.arena.meta.minigameMeta.lobbyDuration
            } else if (!game.playing) {
                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    screenMessageService.setActionBar(p, game.arena.meta.minigameMeta.playersRequiredToStartMessage.replaceGamePlaceholder(game))
                }
            }
        }

        if (game.playing) {
            game.gameCountdown--

            game.ingamePlayersStorage.keys.toTypedArray().map { p -> p as Player }.forEach { p ->
                if (game.gameCountdown <= 10) {
                    p.exp = (game.gameCountdown.toFloat() / 10.0F)
                }
                p.level = game.gameCountdown
            }

            if (game.gameCountdown <= 0) {
                setEndGame(game)
                timesUpGame(game)
            }

            if (game.ingamePlayersStorage.isEmpty()) {
                game.closing = true
            }
        }
    }

    private fun createPlayerStorage(game: MiniGame, player: Player): GameStorage {
        val stats = GameStorageEntity(player.uniqueId, Bukkit.getScoreboardManager()!!.newScoreboard)

        loggingService.debug("Created a temporary storage for player " + player.name + ".")

        with(stats) {
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

        loggingService.debug("PlayerStorage was filled for player " + player.name + ".")

        player.allowFlight = false
        player.isFlying = false
        @Suppress("DEPRECATION")
        player.maxHealth = 20.0
        player.health = 20.0
        player.foodLevel = 20
        player.level = 0
        player.exp = 0.0F
        player.gameMode = game.arena.meta.lobbyMeta.gamemode.toGameMode()

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.setArmorContents(arrayOfNulls(4))
            player.inventory.clear()
            player.inventory.updateInventory()
        }

        player.teleport(game.arena.meta.minigameMeta.lobbySpawnpoint!!.toLocation())

        return stats
    }

    /**
     * Joins the [player] to the given [teamMeta].
     */
    private fun joinTeam(game: MiniGame, player: Player, team: Team, teamMeta: TeamMeta) {
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = teamMeta.inventoryContents.clone().map { d -> d as ItemStack? }.toTypedArray()
            player.inventory.setArmorContents(teamMeta.armorContents.clone().map { d -> d as ItemStack? }.toTypedArray())
            player.inventory.updateInventory()
        }

        val players = if (team == Team.RED) {
            game.redTeam as List<Player>
        } else {
            game.blueTeam as List<Player>
        }

        player.sendMessage(prefix + teamMeta.joinMessage.replaceGamePlaceholder(game, teamMeta, players))
    }

    /**
     * Returns if the given [player] is allowed to spectate the match.
     */
    private fun isAllowedToSpectateWithPermissions(game: MiniGame, player: Player): Boolean {
        if (player.hasPermission(Permission.SPECTATE.permission + ".all")
            || player.hasPermission(Permission.SPECTATE.permission + "." + game.arena.name)
        ) {
            return true
        }

        player.sendMessage(prefix + configurationService.findValue<String>("messages.no-permission-spectate-game"))

        return false
    }

    /**
     * Starts the game.
     */
    private fun startGame(game: MiniGame) {
        game.status = GameStatus.RUNNING
        game.ingamePlayersStorage.keys.toTypedArray().map { p -> p as Player }.forEach { p ->
            val stats = game.ingamePlayersStorage[p]

            if (stats!!.team == null) {
                if (game.redTeam.size < game.blueTeam.size) {
                    stats.team = Team.RED
                    joinTeam(game, p, Team.RED, game.arena.meta.redTeamMeta)
                } else {
                    stats.team = Team.BLUE
                    joinTeam(game, p, Team.BLUE, game.arena.meta.blueTeamMeta)
                }
            }

            gameExecutionService.respawn(game, p)
        }
    }

    /**
     * Returns the amount of queues players.
     */
    private fun getAmountOfQueuedPlayersInThisTeam(game: MiniGame, team: Team): Int {
        var amount = 0

        game.ingamePlayersStorage.values.forEach { p ->
            if (p.team != null && p.team == team) {
                amount++
            }
        }

        return amount
    }

    /**
     * Returns if the lobby countdown can already be started.
     * @return canStart
     */
    private fun canStartLobbyCountdown(game: MiniGame): Boolean {
        val amount = game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount

        if (!game.playing && game.ingamePlayersStorage.size >= amount && game.ingamePlayersStorage.isNotEmpty()) {
            return true
        }

        return false
    }

    /**
     * Gets called when the game ends.
     */
    private fun timesUpGame(game: MiniGame) {
        if (game.ball != null) {
            game.ball!!.remove()
        }

        when {
            game.redScore == game.blueScore -> {
                gameSoccerService.onMatchEnd<Player>(game, null, null)
                this.onDraw(game)
            }
            game.redScore > game.blueScore -> {
                gameSoccerService.onMatchEnd(game, game.redTeam as List<Player>, game.blueTeam as List<Player>)
                gameSoccerService.onWin(game, Team.RED, game.arena.meta.redTeamMeta)
            }
            else -> {
                gameSoccerService.onMatchEnd(game, game.blueTeam as List<Player>, game.redTeam as List<Player>)
                gameSoccerService.onWin(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            }
        }
    }

    /**
     * Sets the game ending with 10 seconds cooldown.
     */
    private fun setEndGame(game: MiniGame) {
        if (!game.endGameActive) {
            game.gameCountdown = 10
        }

        game.endGameActive = true
        game.ballSpawning = true
    }

    /**
     * Get nofified players.
     */
    private fun getNofifiedPlayers(game: MiniGame): List<Pair<Any, Boolean>> {
        val players = ArrayList<Pair<Any, Boolean>>()

        if (game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            game.arena.center.toLocation().world!!.players.forEach { p ->
                if (p.location.distance(game.arena.center.toLocation()) <= game.arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(p, true))
                } else {
                    players.add(Pair(p, false))
                }
            }
        }

        players.addAll(game.spectatorPlayers.map { p -> Pair(p, true) })

        return players
    }

    /**
     * Resets the storage of the given [player].
     */
    private fun resetStorage(player: Player, game: Game, stats: GameStorage) {
        with(player) {
            gameMode = stats.gameMode as GameMode
            allowFlight = stats.allowedFlying
            isFlying = stats.flying
            walkSpeed = stats.walkingSpeed
            scoreboard = stats.scoreboard as Scoreboard
            level = stats.level
            exp = stats.exp
            @Suppress("DEPRECATION")
            maxHealth = stats.maxHealth
            health = stats.health
            foodLevel = stats.hunger
        }

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents as Array<out ItemStack>
            player.inventory.setArmorContents(stats.armorContents as Array<out ItemStack>)
            player.inventory.updateInventory()
        }

        loggingService.debug("The inventory of player " + player.name + " was restored.")
    }
}