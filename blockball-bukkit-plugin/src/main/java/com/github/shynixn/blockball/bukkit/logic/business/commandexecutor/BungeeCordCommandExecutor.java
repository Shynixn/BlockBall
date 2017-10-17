package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallBungeeCordManager;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles the sign creation command for bungeecord.
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
public class BungeeCordCommandExecutor extends SimpleCommandExecutor.UnRegistered {
    private final BlockBallBungeeCordManager manager;

    /**
     * Initializes a new bungeeCord command Executor.
     *
     * @param manager controller
     */
    public BungeeCordCommandExecutor(BlockBallBungeeCordManager manager) {
        super(BlockBallBungeeCordManager.COMMAND_COMMAND, BlockBallBungeeCordManager.COMMAND_USEAGE
                , BlockBallBungeeCordManager.COMMAND_DESCRIPTION, BlockBallBungeeCordManager.COMMAND_PERMISSION,
                BlockBallBungeeCordManager.COMMAND_PERMISSION_MESSAGE, JavaPlugin.getPlugin(BlockBallPlugin.class));
        this.manager = manager;
    }

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
        if (args.length == 1) {
            this.manager.signPlacementCache.put(player, args[0]);
            player.sendMessage(ChatColor.YELLOW + "Rightclick on a sign to convert it into a server sign.");
        }
    }
}
