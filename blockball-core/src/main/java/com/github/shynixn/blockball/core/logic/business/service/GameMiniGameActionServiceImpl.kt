@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.google.inject.Inject
import java.util.*
import kotlin.collections.ArrayList

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
    private val configurationService: ConfigurationService,
    private val screenMessageService: ScreenMessageService,
    private val soundService: SoundService,
    private val proxyService: ProxyService,
    private val gameSoccerService: GameSoccerService,
    private val gameExecutionService: GameExecutionService,
    private val concurrencyService: ConcurrencyService,
    private val placeholderService: PlaceholderService
) : GameMiniGameActionService {
    private val prefix = configurationService.findValue<String>("messages.prefix")

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: MiniGame) {
        game.spectatorPlayers.forEach { p ->
            leaveGame(game, p)
        }
    }

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: MiniGame, player: P, team: Team?): Boolean {
        require(player is Any)

        if (game.playing || game.isLobbyFull) {
            val b = ChatBuilderEntity().text(
                prefix + placeholderService.replacePlaceHolders(
                    game.arena.meta.spectatorMeta.spectateStartMessage[0],
                    game
                )
            )
                .nextLine()
                .component(
                    prefix + placeholderService.replacePlaceHolders(
                        game.arena.meta.spectatorMeta.spectateStartMessage[1],
                        game
                    )
                )
                .setClickAction(
                    ChatClickAction.RUN_COMMAND,
                    "/" + configurationService.findValue<String>("global-spectate.command") + " " + game.arena.name
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
            game.ingamePlayersStorage[player]!!.goalTeam = targetTeam
            return true
        }

        val storage = this.createPlayerStorage(game, player)
        game.ingamePlayersStorage[player] = storage
        proxyService.teleport(player, game.arena.meta.minigameMeta.lobbySpawnpoint!!)

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
        require(player is Any)

        if (game.spectatorPlayers.contains(player)) {
            resetStorage(player, game, game.spectatorPlayersStorage[player]!!)
            game.spectatorPlayersStorage.remove(player)
            proxyService.teleport(player, game.arena.meta.lobbyMeta.leaveSpawnpoint!!)
            return
        }

        if (!game.ingamePlayersStorage.containsKey(player)) {
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
        require(player is Any)

        if (game.spectatorPlayers.contains(player)) {
            return
        }

        if (!isAllowedToSpectateWithPermissions(game, player)) {
            return
        }

        val storage = createPlayerStorage(game, player)
        game.spectatorPlayersStorage[player] = storage
        proxyService.setGameMode(player, GameMode.SPECTATOR)
        proxyService.setPlayerFlying(player, true)

        if (game.arena.meta.spectatorMeta.spectateSpawnpoint != null) {
            proxyService.teleport(player, game.arena.meta.spectatorMeta.spectateSpawnpoint!!)
        } else {
            proxyService.teleport(player, game.arena.meta.ballMeta.spawnpoint!!)
        }
    }

    /**
     * Gets called when the given [game] ends with a draw.
     */
    override fun onDraw(game: MiniGame) {
        val additionalPlayers = getNotifiedPlayers(game).filter { pair -> pair.second }.map { p -> p.first }
        additionalPlayers.forEach { p ->
            screenMessageService.setTitle(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageTitle, game),
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageSubTitle, game)
            )
        }

        game.redTeam.forEach { p ->
            screenMessageService.setTitle(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageTitle, game),
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageSubTitle, game)
            )
        }
        game.blueTeam.forEach { p ->
            screenMessageService.setTitle(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.blueTeamMeta.drawMessageTitle, game),
                placeholderService.replacePlaceHolders(game.arena.meta.blueTeamMeta.drawMessageSubTitle, game)
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

        if (game.lobbyCountDownActive) {
            if (game.lobbyCountdown > 10) {
                val amountPlayers = game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount

                if (game.ingamePlayersStorage.size >= amountPlayers) {
                    game.lobbyCountdown = 10
                }
            }

            game.lobbyCountdown--

            game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                if (game.lobbyCountdown <= 10) {
                    proxyService.setPlayerExp(p, 1.0 - (game.lobbyCountdown.toFloat() / 10.0))
                }

                proxyService.setPlayerLevel(p, game.lobbyCountdown)
            }

            if (game.lobbyCountdown < 5) {
                game.ingamePlayersStorage.keys.forEach { p ->
                    soundService.playSound(proxyService.getEntityLocation<Any, Any>(p), game.blingSound, arrayListOf(p))
                }
            }

            if (game.lobbyCountdown <= 0) {
                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    if (game.lobbyCountdown <= 10) {
                        proxyService.setPlayerExp(p, 1.0)
                    }

                    proxyService.setPlayerLevel(p, 0)
                }

                game.lobbyCountDownActive = false
                game.playing = true
                game.status = GameStatus.RUNNING
                game.matchTimeIndex = -1

                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    val stats = game.ingamePlayersStorage[p]

                    if (stats!!.team == null) {
                        if (game.redTeam.size < game.blueTeam.size) {
                            stats.team = Team.RED
                            stats.goalTeam = Team.RED
                            joinTeam(game, p, Team.RED, game.arena.meta.redTeamMeta)
                        } else {
                            stats.team = Team.BLUE
                            stats.goalTeam = Team.BLUE
                            joinTeam(game, p, Team.BLUE, game.arena.meta.blueTeamMeta)
                        }
                    }
                }

                switchToNextMatchTime(game)
            }
        }

        if (!game.lobbyCountDownActive) {
            if (canStartLobbyCountdown(game)) {
                game.lobbyCountDownActive = true
                game.lobbyCountdown = game.arena.meta.minigameMeta.lobbyDuration
            } else if (!game.playing) {
                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    screenMessageService.setActionBar(
                        p,
                        placeholderService.replacePlaceHolders(
                            game.arena.meta.minigameMeta.playersRequiredToStartMessage,
                            game
                        )
                    )
                }
            }
        }

        if (game.playing) {
            game.gameCountdown--

            game.ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
                if (game.gameCountdown <= 10) {
                    proxyService.setPlayerExp(p, game.gameCountdown.toFloat() / 10.0)
                }

                if (game.gameCountdown <= 5) {
                    soundService.playSound(proxyService.getEntityLocation<Any, Any>(p), game.blingSound, arrayListOf(p))
                }

                proxyService.setPlayerLevel(p, game.gameCountdown)
            }

            if (game.gameCountdown <= 0) {
                switchToNextMatchTime(game)
            }

            if (game.ingamePlayersStorage.isEmpty()) {
                game.closing = true
            }
        }
    }

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    override fun switchToNextMatchTime(game: MiniGame) {
        game.matchTimeIndex++

        val matchTimes = game.arena.meta.minigameMeta.matchTimes

        if (game.matchTimeIndex >= matchTimes.size) {
            game.closing = true
            return
        }

        val matchTime = matchTimes[game.matchTimeIndex]
        val isLastMatchTimeSwap = (game.matchTimeIndex + 1) >= matchTimes.size

        if (isLastMatchTimeSwap) {
            timeAlmostUp(game)
        }

        game.gameCountdown = matchTime.duration

        if (matchTime.isSwitchGoalsEnabled) {
            game.mirroredGoals = !game.mirroredGoals

            game.ingamePlayersStorage.values.forEach { e ->
                if (e.goalTeam != null) {
                    if (e.goalTeam == Team.RED) {
                        e.goalTeam = Team.BLUE
                    } else {
                        e.goalTeam = Team.RED
                    }
                }
            }
        }

        game.ballEnabled = matchTime.playAbleBall

        if (!game.ballEnabled && game.ball != null && !game.ball!!.isDead) {
            game.ball!!.remove()
        }

        game.ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
            if (matchTime.respawnEnabled || game.matchTimeIndex == 0) {
                gameExecutionService.respawn(game, p)
            }

            if (!matchTime.startMessageTitle.isBlank() || !matchTime.startMessageSubTitle.isBlank()) {
                concurrencyService.runTaskSync(20L * 3) {
                    screenMessageService.setTitle(
                        p,
                        placeholderService.replacePlaceHolders(matchTime.startMessageTitle, game),
                        placeholderService.replacePlaceHolders(matchTime.startMessageSubTitle, game)
                    )
                }
            }

            proxyService.setPlayerExp(p, 1.0)
        }
    }

    private fun createPlayerStorage(game: MiniGame, player: Any): GameStorage {
        val stats = GameStorageEntity(UUID.fromString(proxyService.getPlayerUUID(player)))
        stats.gameMode = proxyService.getPlayerGameMode(player)
        stats.armorContents = proxyService.getPlayerInventoryArmorCopy(player)
        stats.inventoryContents = proxyService.getPlayerInventoryCopy(player)
        stats.flying = proxyService.getPlayerFlying(player)
        stats.allowedFlying = proxyService.getPlayerAllowFlying(player)
        stats.walkingSpeed = proxyService.getPlayerWalkingSpeed(player)
        stats.scoreboard = proxyService.getPlayerScoreboard(player)
        stats.level = proxyService.getPlayerLevel(player)
        stats.exp = proxyService.getPlayerExp(player)
        stats.maxHealth = proxyService.getPlayerMaxHealth(player)
        stats.health = proxyService.getPlayerHealth(player)
        stats.hunger = proxyService.getPlayerHunger(player)

        proxyService.setPlayerAllowFlying(player, false)
        proxyService.setPlayerFlying(player, false)
        proxyService.setPlayerMaxHealth(player, 20.0)
        proxyService.setPlayerHunger(player, 20)
        proxyService.setPlayerLevel(player, 0)
        proxyService.setPlayerExp(player, 0.0)
        proxyService.setGameMode(player, game.arena.meta.lobbyMeta.gamemode)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            proxyService.setInventoryContents(player, arrayOfNulls<Any?>(36), arrayOfNulls<Any?>(4))
        }

        return stats
    }

    /**
     * Joins the [player] to the given [teamMeta].
     */
    private fun joinTeam(game: MiniGame, player: Any, team: Team, teamMeta: TeamMeta) {
        proxyService.setPlayerWalkingSpeed(player, teamMeta.walkingSpeed)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            proxyService.setInventoryContents(
                player,
                teamMeta.inventoryContents.clone(),
                teamMeta.armorContents.clone()
            )
        }

        val players = if (team == Team.RED) {
            game.redTeam
        } else {
            game.blueTeam
        }

        proxyService.sendMessage(
            player, prefix + placeholderService.replacePlaceHolders(
                teamMeta.joinMessage,
                game,
                teamMeta,
                players.size
            )
        )
    }

    /**
     * Returns if the given [player] is allowed to spectate the match.
     */
    private fun isAllowedToSpectateWithPermissions(game: MiniGame, player: Any): Boolean {
        val hasSpectatingPermission = proxyService.hasPermission(player, Permission.SPECTATE.permission + ".all")
                || proxyService.hasPermission(player, Permission.SPECTATE.permission + "." + game.arena.name)

        if (hasSpectatingPermission) {
            return true
        }

        proxyService.sendMessage(
            player,
            prefix + configurationService.findValue<String>("messages.no-permission-spectate-game")
        )

        return false
    }

    /**
     * Gets called when the game ends.
     */
    private fun timeAlmostUp(game: MiniGame) {
        when {
            game.redScore == game.blueScore -> {
                gameSoccerService.onMatchEnd<Any>(game, null, null)
                this.onDraw(game)
            }
            game.redScore > game.blueScore -> {
                gameSoccerService.onMatchEnd(game, game.redTeam, game.blueTeam)
                gameSoccerService.onWin(game, Team.RED, game.arena.meta.redTeamMeta)
            }
            else -> {
                gameSoccerService.onMatchEnd(game, game.blueTeam, game.redTeam)
                gameSoccerService.onWin(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            }
        }

        // OnWin sets game.closing to true.
        game.closing = false
    }

    /**
     * Get notified players.
     */
    private fun getNotifiedPlayers(game: MiniGame): List<Pair<Any, Boolean>> {
        val players = ArrayList<Pair<Any, Boolean>>()

        if (game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            val playersInWorld = proxyService.getPlayersInWorld<Any, Any>(game.arena.center as Any)
            for (p in playersInWorld) {
                val position = proxyService.toPosition(proxyService.getEntityLocation<Any, Any>(p))
                if (position.distance(game.arena.center) <= game.arena.meta.spectatorMeta.notificationRadius) {
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
    private fun resetStorage(player: Any, game: Game, stats: GameStorage) {
        proxyService.setGameMode(player, stats.gameMode)
        proxyService.setPlayerAllowFlying(player, stats.allowedFlying)
        proxyService.setPlayerFlying(player, stats.flying)
        proxyService.setPlayerWalkingSpeed(player, stats.walkingSpeed)
        proxyService.setPlayerScoreboard(player, stats.scoreboard)
        proxyService.setPlayerLevel(player, stats.level)
        proxyService.setPlayerExp(player, stats.exp)
        proxyService.setPlayerMaxHealth(player, stats.maxHealth)
        proxyService.setPlayerHealth(player, stats.health)
        proxyService.setPlayerHunger(player, stats.hunger)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            proxyService.setInventoryContents(player, stats.inventoryContents, stats.armorContents)
        }
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
}
