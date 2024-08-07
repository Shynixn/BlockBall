package com.github.shynixn.blockball.impl.commandmenu


import com.github.shynixn.blockball.contract.BlockSelectionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.toVector3d
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import kotlin.math.abs

class MainConfigurationPage @Inject constructor(
    private val arenaRepository: CacheRepository<Arena>,
    private val blockSelectionService: BlockSelectionService,
    private val gameService: GameService,
    private val plugin: Plugin,
    private val chatMessageService: ChatMessageService
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
        if (player !is Player) {
            return MenuCommandResult.BACK
        }

        if (command == MenuCommand.ARENA_CREATE) {

        } else if (command == MenuCommand.ARENA_EDIT) {
            plugin.launch {
                val arenas = arenaRepository.getAll()
                cache[0] = arenas.single { b -> b.name.equals(args[2], true) }
            }
        } else if (command == MenuCommand.ARENA_DELETE) {
            plugin.launch {
                cache[0] = arenaRepository.getAll().single { b -> b.name.equals(args[2], true) }
                arenaRepository.delete(cache[0] as Arena)
                gameService.reloadAll()
            }
            cache[0] = null
            return MenuCommandResult.BACK
        } else if (command == MenuCommand.ARENA_ENABLE) {
            val arena = cache[0] as Arena
            arena.enabled = !arena.enabled
        } else if (command == MenuCommand.ARENA_SETBALLSPAWNPOINT) {
            val arena = cache[0] as Arena
            arena.meta.ballMeta.spawnpoint = player.location.toVector3d()
        } else if (command == MenuCommand.ARENA_SETDISPLAYNAME) {
            val arena = cache[0] as Arena
            arena.displayName = this.mergeArgs(2, args)
        } else if (command == MenuCommand.ARENA_SETAREA) {
            val arena = cache[0] as Arena
            blockSelectionService.setSelectionToolForPlayer(player)
            val left = blockSelectionService.getLeftClickLocation(player)
            val right = blockSelectionService.getRightClickLocation(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = left.get().toVector3d()
                val rightPosition = right.get().toVector3d()
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
            val left = blockSelectionService.getLeftClickLocation(player)
            val right = blockSelectionService.getRightClickLocation(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = left.get().toVector3d()
                val rightPosition =right.get().toVector3d()
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
            } else {
                return MenuCommandResult.WESELECTION_MISSING
            }
        } else if (command == MenuCommand.ARENA_SETGOALBLUE) {
            val arena = cache[0] as Arena

            blockSelectionService.setSelectionToolForPlayer(player)
            val left = blockSelectionService.getLeftClickLocation(player)
            val right = blockSelectionService.getRightClickLocation(player)
            if (left.isPresent && right.isPresent) {
                val leftPosition = left.get().toVector3d()
                val rightPosition = right.get().toVector3d()
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
            } else {
                return MenuCommandResult.WESELECTION_MISSING
            }
        } else if (command == MenuCommand.ARENA_SAVE) {
            if (cache[0] == null || cache[0] !is Arena) {
                val b = ChatBuilder().text("- ")
                    .text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                chatMessageService.sendChatMessage(player, b.convertToTextComponent())
                return MenuCommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.world != null && arena.meta.blueTeamMeta.goal.lowerCorner.world != null && arena.meta.redTeamMeta.goal.lowerCorner.world != null
                && arena.meta.ballMeta.spawnpoint != null
            ) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    plugin.launch {
                        arenaRepository.save(arena)
                    }
                } else {
                    return MenuCommandResult.MINIGAMEARENA_NOTVALID
                }
            } else {
                return MenuCommandResult.ARENA_NOTVALID
            }
        } else if (command == MenuCommand.ARENA_RELOAD) {
            if (cache[0] == null || cache[0] !is Arena) {
                val b = ChatBuilder().text("- ")
                    .text(ChatColor.RED.toString() + "Please select an arena to perform this action.")
                chatMessageService.sendChatMessage(player, b.convertToTextComponent())

                // TODO: Should be replaced in command rework.
                runBlocking {
                    gameService.reloadAll()
                }

                return MenuCommandResult.CANCEL_MESSAGE
            }

            val arena = cache[0] as Arena
            if (arena.lowerCorner.world != null && arena.meta.blueTeamMeta.goal.lowerCorner.world != null && arena.meta.redTeamMeta.goal.lowerCorner.world != null
                && arena.meta.ballMeta.spawnpoint != null
            ) {
                if (arena.gameType === GameType.HUBGAME || (arena.meta.minigameMeta.lobbySpawnpoint != null && arena.meta.lobbyMeta.leaveSpawnpoint != null)) {
                    val name = arena.name
                    plugin.launch {
                        try {
                            arenaRepository.save(arena)
                            gameService.reloadAll()
                            cache[0] = arenaRepository.getAll().single { a -> a.name == name }
                        } catch (e: Exception) {
                            plugin.logger.log(Level.SEVERE, "Failed persistence arena.", e)
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
        if (arena.upperCorner.world != null && arena.lowerCorner.world != null) {
            corners = arena.center.toString()
        }
        if (arena.meta.redTeamMeta.goal.lowerCorner.world != null) {
            goal1 = arena.meta.redTeamMeta.goal.center.toString()
        }
        if (arena.meta.blueTeamMeta.goal.lowerCorner.world != null) {
            goal2 = arena.meta.blueTeamMeta.goal.center.toString()
        }
        if (arena.meta.ballMeta.spawnpoint != null) {
            ballSpawn = arena.meta.ballMeta.spawnpoint!!.toString()
        }
        return ChatBuilder()
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
