package com.github.shynixn.blockball.bukkit.logic.business.controller

import com.github.shynixn.blockball.api.bukkit.business.controller.BukkitGameController
import com.github.shynixn.blockball.api.bukkit.business.entity.BukkitGame
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.HubGame
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.LowLevelGame
import com.github.shynixn.blockball.bukkit.logic.business.listener.GameListener
import com.github.shynixn.blockball.bukkit.logic.business.listener.HubGameListener
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.google.inject.Inject
import com.google.inject.Singleton
import com.sk89q.worldedit.WorldEdit.logger
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.logging.Level

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
@Singleton
class GameRepository : BukkitGameController, Runnable {

    /** ArenaController. */
    @Inject
    override var arenaController: ArenaRepository? = null

    @Inject
    private var plugin: Plugin? = null;

    @Inject
    private var gameListener : GameListener? = null;

    @Inject
    private var hubGameListener : HubGameListener? = null;

    /** Games. */
    val games: ArrayList<BukkitGame> = ArrayList()
    private var task: BukkitTask? = null;

    /**
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        games.forEach { p -> (p as LowLevelGame).run() }
    }

    /** Removes an item from the repository. */
    override fun remove(item: BukkitGame) {
        if (this.games.contains(item)) {
            this.games.remove(item)
        }
    }

    /** Returns all items from the repository. */
    override fun getAll(): List<BukkitGame> {
        return games
    }

    /** Stores a new item into the repository. */
    override fun store(item: BukkitGame) {
        if (!this.games.contains(item)) {
            this.games.add(item)
        }
    }

    /** Returns the amount of items in the repository. */
    override val count: Int
        get() = games.size

    /** Returns the game with the given arena name. */
    override fun getGameFromArenaName(name: String): BukkitGame? {
        games.forEach { p ->
            if (p.arena.name.equals(name, true)) {
                return p
            }
        }
        return null
    }

    /** Returns the game with the [player] inside. */
    override fun getGameFromPlayer(player: Player): BukkitGame? {
        games.forEach { p ->
            if (p.hasJoined(player)) {
                return p
            }
        }
        return null
    }

    /** Returns the game with the given arena displayName. */
    override fun getGameFromArenaDisplayName(name: String): BukkitGame? {
        games.forEach { p ->
            if (p.arena.displayName.equals(name, true)) {
                return p
            }
        }
        return null
    }

    /** Reloads the contents in the cache of the controller. */
    override fun reload() {
        if (task == null) {
            task = plugin!!.server.scheduler.runTaskTimer(plugin, this, 0L, 1L)
        }
        this.arenaController!!.reload()
        for (game in this.games) {
            try {
                game.close()
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Failed to dispose game.", e)
            }
        }
        this.games.clear()
        this.arenaController!!.getAll().forEach { p ->
            if (p.gameType == GameType.HUBGAME) {
                this.store(HubGame(p))
            }
        }
    }
}