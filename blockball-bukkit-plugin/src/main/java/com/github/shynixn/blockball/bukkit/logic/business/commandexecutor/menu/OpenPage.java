package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
public class OpenPage extends Page {
    public static final int ID = 1;

    public OpenPage() {
        super(OpenPage.ID, OpenPage.ID);
    }

    @Inject
    private ArenaRepository arenaRepository;

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    @Override
    public PageKey getCommandKey() {
        return PageKey.OPEN;
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    @Override
    public CommandResult execute(Player player, BlockBallCommand command, Object[] cache, String[] args) {
     if (command == BlockBallCommand.OPEN_EDIT_ARENA) {
            ChatBuilder builder = null;
            for (final BukkitArena arena : this.arenaRepository.getAll()) {
                if (builder == null) {
                    builder = new ChatBuilder();
                }
                builder.component("- Arena: Id: " + arena.getName() + " Name: " + arena.getDisplayName()).builder()
                        .component(" [page..]").setColor(ChatColor.YELLOW)
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND
                                , BlockBallCommand.ARENA_EDIT.getCommand() + " " + arena.getName())
                        .setHoverText("Opens the arena with the id " + arena.getId() + ".").builder().nextLine();
            }
            if (builder != null) {
                builder.sendMessage(player);
            }
            return CommandResult.CANCEL_MESSAGE;
        } else if (command == BlockBallCommand.OPEN_DELETE_ARENA) {
            ChatBuilder builder = null;
            for (final Arena arena : this.arenaRepository.getAll()) {
                if (builder == null) {
                    builder = new ChatBuilder();
                }
                builder.component("- Arena: Id: " + arena.getName() + " Name: " + arena.getDisplayName()).builder()
                        .component(" [delete..]").setColor(ChatColor.DARK_RED)
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND
                                , BlockBallCommand.ARENA_DELETE.getCommand() + " " + arena.getName())
                        .setHoverText("Deletes the arena with the id " + arena.getId() + ".").builder().nextLine();
            }
            if (builder != null) {
                builder.sendMessage(player);
            }
            return CommandResult.CANCEL_MESSAGE;
        }
        return super.execute(player, command, cache, args);
    }

    /**
     * Builds this page for the player.
     *
     * @param cache cache.
     * @return page
     */
    @Override
    public ChatBuilder buildPage(Object[] cache) {
        return new ChatBuilder()
                .component("- Create arena:").builder()
                .component(" [create..]").setColor(ChatColor.AQUA)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_CREATE.getCommand())
                .setHoverText("Creates a new blockball arena.").builder().nextLine()
                .component("- Edit arena:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.OPEN_EDIT_ARENA.getCommand())
                .setHoverText("Opens the arena selection list.").builder().nextLine()
                .component("- Remove arena:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.OPEN_DELETE_ARENA.getCommand())
                .setHoverText("Deletes a blockball arena.").builder();
    }
}
