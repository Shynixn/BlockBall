package com.github.shynixn.blockball.bukkit.logic.business.controller;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.api.persistence.controller.ArenaController;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.entity.BungeeGameEntity;
import com.github.shynixn.blockball.bukkit.logic.business.entity.EventGameEntity;
import com.github.shynixn.blockball.bukkit.logic.business.entity.HubGameEntity;
import com.github.shynixn.blockball.bukkit.logic.business.entity.MiniGameEntity;
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class GameRepository implements GameController, Runnable {
    private final ArenaController arenaController;
    private final List<Game> games = new ArrayList<>();
    private final BukkitTask task;

    /**
     * Initializes a new game repository.
     *
     * @param arenaController controller
     */
    public GameRepository(ArenaController arenaController) {
        super();
        this.arenaController = arenaController;
        this.task = ((ArenaRepository) arenaController).getPlugin().getServer()
                .getScheduler()
                .runTaskTimer(((ArenaRepository) arenaController).getPlugin(), this, 0L, 1L);
    }

    /**
     * Returns the controller of the arenas.
     *
     * @return controller
     */
    @Override
    public ArenaController getArenaController() {
        return this.arenaController;
    }

    /**
     * Reloads the content from the fileSystem.
     */
    @Override
    public void reload() {
        this.getArenaController().reload();
        for (final Game game : this.games) {
            try {
                game.close();
            } catch (final Exception e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to dispose game.", e);
            }
        }
        this.games.clear();
        for (final Arena arena : this.getArenaController().getAll()) {
            if (arena.getGameType() == GameType.BUNGEE) {
                this.games.add(new BungeeGameEntity(arena));
            } else if (arena.getGameType() == GameType.LOBBY) {
                this.games.add(new HubGameEntity(arena));
            } else if (arena.getGameType() == GameType.MINIGAME) {
                this.games.add(new MiniGameEntity(arena));
            } else if (arena.getGameType() == GameType.EVENT) {
                this.games.add(new EventGameEntity(arena));
            }
        }
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(Game item) {
        if (!this.games.contains(item)) {
            this.games.add(item);
        }
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(Game item) {
        if (this.games.contains(item)) {
            this.games.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    @Override
    public int size() {
        return this.games.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    @Override
    public List<Game> getAll() {
        return Collections.unmodifiableList(this.games);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.arenaController.close();
        this.task.cancel();
        for (final Game game : this.games) {
            game.close();
        }
        this.games.clear();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (final Game game : this.games) {
            game.run();
        }
    }
}
