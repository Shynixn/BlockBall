package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallDependencyInjectionModule
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.impl.SoccerHubGameImpl
import com.github.shynixn.blockball.impl.SoccerMiniGameImpl
import com.github.shynixn.blockball.impl.SoccerRefereeGameImpl
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.sign.SignService
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GameServiceImpl(
    private val arenaRepository: Repository<SoccerArena>,
    private val plugin: Plugin,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val placeHolderService: PlaceHolderService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    private val packetService: PacketService,
    private val commandService: CommandService,
    private val soccerBallFactory: SoccerBallFactory,
    private val language: BlockBallLanguage,
    private val signService: SignService,
    private val itemService: ItemService
) : GameService, Runnable {
    private val games = ArrayList<SoccerGame>()
    private var ticks: Int = 0

    /**
     * Init.
     */
    init {
        plugin.server.scheduler.runTaskTimer(
            plugin, Runnable { this.run() }, 0L, 1L
        )
    }

    /**
     * Reloads all games.
     */
    override suspend fun reloadAll() {
        close()

        val arenas = arenaRepository.getAll()

        for (arena in arenas) {
            reload(arena)
        }
    }

    /**
     * Reloads the specific game.
     */
    override suspend fun reload(arena: SoccerArena) {
        // A game with the same arena name is currently running. Stop it and reboot it.
        val existingGame = getByName(arena.name)

        if (existingGame != null) {
            existingGame.close()
            games.remove(existingGame)
            plugin.logger.log(Level.INFO, "Stopped game '" + arena.name + "'.")
        }

        // Enable signs, if they are already added, the call does nothing.
        for (sign in arena.meta.getAllSigns()) {
            sign.tag = arena.name
            signService.addSign(sign)
        }

        if (arena.enabled) {
            validateGame(arena)

            val game: SoccerGame = when (arena.gameType) {
                GameType.HUBGAME -> SoccerHubGameImpl(
                    arena,
                    playerDataRepository,
                    plugin,
                    placeHolderService,
                    language,
                    packetService,
                    soccerBallFactory,
                    commandService,
                    itemService
                )

                GameType.MINIGAME -> SoccerMiniGameImpl(
                    arena,
                    playerDataRepository,
                    plugin,
                    placeHolderService,
                    chatMessageService,
                    soundService,
                    language,
                    packetService,
                    commandService,
                    soccerBallFactory,
                    itemService
                ).also {
                    it.ballEnabled = false
                }

                GameType.REFEREEGAME -> SoccerRefereeGameImpl(
                    arena,
                    playerDataRepository,
                    plugin,
                    placeHolderService,
                    chatMessageService,
                    soundService,
                    language,
                    packetService,
                    commandService,
                    soccerBallFactory,
                    itemService
                ).also {
                    it.ballEnabled = false
                }
            }

            games.add(game)
            plugin.logger.log(Level.INFO, "Game '" + arena.name + "' is ready.")
        } else {
            plugin.logger.log(Level.INFO, "Cannot boot game '" + arena.name + "' because it is not enabled.")
        }
    }

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        games.toTypedArray().forEach { game ->
            if (game.closed) {
                runBlocking {
                    reload(game.arena)
                }
            } else {
                game.handle(ticks)
            }
        }

        if (ticks >= 20) {
            ticks = 0
        }

        ticks++
    }

    /**
     * Returns all currently loaded games on the server.
     */
    override fun getAll(): List<SoccerGame> {
        return games
    }

    /**
     * Tries to locate a game this player is playing.
     */
    override fun getByPlayer(player: Player): SoccerGame? {
        for (game in games) {
            if (game.ingamePlayersStorage.containsKey(player)) {
                return game
            }
        }

        return null
    }

    /**
     * Tries to locate a game of the given name.
     */
    override fun getByName(name: String): SoccerGame? {
        for (game in games) {
            if (game.arena.name.equals(name, true)) {
                return game
            }
        }

        return null
    }

    /**
     * Closes all games permanently and should be executed on server shutdown.
     */
    override fun close() {
        for (game in this.games) {
            try {
                game.close()
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Failed to dispose game.", e)
            }
        }

        games.clear()
    }

    private fun validateGame(arena: SoccerArena) {
        if (arena.gameType == GameType.REFEREEGAME && !BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            throw SoccerGameException(
                arena,
                language.gameTypeRefereeOnlyForPatreons.text
            )
        }
        if (arena.ballSpawnPoint == null) {
            arena.enabled = false
            throw SoccerGameException(arena, "Set the ball spawnpoint for arena ${arena.name}!")
        }
        if (arena.corner2 == null || arena.corner1 == null) {
            arena.enabled = false
            throw SoccerGameException(arena, "Set the playing field for arena ${arena.name}!")
        }
        if (arena.meta.redTeamMeta.goal.corner2 == null || arena.meta.redTeamMeta.goal.corner1 == null) {
            arena.enabled = false
            throw SoccerGameException(arena, "Set the red goal for arena ${arena.name}!")
        }
        if (arena.meta.blueTeamMeta.goal.corner2 == null || arena.meta.blueTeamMeta.goal.corner1 == null) {
            arena.enabled = false
            throw SoccerGameException(arena, "Set the blue goal for arena ${arena.name}!")
        }

        if (arena.gameType == GameType.MINIGAME || arena.gameType == GameType.REFEREEGAME) {
            if (arena.meta.lobbyMeta.leaveSpawnpoint == null) {
                arena.enabled = false
                throw SoccerGameException(arena, "Set the leave spawnpoint for arena ${arena.name}!")
            }
            if (arena.meta.redTeamMeta.lobbySpawnpoint == null) {
                arena.enabled = false
                throw SoccerGameException(arena, "Set the red lobby spawnpoint for arena ${arena.name}!")
            }
            if (arena.meta.blueTeamMeta.lobbySpawnpoint == null) {
                arena.enabled = false
                throw SoccerGameException(arena, "Set the blue lobby spawnpoint for arena ${arena.name}!")
            }
        }

        if (arena.gameType == GameType.REFEREEGAME) {
            if (arena.meta.refereeTeamMeta.lobbySpawnpoint == null) {
                arena.enabled = false
                throw SoccerGameException(arena, "Set the referee lobby spawnpoint for arena ${arena.name}!")
            }

            if (!BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                throw SoccerGameException(
                    arena,
                    "The game type where you can have a referee requires the premium version of BlockBall. Obtainable via https://www.patreon.com/Shynixn."
                )
            }
        }

        fixCorners(arena.corner2!!, arena.corner1!!)

        // If players set a too small arena height for hub game detection, automatically fix it.
        if (abs(arena.corner1!!.y - arena.corner2!!.y) < 3) {
            arena.corner1!!.y = 10.0
        }

        fixCorners(arena.meta.redTeamMeta.goal.corner2!!, arena.meta.redTeamMeta.goal.corner1!!)
        fixCorners(arena.meta.blueTeamMeta.goal.corner2!!, arena.meta.blueTeamMeta.goal.corner1!!)

        validateGoalSize(arena, Team.BLUE, arena.meta.blueTeamMeta)
        validateGoalSize(arena, Team.RED, arena.meta.redTeamMeta)
    }

    private fun validateGoalSize(arena: SoccerArena, team: Team, teamMeta: TeamMeta) {
        if (arena.meta.customizingMeta.ignoreGoalSize) {
            return
        }

        if (abs(teamMeta.goal.corner1!!.x - teamMeta.goal.corner2!!.x) < 1.8) {
            throw SoccerGameException(
                arena,
                "The goal for team ${team.name} should be at least 2x2x2 for ${arena.name}!"
            )
        }
        if (abs(teamMeta.goal.corner1!!.y - teamMeta.goal.corner2!!.y) < 1.8) {
            throw SoccerGameException(
                arena,
                "The goal for team ${team.name} should be at least 2x2x2 for ${arena.name}!"
            )
        }
        if (abs(teamMeta.goal.corner1!!.z - teamMeta.goal.corner2!!.z) < 1.8) {
            throw SoccerGameException(
                arena,
                "The goal for team ${team.name} should be at least 2x2x2 for ${arena.name}!"
            )
        }
    }

    /**
     * Corrects the corner values.
     */
    private fun fixCorners(corner1: Vector3d, corner2: Vector3d) {
        val copyCorner1 = corner1.copy()
        val copyCorner2 = corner2.copy()

        corner1.x = min(copyCorner1.x, copyCorner2.x)
        corner1.y = min(copyCorner1.y, copyCorner2.y)
        corner1.z = min(copyCorner1.z, copyCorner2.z)
        corner2.x = max(copyCorner1.x, copyCorner2.x)
        corner2.y = max(copyCorner1.y, copyCorner2.y)
        corner2.z = max(copyCorner1.z, copyCorner2.z)
    }
}
