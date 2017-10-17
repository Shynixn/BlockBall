package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

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
public final class LeaveCommandExecutor extends SimpleCommandExecutor.UnRegistered {

    private final GameController gameController;

    /**
     * Initializes a new commandExecutor by all required parameters.
     *
     * @param plugin plugin
     */
    public LeaveCommandExecutor(GameController gameController, Plugin plugin) {
        super(Config.getInstance().getLeaveCommandName()
                , Config.getInstance().getLeaveCommandUseag()
                , Config.getInstance().getLeaveCommandDescription()
                , Config.getInstance().getLeaveCommandPermission()
                , Config.getInstance().getLeaveCommandPermissionMessage(), (JavaPlugin) plugin);
        this.gameController = gameController;
    }

    /**
     * Can be overwritten to listen to player executed commands
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
        final Optional<Game> gameOpt = this.gameController.getGameFromPlayer(player);
        gameOpt.ifPresent(game -> game.leave(player));
    }
}
