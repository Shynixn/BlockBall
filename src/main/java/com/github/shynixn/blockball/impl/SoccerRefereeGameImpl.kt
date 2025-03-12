package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.BossBarService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerRefereeGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.mcplayerstats.contract.DiscordService
import com.github.shynixn.mcplayerstats.contract.TemplateProcessService
import com.github.shynixn.mcplayerstats.entity.Template
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class SoccerRefereeGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    bossBarService: BossBarService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    language: BlockBallLanguage,
    packetService: PacketService,
    commandService: CommandService,
    soccerBallFactory: SoccerBallFactory,
    templateProcessService: TemplateProcessService,
    templateRepository: Repository<Template>,
    discordService: DiscordService
) : SoccerMiniGameImpl(
    arena,
    playerDataRepository,
    plugin,
    placeHolderService,
    bossBarService,
    chatMessageService,
    soundService,
    language,
    packetService,
    commandService,
    soccerBallFactory,
    templateProcessService,
    templateRepository,
    discordService
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
        lobbyCountdown = arena.meta.minigameMeta.lobbyDuration
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
    override fun handle(ticks: Int) {
        // Handle ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            close()
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            setGameClosing()
        }

        if (ticks >= 20) {
            // Display the message.
            if (!playing && !lobbyCountDownActive) {
                sendBroadcastMessage(language.waitingForRefereeToStart.text, language.waitingForRefereeToStartHint.text)
            }

            if (lobbyCountDownActive) {
                lobbyCountdown--

                ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    if (lobbyCountdown <= 10) {
                        p.exp = 1.0F - (lobbyCountdown.toFloat() / 10.0F)
                    }

                    p.level = lobbyCountdown
                }

                if (lobbyCountdown < 5) {
                    ingamePlayersStorage.keys.forEach { p ->
                        soundService.playSound(
                            p.location, arrayListOf(p), blingSound
                        )
                    }
                }

                if (lobbyCountdown <= 0) {
                    ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                        if (lobbyCountdown <= 10) {
                            p.exp = 1.0F
                        }

                        p.level = 0
                    }

                    lobbyCountDownActive = false
                    playing = true
                    status = GameState.RUNNING
                    matchTimeIndex = -1
                    ballEnabled = true
                    switchToNextMatchTime()
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

                        if (gameCountdown >= 0) {
                            ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
                                if (gameCountdown <= 10) {
                                    p.exp = gameCountdown.toFloat() / 10.0F
                                }

                                if (gameCountdown <= 5) {
                                    soundService.playSound(
                                        p.location, arrayListOf(p), blingSound
                                    )
                                }

                                p.level = gameCountdown
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
        this.fixBallPositionSpawn()
        this.handleBallSpawning()
        // Update signs and protections.
        super.handleMiniGameEssentials(ticks)
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
