package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.util.Vector

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
class AreaProtectionPage : Page(AreaProtectionPage.ID, MiscSettingsPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 26
    }

    @Inject
    private var arenaRepository: ArenaRepository? = null

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.AREAPROTECTION
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as BukkitArena
        if (command == BlockBallCommand.AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD) {
            arena.meta.protectionMeta.entityProtectionEnabled = !arena.meta.protectionMeta.entityProtectionEnabled
        } else if (command == BlockBallCommand.AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD) {
            arena.meta.protectionMeta.rejoinProtectionEnabled = !arena.meta.protectionMeta.rejoinProtectionEnabled
        }
        else if (command == BlockBallCommand.AREAPROTECTION_SET_ENTITYFORCEFIELD && args.size >= 5
                && args[2].toDoubleOrNull() != null && args[3].toDoubleOrNull() != null  && args[4].toDoubleOrNull() != null) {
            arena.meta.protectionMeta.entityProtection = Vector(args[2].toDouble(), args[3].toDouble(), args[4].toDouble())
        }
        else if (command == BlockBallCommand.AREAPROTECTION_SET_PLAYERJOINFORCEFIELD && args.size >= 5
                && args[2].toDoubleOrNull() != null && args[3].toDoubleOrNull() != null  && args[4].toDoubleOrNull() != null) {
            arena.meta.protectionMeta.rejoinProtection = Vector(args[2].toDouble(), args[3].toDouble(), args[4].toDouble())
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val arena = cache[0] as BukkitArena
        val meta = arena.meta.protectionMeta
        return ChatBuilder()
                .component("- Animal and Monster protection enabled: " + meta.entityProtectionEnabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD.command)
                .setHoverText("Toggles allowing animals and monsters to run inside of the arena.")
                .builder().nextLine()
                .component("- Animal and Monster protection velocity: " + meta.entityProtection).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.AREAPROTECTION_SET_ENTITYFORCEFIELD.command)
                .setHoverText("Changes the velocity being applied to animals and monsters when they try to enter the arena." +
                        "\nEnter 3 values when using this command.\n/blockball aprot enprot <x> <y> <z>")
                .builder().nextLine()
                .component("- Join protection enabled: " + meta.rejoinProtectionEnabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD.command)
                .setHoverText("Toggles the protection to move players outside of the arena.")
                .builder().nextLine()
                .component("- Join protection velocity: " + meta.rejoinProtection).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.AREAPROTECTION_SET_PLAYERJOINFORCEFIELD.command)
                .setHoverText("Changes the velocity being applied to players when they try to enter the arena." +
                        "\nEnter 3 values when using this command.\n/blockball aprot plprot <x> <y> <z>")
                .builder().nextLine()
    }
}