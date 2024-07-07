package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.impl.BlockBallHubGameImpl
import com.github.shynixn.blockball.impl.BlockBallMiniGameImpl
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.logging.Level

class GameServiceImpl @Inject constructor(
    private val arenaRepository: Repository<Arena>,
    private val configurationService: ConfigurationService,
    private val plugin: Plugin,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val placeHolderService: PlaceHolderService,
    private val bossBarService: BossBarService,
    private val chatMessageService: ChatMessageService,
    private val soundService: SoundService,
    private val packetService: PacketService,
    private val scoreboardService: ScoreboardService,
    private val commandService: CommandService,
    private val ballEntityService: BallEntityService
) : GameService, Runnable {
    private val games = ArrayList<BlockBallGame>()
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
        configurationService.reload()

        val arenas = arenaRepository.getAll()

        for (arena in arenas) {
            initGame(arena)
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
                games.remove(game)
                initGame(game.arena)
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
     * Returns the game with the given name or displayName.
     */
    override fun getGameFromName(name: String): BlockBallGame? {
        for (game in games) {
            if (game.arena.name.equals(name, true) || game.arena.displayName.equals(name, true)) {
                return game
            }
        }

        return null
    }

    /**
     * Returns the game if the given [player] is playing a game.
     */
    override fun getGameFromPlayer(player: Player): BlockBallGame? {
        for (game in games) {
            if (game.ingamePlayersStorage.containsKey(player)) {
                return game
            }
        }

        return null
    }

    /**
     * Returns the game if the given [player] is spectating a game.
     */
    override fun getGameFromSpectatingPlayer(player: Player): BlockBallGame? {
        for (g in this.games) {
            if (g is BlockBallMiniGame) {
                if (g.spectatorPlayers.contains(player)) {
                    return g
                }
            }
        }

        return null
    }

    /**
     * Returns the game at the given location.
     */
    override fun getGameFromLocation(location: Location): BlockBallGame? {
        val position = location.toVector3d()

        for (game in games) {
            if (game.arena.isLocationInSelection(position)) {
                return game
            }
        }

        return null
    }

    /**
     * Returns all currently loaded games on the server.
     */
    override fun getAllGames(): List<BlockBallGame> {
        return games.filter { g -> (!g.closing && !g.closed) }
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

    /**
     * Initialises a new game from the given arena.
     */
    private fun initGame(arena: Arena) {
        if (arena.name.isBlank()) {
            throw Exception("Arena(s) cannot be loaded! If you have an obsolete arena file format, convert your arenas using the plugin found here https://github.com/Shynixn/BlockBall/releases/tag/conversion or delete your BlockBall folder.")
        }

        val game: BlockBallGame = when (arena.gameType) {
            GameType.HUBGAME -> BlockBallHubGameImpl(
                arena,
                this,
                playerDataRepository,
                plugin,
                placeHolderService,
                bossBarService,
                packetService,
                scoreboardService,
                ballEntityService,
                chatMessageService,
                commandService
            )

            GameType.MINIGAME -> BlockBallMiniGameImpl(
                arena,
                this,
                playerDataRepository,
                plugin,
                placeHolderService,
                bossBarService,
                chatMessageService,
                configurationService,
                soundService,
                packetService,
                scoreboardService,
                commandService,
                ballEntityService
            )

            else -> throw RuntimeException("GameType ${arena.gameType} not supported!")
        }

        games.add(game)
    }
}
