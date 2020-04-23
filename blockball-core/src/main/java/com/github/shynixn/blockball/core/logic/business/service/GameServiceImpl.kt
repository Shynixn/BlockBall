package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordGame
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.business.extension.thenAcceptSafely
import com.github.shynixn.blockball.core.logic.persistence.entity.BungeeCordGameEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.HubGameEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.MiniGameEntity
import com.google.inject.Inject
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class GameServiceImpl @Inject constructor(
    private val persistenceArenaService: PersistenceArenaService,
    private val gameActionService: GameActionService,
    private val proxyService: ProxyService,
    private val configurationService: ConfigurationService,
    private val loggingService: LoggingService,
    concurrencyService: ConcurrencyService
) : GameService, Runnable {
    private val games = ArrayList<Game>()
    private var ticks: Int = 0

    /**
     * Init.
     */
    init {
        sync(concurrencyService, 0L, 1L) {
            this.run()
        }
    }

    /**
     * Restarts all games on the server.
     */
    override fun restartGames(): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        close()
        configurationService.reload()

        persistenceArenaService.refresh().thenAcceptSafely {
            persistenceArenaService.getArenas().forEach { arena ->
                initGame(arena)
            }

            completableFuture.complete(null)
        }

        return completableFuture
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
    override fun <P> getGameFromPlayer(player: P): Optional<Game> {
        require(player is Any)

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
    override fun <P> getGameFromSpectatingPlayer(player: P): Optional<Game> {
        require(player is Any)

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
    override fun <L> getGameFromLocation(location: L): Optional<Game> {
        require(location is Any)

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
                loggingService.error("Failed to dispose game.", e)
            }
        }

        games.clear()
    }

    /**
     * Initialises a new game from the given arena.
     */
    private fun initGame(arena: Arena) {
        val game: Game = when (arena.gameType) {
            GameType.HUBGAME -> HubGameEntity(arena)
            GameType.MINIGAME -> MiniGameEntity(arena)
            else -> BungeeCordGameEntity(arena)
        }

        if (game is BungeeCordGame && game.arena.enabled) {
            games.add(game)

            for (player in proxyService.getOnlinePlayers<Any>()) {
                if (!getGameFromPlayer(player).isPresent) {
                    gameActionService.joinGame(game, player)
                }
            }
        } else {
            games.add(game)
        }
    }
}