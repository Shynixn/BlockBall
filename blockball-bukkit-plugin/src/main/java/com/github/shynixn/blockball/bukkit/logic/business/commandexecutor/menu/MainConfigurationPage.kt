package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.bukkit.logic.business.extension.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.Location
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
class MainConfigurationPage @Inject constructor(private val configurationService: ConfigurationService, private val arenaRepository: PersistenceArenaService, private val blockSelectionService: BlockSelectionService) : Page(MainConfigurationPage.ID, OpenPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 2
    }

    @Inject
    private lateinit var virtualArenaService: VirtualArenaService

    @Inject
    private lateinit var screenMessageService: ScreenMessageService

    @Inject
    private lateinit var gameService: GameService

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.MAINCONFIGURATION
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val prefix = configurationService.findValue<String>("messages.prefix")

        if (command == BlockBallCommand.ARENA_CREATE) {

        } else if (command == BlockBallCommand.ARENA_EDIT) {
            val arenas = arenaRepository.getArenas()
            cache[0] = arenas.single { b -> b.name.equals(args[2], true) }
        } else if (command == BlockBallCommand.ARENA_DELETE) {
            val arenas = arenaRepository.getArenas()
            cache[0] = arenas.single { b -> b.name.equals(args[2], true) }
            arenaRepository.remove(cache[0] as Arena)
            cache[0] = null
            return CommandResult.BACK
        } else if (command == BlockBallCommand.ARENA_ENABLE) {
            val arena = cache[0] as Arena
            arena.enabled = !arena.enabled
        } else if (command == BlockBallCommand.ARENA_SETBALLSPAWNPOINT) {
            val arena = cache[0] as Arena
            arena.meta.ballMeta.spawnpoint = player.location.toPosition()
        } else if (command == BlockBallCommand.ARENA_SETDISPLAYNAME) {
            val arena = cache[0] as Arena
            arena.displayName = this.mergeArgs(2, args)
        } else if (command == BlockBallCommand.ARENA_SETAREA) {
            val arena = cache[0] as Arena
            val left = blockSelectionService.getLeftClickLocation<Location, Player>(player)
            val right = blockSelectionService.getRightClickLocation<Location, Player>(player)
            if (left.isPresent && right.isPresent) {
                arena.setCorners(left.get(), right.get())
            } else {
                return CommandResult.WESELECTION_MISSING
            }
        } else if (command == BlockBallCommand.ARENA_SETGOALRED) {
            val arena = cache[0] as Arena
            val left = blockSelectionService.getLeftClickLocation<Location, Player>(player)
            val right = blockSelectionService.getRightClickLocation<Location, Player>(player)
            if (left.isPresent && right.isPresent) {
                arena.meta.redTeamMeta.goal.setCorners(left.get(), right.get())
                virtualArenaService.displayForPlayer(player, arena)
                screenMessageService.setActionBar(player, prefix + "Changed goal selection. Rendering virtual blocks...")
            } else {
                return CommandResult.WESELECTION_MISSING
            }
        } else if (command == BlockBallCommand.ARENA_SETGOALBLUE) {
            val arena = cache[0] as Arena
            val left = blockSelectionService.getLeftClickLocation<Location, Player>(player)
            val right = blockSelectionService.getRightClickLocation<Location, Player>(player)
            if (left.isPresent && right.isPresent) {
                arena.meta.blueTeamMeta.goal.setCorners(left.get(), right.get())
                virtualArenaService.displayForPlayer(player, arena)
                screenMessageService.setActionBar(player, prefix + "Changed goal selection. Rendering virtual blocks...")
            } else {
                return CommandResult.WESELECTION_MISSING
            }
        } else if (command == BlockBallCommand.ARENA_SAVE) {
            if (cache[0] == null || cache[0] !is Arena) {
                ChatBuilder().text("- ").text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                        .sendMessage(player)

                return CommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.worldName != null && arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null && arena.meta.redTeamMeta.goal.lowerCorner.worldName != null
                    && arena.meta.ballMeta.spawnpoint != null) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    arenaRepository.save(arena)
                } else {
                    return CommandResult.MINIGAMEARENA_NOTVALID
                }
            } else {
                return CommandResult.ARENA_NOTVALID
            }
        } else if (command == BlockBallCommand.ARENA_RELOAD) {
            if (cache[0] == null || cache[0] !is Arena) {
                ChatBuilder().text("- ").text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                        .sendMessage(player)

                gameService.restartGames()
                return CommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.worldName != null && arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null && arena.meta.redTeamMeta.goal.lowerCorner.worldName != null
                    && arena.meta.ballMeta.spawnpoint != null) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    val name = arena.name
                    arenaRepository.save(arena).thenAccept {
                        gameService.restartGames().thenAccept {
                            cache[0] = arenaRepository.getArenas().single { a -> a.name == name }
                        }
                    }
                } else {
                    return CommandResult.MINIGAMEARENA_NOTVALID
                }
            } else {
                return CommandResult.ARENA_NOTVALID
            }
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val arena = cache[0] as Arena
        var corners = "none"
        var goal1 = "none"
        var goal2 = "none"
        var ballSpawn = "none"
        if (arena.upperCorner.worldName != null && arena.lowerCorner.worldName != null) {
            corners = this.printLocation(arena.center)
        }
        if (arena.meta.redTeamMeta.goal.lowerCorner.worldName != null) {
            goal1 = this.printLocation(arena.meta.redTeamMeta.goal.center)
        }
        if (arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null) {
            goal2 = this.printLocation(arena.meta.blueTeamMeta.goal.center)
        }
        if (arena.meta.ballMeta.spawnpoint != null) {
            ballSpawn = this.printLocation(arena.meta.ballMeta.spawnpoint!!)
        }
        return ChatBuilder()
                .component("- Id: " + arena.name)
                .setColor(ChatColor.GRAY)
                .builder()
                .component(", " + arena.displayName).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.ARENA_SETDISPLAYNAME.command)
                .setHoverText("Edit the name of the arena.")
                .builder().nextLine()
                .component("- Enabled: " + arena.enabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_ENABLE.command)
                .setHoverText("Toggle the arena.")
                .builder().nextLine()
                .component("- Field: $corners").builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETAREA.command)
                .setHoverText("Uses the selected worldedit blocks to span the field of the arena.")
                .builder().nextLine()
                .component("- Goal Red: $goal1").builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETGOALRED.command)
                .setHoverText("Uses the selected worldedit blocks to span the goal for the red team.")
                .builder().nextLine()
                .component("- Goal Blue: $goal2").builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETGOALBLUE.command)
                .setHoverText("Uses the selected worldedit blocks to span the goal for the blue team.")
                .builder().nextLine()
                .component("- Ball spawnpoint: $ballSpawn").builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETBALLSPAWNPOINT.command)
                .setHoverText("Uses your current location to set the spawnpoint of the ball.")
                .builder().nextLine()
                .component("- Settings:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SETTINGS_OPEN.command)
                .setHoverText("Opens the settings page.").builder()

    }
}