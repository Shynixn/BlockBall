package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.CloudService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerRefereeGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.event.GameStartEvent
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Instant

class SoccerRefereeGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    language: BlockBallLanguage,
    commandService: CommandService,
    soccerBallFactory: SoccerBallFactory,
    itemService: ItemService,
    cloudService: CloudService
) : SoccerMiniGameImpl(
    arena,
    playerDataRepository,
    plugin,
    placeHolderService,
    chatMessageService,
    soundService,
    language,
    commandService,
    soccerBallFactory,
    itemService,
    cloudService
), SoccerRefereeGame {
    /**
     * Is the timer blocker enabled.
     */
    override var isTimerBlockerEnabled: Boolean = false

    /**
     * Toggles the lobby countdown if the game is not running yet.
     */
    override fun setLobbyCountdownActive(enabled: Boolean) {
        if (playing) {
            return
        }

        lobbyCountDownActive = enabled
        gameCountdown = arena.meta.minigameMeta.lobbyDuration
    }

    /**
     * Stops the game and sets it to the last match time.
     */
    override fun stopGame() {
        switchToNextMatchTime()

        isTimerBlockerEnabled = false
        val matchTimes = arena.meta.minigameMeta.matchTimes
        val index = matchTimes.size - 2

        if (index > 0) {
            // Guess previous match time and switch to last match time.
            switchToNextMatchTime()
            return
        }

        // Cannot find matching index. Just quit the game.
        matchTimeIndex = Int.MAX_VALUE
        switchToNextMatchTime()
    }

    /**
     * Tick handle.
     */
    override fun handle(hasSecondPassed: Boolean) {
        // Handle ticking.
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
            // Display the message.
            if (!playing && !lobbyCountDownActive) {
                sendBroadcastMessage(language.waitingForRefereeToStart.text, language.waitingForRefereeToStartHint.text)
            }

            if (lobbyCountDownActive) {
                gameCountdown--

                if (gameCountdown < 5) {
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
                    Bukkit.getPluginManager().callEvent(GameStartEvent(this))
                    executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.gameStartCommands)
                    executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.gameStartCommands)
                    executeCommandsWithPlaceHolder(refereeTeam, arena.meta.refereeTeamMeta.gameStartCommands)
                }
            }

            if (playing) {
                if (matchTimeIndex != -1) {
                    if (ball != null && !ball!!.isInteractable) {
                        sendBroadcastMessage(
                            language.whistleTimeOutReferee.text,
                            language.whistleTimeOutRefereeHint.text
                        )
                    }

                    if (!isTimerBlockerEnabled) {
                        gameCountdown--

                        if (gameCountdown <= 0) {
                            gameCountdown = 0

                            if (ball != null) {
                                ingamePlayersStorage.filter { e -> e.value.team != Team.REFEREE }.forEach { p ->
                                    chatMessageService.sendActionBarMessage(
                                        p.key, placeHolderService.resolvePlaceHolder(
                                            language.nextPeriodReferee.text, p.key
                                        )
                                    )
                                }
                            }

                            refereeTeam.forEach { p ->
                                chatMessageService.sendActionBarMessage(
                                    p, placeHolderService.resolvePlaceHolder(
                                        language.nextPeriodRefereeHint.text, p
                                    )
                                )
                            }
                        }

                        if (gameCountdown > 0) {
                            ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
                                if (gameCountdown <= 5) {
                                    soundService.playSound(
                                        p.location, arrayListOf(p), arena.meta.minigameMeta.countdownSound
                                    )
                                }
                            }
                        }
                    }
                }

                // The game has to be resetable automatically.
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


        val wasSpawned = this.handleBallSpawning()
        if (wasSpawned) {
            ball?.isInteractable = false
        }

        // Update signs and protections.
        super.handleMiniGameEssentials(hasSecondPassed)
    }

    private fun sendBroadcastMessage(playerMessage: String, refereeMessage: String) {
        refereeTeam.forEach { p ->
            chatMessageService.sendActionBarMessage(
                p, placeHolderService.resolvePlaceHolder(
                    refereeMessage, p
                )
            )
        }

        val otherPlayers = ArrayList<Player>()
        otherPlayers.addAll(redTeam)
        otherPlayers.addAll(blueTeam)

        for (player in otherPlayers) {
            chatMessageService.sendActionBarMessage(
                player, placeHolderService.resolvePlaceHolder(
                    playerMessage, player
                )
            )
        }
    }
}
