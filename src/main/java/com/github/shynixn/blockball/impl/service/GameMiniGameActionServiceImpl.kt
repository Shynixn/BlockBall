package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.impl.extension.toSoundMeta
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GameMiniGameActionServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val screenMessageService: ChatMessageService,
    private val soundService: SoundService,
    private val proxyService: ProxyService,
    private val gameSoccerService: GameSoccerService,
    private val gameExecutionService: GameExecutionService,
    private val placeholderService: PlaceHolderService,
    private val plugin: Plugin
) : GameMiniGameActionService {

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: MiniGame) {
        game.spectatorPlayers.forEach { p ->
            leaveGame(game, p as Player)
        }
    }

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun joinGame(game: MiniGame, player: Player, team: Team?): Boolean {
        if (game.playing || game.isLobbyFull) {
            val b = ChatBuilder().text(
                placeholderService.replacePlaceHolders(
                    game.arena.meta.spectatorMeta.spectateStartMessage[0], player, game
                )
            ).nextLine().component(
                placeholderService.replacePlaceHolders(
                    game.arena.meta.spectatorMeta.spectateStartMessage[1], player, game
                )
            ).setClickAction(
                ChatClickAction.RUN_COMMAND,
                "/" + configurationService.findValue<String>("global-spectate.command") + " " + game.arena.name
            ).setHoverText(" ").builder()

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
    override fun leaveGame(game: MiniGame, player: Player) {
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
    override fun spectateGame(game: MiniGame, player: Player) {
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
            require(p is Player)
            screenMessageService.sendTitleMessage(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageTitle, p, game),
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageSubTitle, p, game),
                game.arena.meta.redTeamMeta.drawMessageFadeIn,
                game.arena.meta.redTeamMeta.drawMessageStay,
                game.arena.meta.redTeamMeta.drawMessageFadeOut,
            )
        }

        game.redTeam.forEach { p ->
            require(p is Player)
            screenMessageService.sendTitleMessage(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageTitle, p, game),
                placeholderService.replacePlaceHolders(game.arena.meta.redTeamMeta.drawMessageSubTitle, p, game),
                game.arena.meta.redTeamMeta.drawMessageFadeIn,
                game.arena.meta.redTeamMeta.drawMessageStay,
                game.arena.meta.redTeamMeta.drawMessageFadeOut,
            )
        }
        game.blueTeam.forEach { p ->
            require(p is Player)
            screenMessageService.sendTitleMessage(
                p,
                placeholderService.replacePlaceHolders(game.arena.meta.blueTeamMeta.drawMessageTitle, p, game),
                placeholderService.replacePlaceHolders(game.arena.meta.blueTeamMeta.drawMessageSubTitle, p, game),
                game.arena.meta.blueTeamMeta.drawMessageFadeIn,
                game.arena.meta.blueTeamMeta.drawMessageStay,
                game.arena.meta.blueTeamMeta.drawMessageFadeOut,
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
                    require(p is Player)
                    soundService.playSound(
                        proxyService.getEntityLocation<Location, Any>(p),
                        arrayListOf(p),
                        game.blingSound.toSoundMeta()
                    )
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
                game.status = GameState.RUNNING
                game.matchTimeIndex = -1

                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    require(p is Player)
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
                    require(p is Player)
                    screenMessageService.sendActionBarMessage(
                        p, placeholderService.replacePlaceHolders(
                            game.arena.meta.minigameMeta.playersRequiredToStartMessage, p, game
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
                    require(p is Player)
                    soundService.playSound(
                        proxyService.getEntityLocation<Location, Any>(p),
                        arrayListOf(p),
                        game.blingSound.toSoundMeta()
                    )
                }

                proxyService.setPlayerLevel(p, game.gameCountdown)
            }

            if (game.gameCountdown <= 0) {
                switchToNextMatchTime(game)
            }

            if (game.ingamePlayersStorage.isEmpty() || game.redTeam.size < game.arena.meta.redTeamMeta.minPlayingPlayers || game.blueTeam.size < game.arena.meta.blueTeamMeta.minPlayingPlayers) {
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
                gameExecutionService.respawn(game, p as Player)
            }

            if (!matchTime.startMessageTitle.isBlank() || !matchTime.startMessageSubTitle.isBlank()) {
                plugin.launch {
                    delay(60.ticks)
                    screenMessageService.sendTitleMessage(
                        p as Player,
                        placeholderService.replacePlaceHolders(matchTime.startMessageTitle, null, game),
                        placeholderService.replacePlaceHolders(matchTime.startMessageSubTitle, null, game),
                        matchTime.startMessageFadeIn,
                        matchTime.startMessageStay,
                        matchTime.startMessageFadeOut
                    )
                }
            }

            proxyService.setPlayerExp(p, 1.0)
        }
    }

    private fun createPlayerStorage(game: MiniGame, player: Player): GameStorage {
        val stats = GameStorage()
        stats.gameMode = proxyService.getPlayerGameMode(player)
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
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

        if (!game.arena.meta.customizingMeta.keepHealthEnabled) {
            proxyService.setPlayerMaxHealth(player, 20.0)
            proxyService.setPlayerHealth(player, 20.0)
        }

        proxyService.setPlayerHunger(player, 20)
        proxyService.setPlayerLevel(player, 0)
        proxyService.setPlayerExp(player, 0.0)
        proxyService.setGameMode(player, game.arena.meta.lobbyMeta.gamemode)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.clear()
            player.updateInventory()
        }

        return stats
    }

    /**
     * Joins the [player] to the given [teamMeta].
     */
    private fun joinTeam(game: MiniGame, player: Player, team: Team, teamMeta: TeamMeta) {
        proxyService.setPlayerWalkingSpeed(player, teamMeta.walkingSpeed)

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

        if (team == Team.RED && game.arena.meta.redTeamMeta.lobbySpawnpoint != null) {
            proxyService.teleport(player, game.arena.meta.redTeamMeta.lobbySpawnpoint)
        } else if (team == Team.BLUE && game.arena.meta.blueTeamMeta.lobbySpawnpoint != null) {
            proxyService.teleport(player, game.arena.meta.blueTeamMeta.lobbySpawnpoint)
        }

        val players = if (team == Team.RED) {
            game.redTeam
        } else {
            game.blueTeam
        }

        proxyService.sendMessage(
            player, placeholderService.replacePlaceHolders(
                teamMeta.joinMessage, player, game, teamMeta, players.size
            )
        )
    }

    /**
     * Returns if the given [player] is allowed to spectate the match.
     */
    private fun isAllowedToSpectateWithPermissions(game: MiniGame, player: Any): Boolean {
        val hasSpectatingPermission =
            proxyService.hasPermission(player, Permission.SPECTATE.permission + ".all") || proxyService.hasPermission(
                player, Permission.SPECTATE.permission + "." + game.arena.name
            )

        if (hasSpectatingPermission) {
            return true
        }

        proxyService.sendMessage(player, BlockBallLanguage.spectateNoPermission)

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
    private fun resetStorage(player: Player, game: Game, stats: GameStorage) {
        proxyService.setGameMode(player, stats.gameMode)
        proxyService.setPlayerAllowFlying(player, stats.allowedFlying)
        proxyService.setPlayerFlying(player, stats.flying)
        proxyService.setPlayerWalkingSpeed(player, stats.walkingSpeed)
        proxyService.setPlayerScoreboard(player, stats.scoreboard)
        proxyService.setPlayerLevel(player, stats.level)
        proxyService.setPlayerExp(player, stats.exp)

        if (!game.arena.meta.customizingMeta.keepHealthEnabled) {
            proxyService.setPlayerMaxHealth(player, stats.maxHealth)
            proxyService.setPlayerHealth(player, stats.health)
        }

        proxyService.setPlayerHunger(player, stats.hunger)

        if (!game.arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents.clone()
            player.inventory.setArmorContents(stats.armorContents.clone())
            player.updateInventory()
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
