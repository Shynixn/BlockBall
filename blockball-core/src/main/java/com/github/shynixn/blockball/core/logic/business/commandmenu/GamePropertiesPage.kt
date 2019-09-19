package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity

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
class GamePropertiesPage : Page(GamePropertiesPage.ID, MiscSettingsPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 25
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.GAMEEXTENSIONS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena

        if (command == MenuCommand.GAMEPROPERTIES_TOGGLE_DAMAGE) {
            arena.meta.customizingMeta.damageEnabled = !arena.meta.customizingMeta.damageEnabled
        } else if (command == MenuCommand.GAMEPROPERTIES_TOGGLE_TELEPORTBACK) {
            arena.meta.customizingMeta.backTeleport = !arena.meta.customizingMeta.backTeleport
        } else if (command == MenuCommand.GAMEPROPERTIES_TELEPORTBACKDELAY && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.customizingMeta.backTeleportDelay = args[2].toInt()
        } else if (command == MenuCommand.GAMEPROPERTIES_TOGGLE_BALLFORCEFIELD) {
            arena.meta.customizingMeta.ballForceField = !arena.meta.customizingMeta.ballForceField
        } else if (command == MenuCommand.GAMEPROPERTIES_TOGGLE_KEEPINVENTORY) {
            arena.meta.customizingMeta.keepInventoryEnabled = !arena.meta.customizingMeta.keepInventoryEnabled
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
        val meta = arena.meta.customizingMeta
        return ChatBuilderEntity()
            .component("- Ball forcefield enabled: " + meta.ballForceField).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(
                ChatClickAction.RUN_COMMAND,
                MenuCommand.GAMEPROPERTIES_TOGGLE_BALLFORCEFIELD.command
            )
            .setHoverText("Toggles the ball forcefield.")
            .builder().nextLine()
            .component("- Damage enabled: " + meta.damageEnabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMEPROPERTIES_TOGGLE_DAMAGE.command)
            .setHoverText("Toggles the dealing damage in the arena.")
            .builder().nextLine()
            .component("- Score teleport back: " + meta.backTeleport).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(
                ChatClickAction.RUN_COMMAND,
                MenuCommand.GAMEPROPERTIES_TOGGLE_TELEPORTBACK.command
            )
            .setHoverText("Toggles if players should be teleported back to their game spawnpoint after anyone scores a point.")
            .builder().nextLine()
            .component("- Score teleport back delay: " + arena.meta.customizingMeta.backTeleportDelay).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(
                ChatClickAction.SUGGEST_COMMAND,
                MenuCommand.GAMEPROPERTIES_TELEPORTBACKDELAY.command
            )
            .setHoverText("Delay after the players get teleported back to their game spawnpoint.")
            .builder().nextLine()
            .component("- Keep inventory: " + meta.keepInventoryEnabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(
                ChatClickAction.RUN_COMMAND,
                MenuCommand.GAMEPROPERTIES_TOGGLE_KEEPINVENTORY.command
            )
            .setHoverText("Toggles if players should keep their current inventory when they join a match.")
            .builder().nextLine()
    }
}