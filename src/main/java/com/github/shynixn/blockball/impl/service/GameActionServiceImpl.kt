@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.blockball.impl.PacketHologram
import com.github.shynixn.blockball.impl.extension.setSignLines
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard
import java.util.logging.Level

class GameActionServiceImpl @Inject constructor(
    private val gameHubGameActionService: GameHubGameActionService,
    private val bossBarService: BossBarService,
    private val hubGameActionService: GameHubGameActionService,
    private val minigameActionService: GameMiniGameActionService,
    private val bungeeCordGameActionService: GameBungeeCordGameActionService,
    private val scoreboardService: ScoreboardService,
    private val dependencyBossBarApiService: DependencyBossBarApiService,
    private val gameSoccerService: GameSoccerService,
    private val placeholderService: PlaceHolderService,
    private val packetService: PacketService,
    private val plugin: Plugin,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>
) : GameActionService {
    /**
     * Compatibility reference.
     * Cyclic Reference reason why it cannot be constructor parameter.
     */
    override lateinit var gameService: GameService

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun joinGame(game: Game, player: Player, team: Team?): Boolean {
        if (!isAllowedToJoinWithPermissions(game, player)) {
            return false
        }

        gameService.getGameFromPlayer(player).ifPresent { g ->
            this.leaveGame(g, player)
        }

        val event = GameJoinEvent(player, game)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return false
        }

        val result = if (game is HubGame) {
            gameHubGameActionService.joinGame(game, player, team)
        } else if (game is MiniGame) {
            minigameActionService.joinGame(game, player, team)
        } else {
            throw RuntimeException("Game not supported!")
        }

        if (result) {
            plugin.launch {
                val playerData = playerDataRepository.getByPlayer(player)
                if (playerData != null) {
                    playerData.statsMeta.joinedGames++
                }
            }
        }

        return result
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun leaveGame(game: Game, player: Player) {
        val event = GameLeaveEvent(player, game)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        if (game.scoreboard != null && player.scoreboard == game.scoreboard) {
            player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
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
                player.sendMessage(
                    placeholderService.replacePlaceHolders(
                        game.arena.meta.redTeamMeta.leaveMessage,
                        player,
                        game,
                        null,
                        null
                    )
                )
            } else if (storage.team == Team.BLUE) {
                player.sendMessage(
                    placeholderService.replacePlaceHolders(
                        game.arena.meta.redTeamMeta.leaveMessage,
                        player,
                        game,
                        null,
                        null
                    )
                )
            }

            game.ingamePlayersStorage.remove(player)
        }

        if (game.arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(game.arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
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

        game.status = GameState.DISABLED
        game.closed = true
        game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leaveGame(game, p)
        }
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
            game.status = GameState.DISABLED
            onUpdateSigns(game)
            closeGame(game)
            if (game.ingamePlayersStorage.isNotEmpty() && game.arena.gameType != GameType.BUNGEE) {
                game.closing = true
            }
            return
        }

        if (game.status == GameState.DISABLED) {
            game.status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(game.arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
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
            plugin.logger.log(Level.SEVERE, "Sign update was cached.", e)
        }
    }

    /**
     * Replaces the text on the sign.
     */
    private fun replaceTextOnSign(
        game: Game,
        signPosition: Vector3d,
        lines: List<String>,
        teamMeta: TeamMeta?
    ): Boolean {
        var players = game.redTeam

        if (game.arena.meta.blueTeamMeta == teamMeta) {
            players = game.blueTeam
        }

        val location = signPosition.toLocation()
        val placeHolderReplacedLines =
            lines.map { l -> placeholderService.replacePlaceHolders(l, null, game, teamMeta, players.size) }

        return location.setSignLines(placeHolderReplacedLines)
    }

    /**
     * Kicks entities out of the arena.
     */
    private fun kickUnwantedEntitiesOutOfForcefield(game: Game) {
        if (!game.arena.meta.protectionMeta.entityProtectionEnabled) {
            return
        }

        val ballSpawnpointLocation = game.arena.meta.ballMeta.spawnpoint!!.toLocation()

        for (entity in ballSpawnpointLocation.world!!.entities) {
            if (entity is Player || entity is ItemFrame) {
                continue
            }

            if (game.arena.isLocationInSelection(entity.location.toVector3d())) {
                val vector = game.arena.meta.protectionMeta.entityProtection
                entity.location.setDirection(vector.toVector())
                entity.velocity = vector.toVector()
            }
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
                hologram.packetService = packetService

                hologram.lines = meta.lines
                hologram.location = meta.position!!.toLocation()
                game.holograms.add(hologram)
            }
        }

        game.holograms.forEachIndexed { i, holo ->
            val players = ArrayList(game.inTeamPlayers)
            val additionalPlayers = getAdditionalNotificationPlayers(game)
            players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first as Player }
                .toList())

            holo.players.addAll(players as Collection<Player>)

            additionalPlayers.filter { p -> !p.second && holo.players.contains(p.first) }.forEach { p ->
                holo.players.remove(p.first)
            }

            val lines = ArrayList(game.arena.meta.hologramMetas[i].lines)

            for (k in lines.indices) {
                lines[k] = placeholderService.replacePlaceHolders(lines[k], null, game)
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
        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            if (game.bossBar == null && game.arena.meta.bossBarMeta.enabled) {
                game.bossBar = bossBarService.createNewBossBar<Any>(game.arena.meta.bossBarMeta)
            }

            if (game.bossBar != null) {
                bossBarService.changeConfiguration(
                    game.bossBar,
                    placeholderService.replacePlaceHolders(meta.message, null, game),
                    meta,
                    null
                )

                val players = ArrayList(game.inTeamPlayers)
                val additionalPlayers = getAdditionalNotificationPlayers(game)
                players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }
                    .map { p -> p.first as Player }
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
        } else if (Bukkit.getPluginManager().getPlugin("BossBarAPI") != null) {
            if (game.arena.meta.bossBarMeta.enabled) {
                val percentage = meta.percentage

                val players = ArrayList(game.inTeamPlayers)
                val additionalPlayers = getAdditionalNotificationPlayers(game)
                players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }
                    .map { p -> p.first as Player }
                    .toList())

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    dependencyBossBarApiService.removeBossbarMessage(p.first as Player)
                }

                players.forEach { p ->
                    require(p is Player)
                    dependencyBossBarApiService.setBossbarMessage(
                        p,
                        placeholderService.replacePlaceHolders(meta.message, null, game),
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
            game.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard

            scoreboardService.setConfiguration(
                game.scoreboard as Scoreboard,
                ScoreboardDisplaySlot.SIDEBAR,
                game.arena.meta.scoreboardMeta.title
            )
        }

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getAdditionalNotificationPlayers(game)
        players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first as Player }
            .toList())

        additionalPlayers.filter { p -> !p.second }.forEach { p ->
            if ((p.first as Player).scoreboard == game.scoreboard) {
                (p.first as Player).scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
            }
        }

        players.forEach { p ->
            if (game.scoreboard != null) {
                if (p.scoreboard != game.scoreboard) {
                    p.scoreboard = game.scoreboard as Scoreboard
                }

                val lines = game.arena.meta.scoreboardMeta.lines

                var j = lines.size
                for (i in 0 until lines.size) {
                    val line = placeholderService.replacePlaceHolders(lines[i], p as Player, game)
                    scoreboardService.setLine(game.scoreboard as Scoreboard, j, line)
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


        center.toLocation().world!!.players
            .filter { p -> !game.ingamePlayersStorage.containsKey(p) }
            .forEach { p ->
                val playerPosition = p.location.toVector3d()
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
    private fun isAllowedToJoinWithPermissions(game: Game, player: Player): Boolean {
        if (player.hasPermission(Permission.JOIN.permission + ".all")
            || player.hasPermission(Permission.JOIN.permission + "." + game.arena.name)
        ) {
            return true
        }

        player.sendMessage(BlockBallLanguage.joinNoPermission)
        return false
    }
}
