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
class DoubleJumpPage : Page(DoubleJumpPage.ID, OpenPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 18
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.DOUBLEJUMP
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        if (command == MenuCommand.DOUBLEJUMP_TOGGLE) {
            arena.meta.doubleJumpMeta.enabled = !arena.meta.doubleJumpMeta.enabled
        } else if (command == MenuCommand.DOUBLEJUMP_COOLDOWN && args[2].toIntOrNull() != null) {
            arena.meta.doubleJumpMeta.cooldown = args[2].toInt()
        } else if (command == MenuCommand.DOUBLEJUMP_VERTICAL_STRENGTH && args[2].toDoubleOrNull() != null) {
            arena.meta.doubleJumpMeta.verticalStrength = args[2].toDouble()
        } else if (command == MenuCommand.DOUBLEJUMP_HORIZONTAL_STRENGTH && args[2].toDoubleOrNull() != null) {
            arena.meta.doubleJumpMeta.horizontalStrength = args[2].toDouble()
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
        val meta = arena.meta.doubleJumpMeta
        return ChatBuilderEntity()
            .component("- Enabled: " + meta.enabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.DOUBLEJUMP_TOGGLE.command)
            .setHoverText("Toggles the double jump.")
            .builder().nextLine()
            .component("- Cooldown: " + meta.cooldown).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.DOUBLEJUMP_COOLDOWN.command)
            .setHoverText("Changes the cooldown between double jumps.")
            .builder().nextLine()
            .component("- Vertical strength: " + meta.verticalStrength).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.DOUBLEJUMP_VERTICAL_STRENGTH.command)
            .setHoverText("Changes the vertical strength modifier a player is flying.")
            .builder().nextLine()
            .component("- Horizontal strength: " + meta.horizontalStrength).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.DOUBLEJUMP_HORIZONTAL_STRENGTH.command)
            .setHoverText("Changes the horizontal strength modifier a player is flying.")
            .builder().nextLine()
            .component("- Particleeffect:").builder()
            .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.PARTICLE_DOUBLEJUMP.command)
            .setHoverText("Opens the particleEffect page.")
            .builder().nextLine()
            .component("- Soundeffect:").builder()
            .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SOUND_DOUBLEJUMP.command)
            .setHoverText("Opens the soundEffect page.").builder()
    }
}