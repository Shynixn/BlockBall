package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity

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

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.AREAPROTECTION
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        if (command == MenuCommand.AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD) {
            arena.meta.protectionMeta.entityProtectionEnabled = !arena.meta.protectionMeta.entityProtectionEnabled
        } else if (command == MenuCommand.AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD) {
            arena.meta.protectionMeta.rejoinProtectionEnabled = !arena.meta.protectionMeta.rejoinProtectionEnabled
        } else if (command == MenuCommand.AREAPROTECTION_SET_ENTITYFORCEFIELD && args.size >= 5
            && args[2].toDoubleOrNull() != null && args[3].toDoubleOrNull() != null && args[4].toDoubleOrNull() != null
        ) {
            arena.meta.protectionMeta.entityProtection = PositionEntity(args[2].toDouble(), args[3].toDouble(), args[4].toDouble())
        } else if (command == MenuCommand.AREAPROTECTION_SET_PLAYERJOINFORCEFIELD && args.size >= 5
            && args[2].toDoubleOrNull() != null && args[3].toDoubleOrNull() != null && args[4].toDoubleOrNull() != null
        ) {
            arena.meta.protectionMeta.rejoinProtection = PositionEntity(args[2].toDouble(), args[3].toDouble(), args[4].toDouble())
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
        val meta = arena.meta.protectionMeta
        return ChatBuilderEntity()
            .component("- Animal and Monster protection enabled: " + meta.entityProtectionEnabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD.command)
            .setHoverText("Toggles allowing animals and monsters to run inside of the arena.")
            .builder().nextLine()
            .component("- Animal and Monster protection velocity: " + meta.entityProtection).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.AREAPROTECTION_SET_ENTITYFORCEFIELD.command)
            .setHoverText(
                "Changes the velocity being applied to animals and monsters when they try to enter the arena." +
                        "\nEnter 3 values when using this command.\n/blockball aprot enprot <x> <y> <z>"
            )
            .builder().nextLine()
            .component("- Join protection enabled: " + meta.rejoinProtectionEnabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD.command)
            .setHoverText("Toggles the protection to move players outside of the arena.")
            .builder().nextLine()
            .component("- Join protection velocity: " + meta.rejoinProtection).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.AREAPROTECTION_SET_PLAYERJOINFORCEFIELD.command)
            .setHoverText(
                "Changes the velocity being applied to players when they try to enter the arena." +
                        "\nEnter 3 values when using this command.\n/blockball aprot plprot <x> <y> <z>"
            )
            .builder().nextLine()
    }
}