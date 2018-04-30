package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.entity.Player

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
class OpenPage : Page(OpenPage.ID, OpenPage.ID) {
    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.OPEN
    }

    @Inject
    private val arenaRepository: ArenaRepository? = null

    companion object {
        /** Id of the page. */
        const val ID = 1
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        if (command == BlockBallCommand.OPEN_EDIT_ARENA) {
            var builder: ChatBuilder? = null
            for (arena in this.arenaRepository!!.getAll()) {
                if (builder == null) {
                    builder = ChatBuilder()
                }
                builder.component("- Arena: Id: " + arena.name + " Name: " + arena.displayName).builder()
                        .component(" [page..]").setColor(ChatColor.YELLOW)
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_EDIT.command + " " + arena.name)
                        .setHoverText("Opens the arena with the id " + arena.id + ".").builder().nextLine()
            }
            if (builder != null) {
                builder.sendMessage(player)
            }
            return CommandResult.CANCEL_MESSAGE
        } else if (command == BlockBallCommand.OPEN_DELETE_ARENA) {
            var builder: ChatBuilder? = null
            for (arena in this.arenaRepository!!.getAll()) {
                if (builder == null) {
                    builder = ChatBuilder()
                }
                builder.component("- Arena: Id: " + arena.name + " Name: " + arena.displayName).builder()
                        .component(" [delete..]").setColor(ChatColor.DARK_RED)
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_DELETE.command + " " + arena.name)
                        .setHoverText("Deletes the arena with the id " + arena.id + ".").builder().nextLine()
            }
            if (builder != null) {
                builder.sendMessage(player)
            }
            return CommandResult.CANCEL_MESSAGE
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @param cache cache.
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        return ChatBuilder()
                .component("- Create arena:").builder()
                .component(" [create..]").setColor(ChatColor.AQUA)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEMPLATE_OPEN.command)
                .setHoverText("Creates a new blockball arena.").builder().nextLine()
                .component("- Edit arena:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.OPEN_EDIT_ARENA.command)
                .setHoverText("Opens the arena selection list.").builder().nextLine()
                .component("- Remove arena:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.OPEN_DELETE_ARENA.command)
                .setHoverText("Deletes a blockball arena.").builder()
    }
}