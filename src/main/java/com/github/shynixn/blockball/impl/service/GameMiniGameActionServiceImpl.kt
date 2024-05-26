package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.GameExecutionService
import com.github.shynixn.blockball.contract.GameMiniGameActionService
import com.github.shynixn.blockball.contract.GameSoccerService
import com.github.shynixn.blockball.contract.PlaceHolderService
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard

class GameMiniGameActionServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val screenMessageService: ChatMessageService,
    private val soundService: SoundService,
    private val gameSoccerService: GameSoccerService,
    private val gameExecutionService: GameExecutionService,
    private val placeholderService: PlaceHolderService,
    private val plugin: Plugin,
    private val chatMessageService: ChatMessageService
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
            chatMessageService.sendChatMessage(player, b.convertToTextComponent())
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
        player.teleport(game.arena.meta.minigameMeta.lobbySpawnpoint!!.toLocation())

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
            player.teleport(game.arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
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
        player.gameMode = GameMode.SPECTATOR
        player.isFlying = true

        if (game.arena.meta.spectatorMeta.spectateSpawnpoint != null) {
            player.teleport(game.arena.meta.spectatorMeta.spectateSpawnpoint!!.toLocation())
        } else {
            player.teleport(game.arena.meta.ballMeta.spawnpoint!!.toLocation())
        }
    }

    /**
     * Gets called when the given [game] ends with a draw.
     */
    override fun onDraw(game: MiniGame) {
        val additionalPlayers = getNotifiedPlayers(game).filter { pair -> pair.second }.map { p -> p.first as Player }
        additionalPlayers.forEach { p ->
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
                    p.exp =  1.0F - (game.lobbyCountdown.toFloat() / 10.0F)
                }

                p.level = game.lobbyCountdown
            }

            if (game.lobbyCountdown < 5) {
                game.ingamePlayersStorage.keys.forEach { p ->
                    soundService.playSound(
                        p.location, arrayListOf(p), game.blingSound
                    )
                }
            }

            if (game.lobbyCountdown <= 0) {
                game.ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    if (game.lobbyCountdown <= 10) {
                        p.exp = 1.0F
                    }

                    p.level = 0
                }

                game.lobbyCountDownActive = false
                game.playing = true
                game.status = GameState.RUNNING
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
                    p.exp = game.gameCountdown.toFloat() / 10.0F
                }

                if (game.gameCountdown <= 5) {
                    soundService.playSound(
                        p.location, arrayListOf(p), game.blingSound
                    )
                }

                p.level = game.gameCountdown
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
                gameExecutionService.respawn(game, p)
            }

            if (!matchTime.startMessageTitle.isBlank() || !matchTime.startMessageSubTitle.isBlank()) {
                plugin.launch {
                    delay(60.ticks)
                    screenMessageService.sendTitleMessage(
                        p,
                        placeholderService.replacePlaceHolders(matchTime.startMessageTitle, null, game),
                        placeholderService.replacePlaceHolders(matchTime.startMessageSubTitle, null, game),
                        matchTime.startMessageFadeIn,
                        matchTime.startMessageStay,
                        matchTime.startMessageFadeOut
                    )
                }
            }

            p.exp = 1.0F
        }
    }

    private fun createPlayerStorage(game: MiniGame, player: Player): GameStorage {
        val stats = GameStorage()
        stats.gameMode = player.gameMode
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
        stats.flying = player.isFlying
        stats.allowedFlying = player.allowFlight
        stats.walkingSpeed = player.walkSpeed.toDouble()
        stats.scoreboard = player.scoreboard
        stats.level = player.level
        stats.exp = player.exp.toDouble()
        stats.maxHealth = player.maxHealth
        stats.health = player.health
        stats.hunger = player.foodLevel

        player.allowFlight = false
        player.isFlying = false

        if (!game.arena.meta.customizingMeta.keepHealthEnabled) {
            player.maxHealth = 20.0
            player.health = 20.0
        }

        player.foodLevel = 20
        player.level = 0
        player.exp = 0.0F
        player.gameMode = game.arena.meta.lobbyMeta.gamemode

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

        if (team == Team.RED && game.arena.meta.redTeamMeta.lobbySpawnpoint != null) {
            player.teleport(game.arena.meta.redTeamMeta.lobbySpawnpoint!!.toLocation())
        } else if (team == Team.BLUE && game.arena.meta.blueTeamMeta.lobbySpawnpoint != null) {
            player.teleport(game.arena.meta.blueTeamMeta.lobbySpawnpoint!!.toLocation())
        }

        val players = if (team == Team.RED) {
            game.redTeam
        } else {
            game.blueTeam
        }

        player.sendMessage(
            placeholderService.replacePlaceHolders(
                teamMeta.joinMessage, player, game, teamMeta, players.size
            )
        )
    }

    /**
     * Returns if the given [player] is allowed to spectate the match.
     */
    private fun isAllowedToSpectateWithPermissions(game: MiniGame, player: Player): Boolean {
        val hasSpectatingPermission =
            player.hasPermission(Permission.SPECTATE.permission + ".all") || player.hasPermission(
                Permission.SPECTATE.permission + "." + game.arena.name
            )

        if (hasSpectatingPermission) {
            return true
        }

        player.sendMessage(BlockBallLanguage.spectateNoPermission)
        return false
    }

    /**
     * Gets called when the game ends.
     */
    private fun timeAlmostUp(game: MiniGame) {
        when {
            game.redScore == game.blueScore -> {
                gameSoccerService.onMatchEnd(game, null, null)
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
            val playersInWorld = game.arena.center.toLocation().world!!.players
            for (p in playersInWorld) {
                val position = p.location.toVector3d()
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
        player.gameMode = stats.gameMode
        player.allowFlight = stats.gameMode == GameMode.CREATIVE
        player.isFlying = false
        player.walkSpeed = stats.walkingSpeed.toFloat()
        player.scoreboard = stats.scoreboard as Scoreboard
        player.level = stats.level
        player.exp = stats.exp.toFloat()

        if (!game.arena.meta.customizingMeta.keepHealthEnabled) {
            player.maxHealth = stats.maxHealth
            player.health = stats.health
        }

        player.foodLevel = stats.hunger

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
