package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.repository.Repository
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.logging.Level

class GameServiceImpl @Inject constructor(
    private val arenaRepository: Repository<Arena>,
    private val gameActionService: GameActionService,
    private val proxyService: ProxyService,
    private val configurationService: ConfigurationService,
    private val plugin: Plugin,
) : GameService, Runnable {
    private val games = ArrayList<Game>()
    private var ticks: Int = 0

    /**
     * Init.
     */
    init {
        plugin.server.scheduler.runTaskTimer(
            plugin, Runnable { this.run() },
            0L, 1L
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
                if (game !is BungeeCordGame) {
                    games.remove(game)
                    initGame(game.arena)
                }
            } else {
                gameActionService.handle(game, ticks)
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
    override fun getGameFromName(name: String): Optional<Game> {
        games.forEach { p ->
            if (p.arena.name.equals(name, true) || p.arena.displayName.equals(name, true)) {
                return Optional.of(p)
            }
        }

        return Optional.empty()
    }

    /**
     * Returns the game if the given [player] is playing a game.
     */
    override fun getGameFromPlayer(player: Player): Optional<Game> {
        games.forEach { p ->
            if (p.ingamePlayersStorage.containsKey(player)) {
                return Optional.of(p)
            }
        }

        return Optional.empty()
    }

    /**
     * Returns the game if the given [player] is spectating a game.
     */
    override fun getGameFromSpectatingPlayer(player: Player): Optional<Game> {
        games.forEach { g ->
            if (g is MiniGame) {
                if (g.spectatorPlayers.contains(player)) {
                    return Optional.of(g)
                }
            }
        }

        return Optional.empty()
    }

    /**
     * Returns the game at the given location.
     */
    override fun getGameFromLocation(location: Location): Optional<Game> {
        val position = proxyService.toPosition(location)

        games.forEach { g ->
            if (g.arena.isLocationInSelection(position)) {
                return Optional.of(g)
            }
        }

        return Optional.empty()
    }

    /**
     * Returns all currently loaded games on the server.
     */
    override fun getAllGames(): List<Game> {
        return games.filter { g -> (!g.closing && !g.closed) || g is BungeeCordGame }
    }

    /**
     * Closes all games permanently and should be executed on server shutdown.
     */
    override fun close() {
        for (game in this.games) {
            try {
                gameActionService.closeGame(game)
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
        if (arena.name.isNullOrBlank()) {
            throw Exception("Arena(s) cannot be loaded! If you have an obsolete arena file format, convert your arenas using the plugin found here https://github.com/Shynixn/BlockBall/releases/tag/conversion or delete your BlockBall folder.")
        }

        val game: Game = when (arena.gameType) {
            GameType.HUBGAME -> HubGame(arena)
            GameType.MINIGAME -> MiniGame(arena)
            else -> BungeeCordGame(arena)
        }

        if (game is BungeeCordGame && game.arena.enabled) {
            games.add(game)

            for (player in proxyService.getOnlinePlayers<Any>()) {
                require(player is Player)
                if (!getGameFromPlayer(player).isPresent) {
                    gameActionService.joinGame(game, player)
                }
            }
        } else {
            games.add(game)
        }
    }
}
