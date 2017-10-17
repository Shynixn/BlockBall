package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.lib.ChatBuilder;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
public class NewArenaCommandExecutor extends SimpleCommandExecutor.Registered {
    private static final String HEADER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                         Balls                      ";
    private static final String FOOTER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            ";

    /**
     * Initializes a new commandExecutor by command, plugin.
     *
     * @param plugin plugin
     */
    public NewArenaCommandExecutor(JavaPlugin plugin) {
        super("blockball", plugin);
    }

    /**
     * Can be overwritten to listen to player executed commands
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
        player.sendMessage(HEADER_STANDARD);
        if (args.length == 0) {
            printFirstPage();
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("crna")) {
                printArenaPage();
            }

        }

        player.sendMessage(FOOTER_STANDARD);
    }

    private void printArenaPage() {
        new ChatBuilder().component(">>Set arena<<").setColor(ChatColor.YELLOW).setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "blockball crna");
    }

    private void printFirstPage() {
        new ChatBuilder().component(">>Create arena<<").setColor(ChatColor.YELLOW).setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "blockball crna");
    }
}
