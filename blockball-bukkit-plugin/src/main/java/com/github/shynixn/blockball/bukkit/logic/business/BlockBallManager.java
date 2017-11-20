package com.github.shynixn.blockball.bukkit.logic.business;

import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.Factory;
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.BlockBallReloadCommandExecutor;
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.NewArenaCommandExecutor;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.listener.BallListener;
import com.github.shynixn.blockball.bukkit.logic.business.listener.StatsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public class BlockBallManager implements AutoCloseable {
    private final BallController ballController;
    private final GameController gameController;

    /**
     * Initializes a new blockBall manager.
     *
     * @param plugin plugin
     */
    public BlockBallManager(Plugin plugin) {
        this.ballController = Factory.createBallController();
        this.gameController = Factory.createGameController(Factory.createArenaController(plugin));

        new BallListener(this, plugin);
        new NewArenaCommandExecutor(this, plugin);
        new BlockBallReloadCommandExecutor(this, plugin);

        Factory.initialize(plugin);

        this.gameController.reload();

        if (Config.getInstance().isStatsScoreboardEnabled()) {
            new StatsListener(plugin);
        }
    }

    /**
     * Returns the gameController.
     *
     * @return gameController
     */
    public GameController getGameController() {
        return this.gameController;
    }

    /**
     * Returns the ballController.
     *
     * @return controller
     */
    public BallController getBallController() {
        return this.ballController;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.ballController.close();
        this.gameController.close();
    }
}
