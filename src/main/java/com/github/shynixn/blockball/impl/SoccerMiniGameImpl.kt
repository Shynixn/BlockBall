package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.CloudService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerMiniGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.JoinResult
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.event.GameStartEvent
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Instant

open class SoccerMiniGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    language: BlockBallLanguage,
    commandService: CommandService,
    soccerBallFactory: SoccerBallFactory,
    itemService: ItemService,
    cloudService: CloudService
) : SoccerGameImpl(
    arena,
    placeHolderService,
    plugin,
    soccerBallFactory,
    commandService,
    language,
    playerDataRepository,
    itemService,
    chatMessageService,
    cloudService
), SoccerMiniGame {
    private var currentQueueTime = arena.meta.customizingMeta.queueTimeOutSec
    private var isQueueTimeRunning = false

    /**
     * Is the lobby countdown active.
     */
    var lobbyCountDownActive: Boolean = false

    /**
     * Actual game coutndown.
     */
    override var gameCountdown: Int = 0

    /**
     * Index of the current match time.
     */
    override var matchTimeIndex: Int = 0

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of soccerArena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun join(player: Player, team: Team?): JoinResult {
        if (playing) {
            return JoinResult.GAME_ALREADY_RUNNING
        }

        queueTimeOut()
        return super.join(player, team)
    }

    /**
     * Tick handle.
     */
    override fun handle(hasSecondPassed: Boolean) {
        // Handle HubGame ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            close()
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.ballSpawnPoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            setGameClosing()
        }

        if (hasSecondPassed) {
            if (lobbyCountDownActive) {
                isQueueTimeRunning = false

                if (gameCountdown > 10) {
                    val amountPlayers = arena.meta.blueTeamMeta.maxAmount + arena.meta.redTeamMeta.maxAmount

                    if (ingamePlayersStorage.size >= amountPlayers) {
                        gameCountdown = 10
                    }
                }

                gameCountdown--

                if (gameCountdown < 5) {
                    ingamePlayersStorage.keys.forEach { p ->
                        soundService.playSound(
                            p.location, arrayListOf(p), arena.meta.minigameMeta.countdownSound
                        )
                    }
                }

                if (gameCountdown <= 0) {
                    lobbyCountDownActive = false
                    playing = true
                    status = GameState.RUNNING
                    matchTimeIndex = -1
                    ballEnabled = true
                    startDateUtc = Instant.now()
                    switchToNextMatchTime()
                    Bukkit.getPluginManager().callEvent(GameStartEvent(this))
                    executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.gameStartCommands)
                    executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.gameStartCommands)
                    executeCommandsWithPlaceHolder(refereeTeam, arena.meta.refereeTeamMeta.gameStartCommands)
                }
            }

            if (!lobbyCountDownActive) {
                if (canStartLobbyCountdown()) {
                    lobbyCountDownActive = true
                    gameCountdown = arena.meta.minigameMeta.lobbyDuration
                }
            }

            if (playing) {
                gameCountdown--

                ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
                    if (gameCountdown <= 5) {
                        soundService.playSound(
                            p.location, arrayListOf(p), arena.meta.minigameMeta.countdownSound
                        )
                    }
                }

                if (gameCountdown <= 0) {
                    switchToNextMatchTime()
                }

                if (ingamePlayersStorage.isEmpty() || redTeam.size < arena.meta.redTeamMeta.minPlayingPlayers || blueTeam.size < arena.meta.blueTeamMeta.minPlayingPlayers) {
                    setGameClosing()
                }
            }
        }

        // Handle SoccerBall.
        if (ball == null) {
            if (ballEnabled && ingamePlayersStorage.isNotEmpty() && ballSpawning == false) {
                respawnBall(arena.meta.customizingMeta.gameStartBallSpawnDelayTicks)
            }
        }

        if (!ballEnabled && ball != null) {
            destroyBall()
        }

        this.handleBallSpawning()

        // Update signs and protections.
        super.handleMiniGameEssentials(hasSecondPassed)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (closed) {
            return
        }
        status = GameState.DISABLED
        closed = true
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
        interactedWithBall.clear()
    }

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    override fun switchToNextMatchTime() {
        matchTimeIndex++

        val matchTimes = arena.meta.minigameMeta.matchTimes

        if (matchTimeIndex >= matchTimes.size) {
            setGameClosing()
            return
        }

        val matchTime = matchTimes[matchTimeIndex]
        val isLastMatchTimeSwap = (matchTimeIndex + 1) >= matchTimes.size

        if (isLastMatchTimeSwap) {
            timeAlmostUp()
        }

        gameCountdown = matchTime.duration

        if (matchTime.switchGoals) {
            mirroredGoals = !mirroredGoals

            ingamePlayersStorage.values.forEach { e ->
                if (e.goalTeam != null) {
                    if (e.goalTeam == Team.RED) {
                        e.goalTeam = Team.BLUE
                    } else if (e.goalTeam == Team.BLUE) {
                        e.goalTeam = Team.RED
                    } else {
                        e.goalTeam = Team.REFEREE
                    }
                }
            }
        }

        ballEnabled = matchTime.playAbleBall

        if (!ballEnabled && ball != null && !ball!!.isDead) {
            ball!!.remove()
        }

        for (p in ingamePlayersStorage.keys) {
            if (matchTime.respawnEnabled || matchTimeIndex == 0) {
                respawn(p)
            }

            if (!matchTime.startMessageTitle.isBlank() || !matchTime.startMessageSubTitle.isBlank()) {
                plugin.launch {
                    delay(10.ticks)
                    chatMessageService.sendTitleMessage(
                        p,
                        placeHolderService.resolvePlaceHolder(matchTime.startMessageTitle, p),
                        placeHolderService.resolvePlaceHolder(matchTime.startMessageSubTitle, p),
                        matchTime.startMessageFadeIn,
                        matchTime.startMessageStay,
                        matchTime.startMessageFadeOut
                    )
                }
            }
        }
    }

    override fun setPlayerToArena(player: Player, team: Team) {
        val teamMeta = getTeamMetaFromTeam(team)
        val location = teamMeta.lobbySpawnpoint!!.toLocation()

        plugin.launch(plugin.entityDispatcher(player)) {
            player.teleportCompat(plugin, location)
        }
    }

    /**
     * Gets called when the game ends.
     */
    private fun timeAlmostUp() {
        when {
            redScore == blueScore -> {
                onMatchEnd(null)
                onDraw()
            }

            redScore > blueScore -> {
                onMatchEnd(Team.RED)
                onWin(Team.RED)
            }

            else -> {
                onMatchEnd(Team.BLUE)
                onWin(Team.BLUE)
            }
        }
    }

    /**
     * Returns if the lobby countdown can already be started.
     * @return canStart
     */
    private fun canStartLobbyCountdown(): Boolean {
        val amount = arena.meta.redTeamMeta.minAmount + arena.meta.blueTeamMeta.minAmount

        if (!playing && ingamePlayersStorage.size >= amount && ingamePlayersStorage.isNotEmpty()) {
            return true
        }

        return false
    }


    private fun queueTimeOut() {
        if (arena.gameType == GameType.REFEREEGAME) {
            return
        }

        currentQueueTime = arena.meta.customizingMeta.queueTimeOutSec // Reset queue timer each time someone joins.

        if (isQueueTimeRunning) {
            return
        }

        isQueueTimeRunning = true
        plugin.launch {
            while (isQueueTimeRunning && status == GameState.JOINABLE) {
                currentQueueTime -= 1

                if (currentQueueTime <= 0) {
                    isQueueTimeRunning = false
                    for (player in ingamePlayersStorage.keys.toTypedArray()) {
                        chatMessageService.sendLanguageMessage(player, language.queueTimeOutMessage)
                        leave(player)
                    }
                    status = GameState.JOINABLE
                    return@launch
                }

                delay(20.ticks)
            }
        }
    }
}
