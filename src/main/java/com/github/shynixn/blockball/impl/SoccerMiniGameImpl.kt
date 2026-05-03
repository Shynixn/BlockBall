package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.ForceField
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.GameSubState
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.JoinResult
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.event.GameStartEvent
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import kotlinx.coroutines.delay
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Instant

open class SoccerMiniGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    placeHolderService: PlaceHolderService,
    chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    language: BlockBallLanguage,
    commandService: CommandService,
    soccerBallFactory: SoccerBallFactory,
    itemService: ItemService,
    cloudService: CloudService,
    private val server: Server,
    private val coroutineHandler: CoroutineHandler,
    private val forceFieldService: ForceFieldService
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
    cloudService,
    coroutineHandler,
    server
), SoccerMiniGame {
    private var currentQueueTime = arena.meta.customizingMeta.queueTimeOutSec
    private var isQueueTimeRunning = false
    private val forceField: ForceField = ForceField(arena.outerField.corner1!!, arena.outerField.corner2!!).also {
        it.on2dOutSide = { player ->
            if (arena.meta.minigameMeta.forceFieldEnabled && status == GameState.RUNNING && ingamePlayersStorage.containsKey(
                    player
                )
            ) {
                lockInsideForceField(player)
            }
        }
    }

    init {
        forceFieldService.addForceField(forceField)
    }

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
        if (server.getWorld(arena.ballSpawnPoint!!.world!!) == null) {
            return
        }

        if (!arena.enabled || subState == GameSubState.CLOSED || isDisposed) {
            close()
            return
        }

        if (subStateNext != GameSubState.CLOSED) {
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

                    if (gameCountdown < 5 && !isHytaleLoaded) {
                        ingamePlayersStorage.keys.forEach { p ->
                            soundService.playSound(
                                p.location, arrayListOf(p), arena.meta.minigameMeta.countdownSound
                            )
                        }
                    }

                    if (gameCountdown % 10 == 0 || gameCountdown <= 5) {
                        for (player in ingamePlayersStorage.keys) {
                            chatMessageService.sendLanguageMessage(
                                player,
                                language.gameStartingMessage,
                                gameCountdown.toString()
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
                        server.pluginManager.callEvent(GameStartEvent(this))
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
                        if (gameCountdown <= 5 && !isHytaleLoaded) {
                            soundService.playSound(
                                p.location, arrayListOf(p), arena.meta.minigameMeta.countdownSound
                            )
                        }
                    }

                    if (gameCountdown <= 0) {
                        switchToNextMatchTime()
                    }

                    if (ingamePlayersStorage.isEmpty() || redTeam.size < arena.meta.redTeamMeta.minPlayingPlayers || blueTeam.size < arena.meta.blueTeamMeta.minPlayingPlayers) {
                        if (subStateNext != GameSubState.CLOSED) {
                            setNextGameSubState(GameSubState.CLOSED, 1000L)
                        }
                    }
                }
            }

            if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
                setNextGameSubState(GameSubState.CLOSED, 1000L)
            }

            if (ball == null && ballEnabled && ingamePlayersStorage.isNotEmpty() && subStateNext != GameSubState.BALL_RESPAWNED) {
                setNextGameSubState(
                    GameSubState.BALL_RESPAWNED,
                    arena.meta.customizingMeta.gameStartBallSpawnDelayTicks * 50L
                )
            }

            if (!ballEnabled && ball != null && subStateNext != GameSubState.BALL_DESTROYED) {
                setNextGameSubState(GameSubState.BALL_DESTROYED)
            }
        }

        this.runStateMachine()
        super.handleMiniGameEssentials(hasSecondPassed)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (isDisposed) {
            return
        }
        status = GameState.DISABLED
        isDisposed = true
        forceFieldService.removeForceField(forceField)
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
        interactedWithBall.clear()
        subStatePlayerParam = null
        subStateLocationParam = null
    }

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    override fun switchToNextMatchTime() {
        matchTimeIndex++

        val matchTimes = arena.meta.minigameMeta.matchTimes

        if (matchTimeIndex >= matchTimes.size) {
            if (subStateNext != GameSubState.CLOSED) {
                setNextGameSubState(GameSubState.CLOSED, 5000L)
            }
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
                coroutineHandler.execute {
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

        coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
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
        coroutineHandler.execute {
            while (isQueueTimeRunning && status == GameState.JOINABLE) {
                currentQueueTime -= 1

                if (currentQueueTime <= 0) {
                    isQueueTimeRunning = false
                    for (player in ingamePlayersStorage.keys.toTypedArray()) {
                        chatMessageService.sendLanguageMessage(player, language.queueTimeOutMessage)
                        leave(player)
                    }
                    status = GameState.JOINABLE
                    return@execute
                }

                delay(20.ticks)
            }
        }
    }

    private fun lockInsideForceField(player: Player) {
        forceFieldService.knockBackInside(forceField, player, 0.9F)
    }
}
