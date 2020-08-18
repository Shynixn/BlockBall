package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.business.extension.thenAcceptSafely
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.google.inject.Inject
import kotlin.math.abs

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
class MainConfigurationPage @Inject constructor(
    private val configurationService: ConfigurationService,
    private val arenaRepository: PersistenceArenaService,
    private val blockSelectionService: BlockSelectionService,
    private val virtualArenaService: VirtualArenaService,
    private val screenMessageService: ScreenMessageService,
    private val gameService: GameService,
    private val proxyService: ProxyService
) : Page(ID, OpenPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 2
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.MAINCONFIGURATION
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(
        player: P,
        command: MenuCommand,
        cache: Array<Any?>,
        args: Array<String>
    ): MenuCommandResult {
        val prefix = configurationService.findValue<String>("messages.prefix")

        if (command == MenuCommand.ARENA_CREATE) {

        } else if (command == MenuCommand.ARENA_EDIT) {
            val arenas = arenaRepository.getArenas()
            cache[0] = arenas.single { b -> b.name.equals(args[2], true) }
        } else if (command == MenuCommand.ARENA_DELETE) {
            val arenas = arenaRepository.getArenas()
            cache[0] = arenas.single { b -> b.name.equals(args[2], true) }
            arenaRepository.remove(cache[0] as Arena)
            cache[0] = null
            return MenuCommandResult.BACK
        } else if (command == MenuCommand.ARENA_ENABLE) {
            val arena = cache[0] as Arena
            arena.enabled = !arena.enabled
        } else if (command == MenuCommand.ARENA_SETBALLSPAWNPOINT) {
            val arena = cache[0] as Arena
            arena.meta.ballMeta.spawnpoint = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        } else if (command == MenuCommand.ARENA_SETDISPLAYNAME) {
            val arena = cache[0] as Arena
            arena.displayName = this.mergeArgs(2, args)
        } else if (command == MenuCommand.ARENA_SETAREA) {
            val arena = cache[0] as Arena
            blockSelectionService.setSelectionToolForPlayer(player)
            val left = blockSelectionService.getLeftClickLocation<Any, P>(player)
            val right = blockSelectionService.getRightClickLocation<Any, P>(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = proxyService.toPosition(left.get())
                val rightPosition = proxyService.toPosition(right.get())
                val yDistance = abs(leftPosition.y - rightPosition.y)

                if (yDistance < 10) {
                    return MenuCommandResult.WESELECTION_TOSMALL
                }

                arena.setCorners(leftPosition, rightPosition)
            } else {
                return MenuCommandResult.WESELECTION_MISSING
            }
        } else if (command == MenuCommand.ARENA_SETGOALRED) {
            val arena = cache[0] as Arena

            blockSelectionService.setSelectionToolForPlayer(player)
            val left = blockSelectionService.getLeftClickLocation<Any, P>(player)
            val right = blockSelectionService.getRightClickLocation<Any, P>(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = proxyService.toPosition(left.get())
                val rightPosition = proxyService.toPosition(right.get())
                val xDistance = abs(leftPosition.x - rightPosition.x)
                val yDistance = abs(leftPosition.y - rightPosition.y)
                val zDistance = abs(leftPosition.z - rightPosition.z)

                if (yDistance < 2) {
                    return MenuCommandResult.WESELECTIONHEIGHTGOAL_TOSMALL
                }

                if (zDistance < 2) {
                    return MenuCommandResult.WESELECTIONZAXEGOAL_TOSMALL
                }

                if (xDistance < 2) {
                    return MenuCommandResult.WESELECTIONXAXEGOAL_TOSMALL
                }

                arena.meta.redTeamMeta.goal.setCorners(leftPosition, rightPosition)
                virtualArenaService.displayForPlayer(player, arena)
                screenMessageService.setActionBar(
                    player,
                    prefix + "Changed goal selection. Rendering virtual blocks..."
                )
            } else {
                return MenuCommandResult.WESELECTION_MISSING
            }
        } else if (command == MenuCommand.ARENA_SETGOALBLUE) {
            val arena = cache[0] as Arena

            blockSelectionService.setSelectionToolForPlayer(player)
            val left = blockSelectionService.getLeftClickLocation<Any, P>(player)
            val right = blockSelectionService.getRightClickLocation<Any, P>(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = proxyService.toPosition(left.get())
                val rightPosition = proxyService.toPosition(right.get())
                val xDistance = abs(leftPosition.x - rightPosition.x)
                val yDistance = abs(leftPosition.y - rightPosition.y)
                val zDistance = abs(leftPosition.z - rightPosition.z)

                if (yDistance < 2) {
                    return MenuCommandResult.WESELECTIONHEIGHTGOAL_TOSMALL
                }

                if (zDistance < 2) {
                    return MenuCommandResult.WESELECTIONZAXEGOAL_TOSMALL
                }

                if (xDistance < 2) {
                    return MenuCommandResult.WESELECTIONXAXEGOAL_TOSMALL
                }

                arena.meta.blueTeamMeta.goal.setCorners(leftPosition, rightPosition)
                virtualArenaService.displayForPlayer(player, arena)
                screenMessageService.setActionBar(
                    player,
                    prefix + "Changed goal selection. Rendering virtual blocks..."
                )
            } else {
                return MenuCommandResult.WESELECTION_MISSING
            }
        } else if (command == MenuCommand.ARENA_SAVE) {
            if (cache[0] == null || cache[0] !is Arena) {
                val b = ChatBuilderEntity().text("- ")
                    .text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                proxyService.sendMessage(player, b)

                return MenuCommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.worldName != null && arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null && arena.meta.redTeamMeta.goal.lowerCorner.worldName != null
                && arena.meta.ballMeta.spawnpoint != null
            ) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    arenaRepository.save(arena)
                } else {
                    return MenuCommandResult.MINIGAMEARENA_NOTVALID
                }
            } else {
                return MenuCommandResult.ARENA_NOTVALID
            }
        } else if (command == MenuCommand.ARENA_RELOAD) {
            if (cache[0] == null || cache[0] !is Arena) {
                val b = ChatBuilderEntity().text("- ")
                    .text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                proxyService.sendMessage(player, b)

                gameService.restartGames()
                return MenuCommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.worldName != null && arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null && arena.meta.redTeamMeta.goal.lowerCorner.worldName != null
                && arena.meta.ballMeta.spawnpoint != null
            ) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    val name = arena.name
                    arenaRepository.save(arena).thenAcceptSafely {
                        gameService.restartGames().thenAcceptSafely {
                            cache[0] = arenaRepository.getArenas().single { a -> a.name == name }
                        }
                    }
                } else {
                    return MenuCommandResult.MINIGAMEARENA_NOTVALID
                }
            } else {
                return MenuCommandResult.ARENA_NOTVALID
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
            corners = arena.center.toString()
        }
        if (arena.meta.redTeamMeta.goal.lowerCorner.worldName != null) {
            goal1 = arena.meta.redTeamMeta.goal.center.toString()
        }
        if (arena.meta.blueTeamMeta.goal.lowerCorner.worldName != null) {
            goal2 = arena.meta.blueTeamMeta.goal.center.toString()
        }
        if (arena.meta.ballMeta.spawnpoint != null) {
            ballSpawn = arena.meta.ballMeta.spawnpoint!!.toString()
        }
        return ChatBuilderEntity()
            .component("- Id: " + arena.name)
            .setColor(ChatColor.GRAY)
            .builder()
            .component(", " + arena.displayName).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.ARENA_SETDISPLAYNAME.command)
            .setHoverText("Edit the name of the arena.")
            .builder().nextLine()
            .component("- Enabled: " + arena.enabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_ENABLE.command)
            .setHoverText("Toggle the arena.")
            .builder().nextLine()
            .component("- Field: $corners").builder()
            .component(MenuClickableItem.SELECTION.text).setColor(ChatColor.GOLD)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SETAREA.command)
            .setHoverText("Uses the selected blocks to span the field of the arena.")
            .builder().nextLine()
            .component("- Goal Red: $goal1").builder()
            .component(MenuClickableItem.SELECTION.text).setColor(ChatColor.GOLD)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SETGOALRED.command)
            .setHoverText("Uses the selected blocks to span the goal for the red team.")
            .builder().nextLine()
            .component("- Goal Blue: $goal2").builder()
            .component(MenuClickableItem.SELECTION.text).setColor(ChatColor.GOLD)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SETGOALBLUE.command)
            .setHoverText("Uses the selected blocks to span the goal for the blue team.")
            .builder().nextLine()
            .component("- Ball spawnpoint: $ballSpawn").builder()
            .component(" [location..]").setColor(ChatColor.BLUE)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SETBALLSPAWNPOINT.command)
            .setHoverText("Uses your current location to set the spawnpoint of the ball.")
            .builder().nextLine()
            .component("- Settings:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SETTINGS_OPEN.command)
            .setHoverText("Opens the settings page.").builder()
    }
}