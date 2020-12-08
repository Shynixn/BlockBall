@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.core.logic.business.proxy.PacketHologram
import com.github.shynixn.blockball.core.logic.persistence.event.GameJoinEventEntity
import com.github.shynixn.blockball.core.logic.persistence.event.GameLeaveEventEntity
import com.google.inject.Inject

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
class GameActionServiceImpl @Inject constructor(
    private val pluginProxy: PluginProxy,
    private val gameHubGameActionService: GameHubGameActionService,
    private val bossBarService: BossBarService,
    private val configurationService: ConfigurationService,
    private val hubGameActionService: GameHubGameActionService,
    private val minigameActionService: GameMiniGameActionService,
    private val bungeeCordGameActionService: GameBungeeCordGameActionService,
    private val scoreboardService: ScoreboardService,
    private val dependencyService: DependencyService,
    private val dependencyBossBarApiService: DependencyBossBarApiService,
    private val gameSoccerService: GameSoccerService,
    private val placeholderService: PlaceholderService,
    private val eventService: EventService,
    private val proxyService: ProxyService,
    private val loggingService: LoggingService,
    private val packetService: PacketService,
    private val concurrencyService: ConcurrencyService
) : GameActionService {
    private val prefix = configurationService.findValue<String>("messages.prefix")

    // Cyclic Reference reason why it cannot be constructor parameter.
    private val gameService: GameService by lazy {
        BlockBallApi.resolve(GameService::class.java)
    }

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: Game, player: P, team: Team?): Boolean {
        require(player is Any)

        if (!isAllowedToJoinWithPermissions(game, player)) {
            return false
        }

        gameService.getGameFromPlayer(player).ifPresent { g ->
            this.leaveGame(g, player)
        }

        val event = GameJoinEventEntity(game, player)
        eventService.sendEvent(event)

        if (event.isCancelled) {
            return false
        }

        if (game is HubGame) {
            return gameHubGameActionService.joinGame(game, player, team)
        }
        if (game is MiniGame) {
            return minigameActionService.joinGame(game, player, team)
        }

        throw RuntimeException("Game not supported!")
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun <P> leaveGame(game: Game, player: P) {
        require(player is Any)
        val event = GameLeaveEventEntity(game, player)
        eventService.sendEvent(event)

        if (event.isCancelled) {
            return
        }

        if (game.scoreboard != null && proxyService.getPlayerScoreboard<Any, Any?>(player) == game.scoreboard) {
            proxyService.setPlayerScoreboard(player, proxyService.generateNewScoreboard<Any>())
        }

        if (game.bossBar != null) {
            bossBarService.removePlayer(game.bossBar, player)
        }

        for (hologram in game.holograms) {
            if (hologram.players.contains(player)) {
                hologram.players.remove(player)
            }
        }

        if (game is HubGame) {
            hubGameActionService.leaveGame(game, player)
        }
        if (game is MiniGame) {
            minigameActionService.leaveGame(game, player)
        }
        if (game is BungeeCordGame) {
            bungeeCordGameActionService.leaveGame(game, player)
        }

        if (game.ingamePlayersStorage.containsKey(player)) {
            val storage = game.ingamePlayersStorage[player]!!

            if (storage.team == Team.RED) {
                proxyService.sendMessage(player, prefix + game.arena.meta.redTeamMeta.leaveMessage)
            } else if (storage.team == Team.BLUE) {
                proxyService.sendMessage(player, prefix + game.arena.meta.blueTeamMeta.leaveMessage)
            }

            game.ingamePlayersStorage.remove(player)
        }

        if (game.arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            proxyService.teleport(player, proxyService.toLocation<Any>(game.arena.meta.lobbyMeta.leaveSpawnpoint!!))
        }
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: Game) {
        if (game.closed) {
            return
        }

        if (game is MiniGame) {
            minigameActionService.closeGame(game)
        }

        if (game is BungeeCordGame) {
            bungeeCordGameActionService.closeGame(game)
        }

        if (game.ballForceFieldBlockPosition != null) {
            proxyService.setBlockType(
                proxyService.toLocation<Any>(game.ballForceFieldBlockPosition!!),
                MaterialType.AIR
            )
        }

        game.status = GameStatus.DISABLED
        game.closed = true
        game.ingamePlayersStorage.keys.toTypedArray().forEach { p -> leaveGame(game, p) }
        game.ingamePlayersStorage.clear()
        game.ball?.remove()
        game.doubleJumpCoolDownPlayers.clear()
        game.holograms.forEach { h -> h.remove() }
        game.holograms.clear()

        if (game.bossBar != null) {
            bossBarService.cleanResources(game.bossBar)
        }
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: Game, ticks: Int) {
        if (!game.arena.enabled || game.closing) {
            game.status = GameStatus.DISABLED
            onUpdateSigns(game)
            closeGame(game)
            if (game.ingamePlayersStorage.isNotEmpty() && game.arena.gameType != GameType.BUNGEE) {
                game.closing = true
            }
            return
        }

        if (game.status == GameStatus.DISABLED) {
            game.status = GameStatus.ENABLED
        }

        if (proxyService.getWorldFromName<Any?>(game.arena.meta.ballMeta.spawnpoint!!.worldName!!) == null) {
            return
        }

        if (game is HubGame) {
            hubGameActionService.handle(game, ticks)
        }

        if (game is MiniGame) {
            minigameActionService.handle(game, ticks)
        }

        if (game is BungeeCordGame) {
            bungeeCordGameActionService.handle(game, ticks)
        }

        this.gameSoccerService.handle(game, ticks)

        if (ticks >= 20) {
            if (game.closing) {
                return
            }

            this.kickUnwantedEntitiesOutOfForcefield(game)
            this.onUpdateSigns(game)
            this.updateScoreboard(game)
            this.updateBossBar(game)
            this.updateDoubleJumpCooldown(game)
            this.updateHolograms(game)
            this.updateDoubleJumpCooldown(game)
        }
    }

    /**
     * Gets called when the signs should be updated in the game lifecycle.
     */
    private fun onUpdateSigns(game: Game) {
        try {
            for (i in game.arena.meta.redTeamMeta.signs.indices) {
                val position = game.arena.meta.redTeamMeta.signs[i]

                if (!replaceTextOnSign(
                        game,
                        position,
                        game.arena.meta.redTeamMeta.signLines,
                        game.arena.meta.redTeamMeta
                    )
                ) {
                    game.arena.meta.redTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.blueTeamMeta.signs.indices) {
                val position = game.arena.meta.blueTeamMeta.signs[i]

                if (!replaceTextOnSign(
                        game,
                        position,
                        game.arena.meta.blueTeamMeta.signLines,
                        game.arena.meta.blueTeamMeta
                    )
                ) {
                    game.arena.meta.blueTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.lobbyMeta.joinSigns.indices) {
                val position = game.arena.meta.lobbyMeta.joinSigns[i]

                if (!replaceTextOnSign(game, position, game.arena.meta.lobbyMeta.joinSignLines, null)) {
                    game.arena.meta.lobbyMeta.joinSigns.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.lobbyMeta.leaveSigns.indices) {
                val position = game.arena.meta.lobbyMeta.leaveSigns[i]

                if (!replaceTextOnSign(game, position, game.arena.meta.lobbyMeta.leaveSignLines, null)) {
                    game.arena.meta.lobbyMeta.leaveSigns.removeAt(i)
                    return
                }
            }
        } catch (e: Exception) { // Removing sign task could clash with updating signs.
            loggingService.error("Sign update was cached.", e)
        }
    }

    /**
     * Replaces the text on the sign.
     */
    private fun replaceTextOnSign(
        game: Game,
        signPosition: Position,
        lines: List<String>,
        teamMeta: TeamMeta?
    ): Boolean {
        var players = game.redTeam

        if (game.arena.meta.blueTeamMeta == teamMeta) {
            players = game.blueTeam
        }

        val location = proxyService.toLocation<Any>(signPosition)
        val placeHolderReplacedLines =
            lines.map { l -> placeholderService.replacePlaceHolders(l, game, teamMeta, players.size) }

        return proxyService.setSignLines(location, placeHolderReplacedLines)
    }

    /**
     * Kicks entities out of the arena.
     */
    private fun kickUnwantedEntitiesOutOfForcefield(game: Game) {
        if (!game.arena.meta.protectionMeta.entityProtectionEnabled) {
            return
        }

        val ballSpawnpointLocation = proxyService.toLocation<Any>(game.arena.meta.ballMeta.spawnpoint!!)

        proxyService.getEntitiesInWorld<Any, Any>(ballSpawnpointLocation)
            .filter { e -> !proxyService.isPlayerInstance(e) }
            .filter { e -> proxyService.getCustomNameFromEntity(e) != "ResourceBallsPlugin" }
            .filter { e -> !proxyService.isItemFrameInstance(e) }
            .filter { e ->
                game.arena.isLocationInSelection(
                    proxyService.toPosition(
                        proxyService.getEntityLocation<Any, Any>(
                            e
                        )
                    )
                )
            }
            .forEach { e ->
                val vector = game.arena.meta.protectionMeta.entityProtection
                proxyService.setLocationDirection(proxyService.getEntityLocation<Any, Any>(e), vector)
                proxyService.setEntityVelocity(e, vector)
            }
    }

    /**
     * Updates the hologram for the current game.
     */
    private fun updateHolograms(game: Game) {
        if (game.holograms.size != game.arena.meta.hologramMetas.size) {
            game.holograms.forEach { h -> h.remove() }
            game.holograms.clear()

            game.arena.meta.hologramMetas.forEach { meta ->
                val hologram = PacketHologram()
                hologram.proxyService = proxyService
                hologram.packetService = packetService

                hologram.lines = meta.lines
                hologram.location = proxyService.toLocation(meta.position!!)
                game.holograms.add(hologram)
            }
        }

        game.holograms.forEachIndexed { i, holo ->
            val players = ArrayList(game.inTeamPlayers)
            val additionalPlayers = getAdditionalNotificationPlayers(game)
            players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first }.toList())

            holo.players.addAll(players)

            additionalPlayers.filter { p -> !p.second && holo.players.contains(p.first) }.forEach { p ->
                holo.players.remove(p.first)
            }

            val lines = ArrayList(game.arena.meta.hologramMetas[i].lines)

            for (k in lines.indices) {
                lines[k] = placeholderService.replacePlaceHolders(lines[k], game)
            }

            holo.lines = lines
            holo.update()
        }
    }

    /**
     * Updates the bossbar for the current game.
     */
    private fun updateBossBar(game: Game) {
        val meta = game.arena.meta.bossBarMeta
        if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            if (game.bossBar == null && game.arena.meta.bossBarMeta.enabled) {
                game.bossBar = bossBarService.createNewBossBar<Any>(game.arena.meta.bossBarMeta)
            }

            if (game.bossBar != null) {
                bossBarService.changeConfiguration(
                    game.bossBar,
                    placeholderService.replacePlaceHolders(meta.message, game),
                    meta,
                    null
                )

                val players = ArrayList(game.inTeamPlayers)
                val additionalPlayers = getAdditionalNotificationPlayers(game)
                players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first }
                    .toList())

                val bossbarPlayers = bossBarService.getPlayers<Any, Any>(game.bossBar!!)

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    if (bossbarPlayers.contains(p.first)) {
                        bossBarService.removePlayer(game.bossBar!!, p.first)
                    }
                }

                players.forEach { p ->
                    bossBarService.addPlayer(game.bossBar, p)
                }
            }
        } else if (dependencyService.isInstalled(PluginDependency.BOSSBARAPI)) {
            if (game.arena.meta.bossBarMeta.enabled) {
                val percentage = meta.percentage

                val players = ArrayList(game.inTeamPlayers)
                val additionalPlayers = getAdditionalNotificationPlayers(game)
                players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first }
                    .toList())

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    dependencyBossBarApiService.removeBossbarMessage(p.first)
                }

                players.forEach { p ->
                    dependencyBossBarApiService.setBossbarMessage(
                        p,
                        placeholderService.replacePlaceHolders(meta.message, game),
                        percentage
                    )
                }
            }
        }
    }

    /**
     * Updates the cooldown of the double jump for the given game.
     */
    private fun updateDoubleJumpCooldown(game: Game) {
        game.doubleJumpCoolDownPlayers.keys.toTypedArray().forEach { p ->
            var time = game.doubleJumpCoolDownPlayers[p]!!
            time -= 1

            if (time <= 0) {
                game.doubleJumpCoolDownPlayers.remove(p)
            } else {
                game.doubleJumpCoolDownPlayers[p] = time
            }
        }
    }

    /**
     * Updates the scoreboard for all players when enabled.
     */
    private fun updateScoreboard(game: Game) {
        if (!game.arena.meta.scoreboardMeta.enabled) {
            return
        }

        if (game.scoreboard == null) {
            game.scoreboard = proxyService.generateNewScoreboard()

            scoreboardService.setConfiguration(
                game.scoreboard,
                ScoreboardDisplaySlot.SIDEBAR,
                game.arena.meta.scoreboardMeta.title
            )
        }

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getAdditionalNotificationPlayers(game)
        players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first }.toList())

        additionalPlayers.filter { p -> !p.second }.forEach { p ->
            if (proxyService.getPlayerScoreboard<Any, Any>(p.first) == game.scoreboard) {
                proxyService.setPlayerScoreboard(p.first, proxyService.generateNewScoreboard<Any>())
            }
        }

        players.forEach { p ->
            if (game.scoreboard != null) {
                if (proxyService.getPlayerScoreboard<Any, Any>(p) != game.scoreboard) {
                    proxyService.setPlayerScoreboard(p, game.scoreboard)
                }

                val lines = game.arena.meta.scoreboardMeta.lines

                var j = lines.size
                for (i in 0 until lines.size) {
                    val line = placeholderService.replacePlaceHolders(lines[i], game)
                    scoreboardService.setLine(game.scoreboard, j, line)
                    j--
                }
            }
        }
    }

    /**
     * Returns a list of players which can be also notified
     */
    private fun getAdditionalNotificationPlayers(game: Game): MutableList<Pair<Any, Boolean>> {
        if (!game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            return ArrayList()
        }

        val players = ArrayList<Pair<Any, Boolean>>()
        val center = game.arena.center

        proxyService.getPlayersInWorld<Any, Any>(proxyService.toLocation(center))
            .filter { p -> !game.ingamePlayersStorage.containsKey(p) }
            .forEach { p ->
                val playerPosition = proxyService.toPosition(proxyService.getEntityLocation<Any, Any>(p))
                val distanceToCenter = playerPosition.distance(center)

                if (distanceToCenter <= game.arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(p, true))
                } else {
                    players.add(Pair(p, false))
                }
            }

        return players
    }

    /**
     * Returns if the given [player] is allowed to join the match.
     */
    private fun isAllowedToJoinWithPermissions(game: Game, player: Any): Boolean {
        if (proxyService.hasPermission(player, Permission.JOIN.permission + ".all")
            || proxyService.hasPermission(player, Permission.JOIN.permission + "." + game.arena.name)
        ) {
            return true
        }

        val prefix = configurationService.findValue<String>("messages.prefix")
        val joinGamePermissionMessage = configurationService.findValue<String>("messages.no-permission-join-game")

        proxyService.sendMessage(player, prefix + joinGamePermissionMessage)
        return false
    }
}
