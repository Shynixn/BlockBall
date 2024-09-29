package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class SoccerRefereeGameImpl constructor(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val bossBarService: BossBarService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    private val language: BlockBallLanguage,
    packetService: PacketService,
    scoreboardService: ScoreboardService,
    commandService: CommandService,
    soccerBallFactory: SoccerBallFactory
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
    scoreboardService,
    commandService,
    soccerBallFactory
), SoccerRefereeGame {
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
     * Tick handle.
     */
    override fun handle(ticks: Int) {
        // Handle ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            close()
            if (ingamePlayersStorage.isNotEmpty()) {
                closing = true
            }
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            closing = true
        }

        if (ticks >= 20) {
            // Display the message.
            if (!playing && !lobbyCountDownActive) {
                ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    chatMessageService.sendActionBarMessage(
                        p, placeHolderService.replacePlaceHolders(
                            language.waitingForRefereeToStart, p, this
                        )
                    )
                }
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
                gameCountdown--

                if (gameCountdown <= 0) {
                    gameCountdown = 0
                    for (player in refereeTeam) {
                        chatMessageService.sendActionBarMessage(
                            player, placeHolderService.replacePlaceHolders(
                                language.refereeNextPeriodHint, player, this
                            )
                        )
                    }
                }

                if (gameCountdown > 0) {
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

                if (ingamePlayersStorage.isEmpty() || redTeam.size < arena.meta.redTeamMeta.minPlayingPlayers || blueTeam.size < arena.meta.blueTeamMeta.minPlayingPlayers) {
                    closing = true
                }
            }
        }

        // Handle SoccerBall.
        this.fixBallPositionSpawn()
        if (ticks >= 20) {
            this.handleBallSpawning()
        }

        // Update signs and protections.
        super.handleMiniGameEssentials(ticks)
    }

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    override fun switchToNextMatchTime() {
        super.switchToNextMatchTime()

        if (ball != null) {
            ballEnabled = false
            ball!!.remove()
            ball = null
        }
    }
}
