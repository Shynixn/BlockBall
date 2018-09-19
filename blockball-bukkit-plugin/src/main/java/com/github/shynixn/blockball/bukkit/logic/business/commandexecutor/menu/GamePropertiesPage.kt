package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.bukkit.logic.business.extension.ChatBuilder
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
    override fun getCommandKey(): PageKey {
        return PageKey.GAMEEXTENSIONS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as Arena
        if (command == BlockBallCommand.GAMEPROPERTIES_TOGGLE_DAMAGE) {
            arena.meta.customizingMeta.damageEnabled = !arena.meta.customizingMeta.damageEnabled
        } else if (command == BlockBallCommand.GAMEPROPERTIES_TOGGLE_TELEPORTBACK) {
            arena.meta.customizingMeta.backTeleport = !arena.meta.customizingMeta.backTeleport
        } else if (command == BlockBallCommand.GAMEPROPERTIES_TELEPORTBACKDELAY && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.customizingMeta.backTeleportDelay = args[2].toInt()
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
        return ChatBuilder()
                .component("- Damage enabled: " + meta.damageEnabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMEPROPERTIES_TOGGLE_DAMAGE.command)
                .setHoverText("Toggles the dealing damage in the arena.")
                .builder().nextLine()
                .component("- Score teleport back: " + meta.backTeleport).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMEPROPERTIES_TOGGLE_TELEPORTBACK.command)
                .setHoverText("Toggles if players should be teleported back to their game spawnpoint after anyone scores a point.")
                .builder().nextLine()
                .component("- Score teleport back delay: " + arena.meta.customizingMeta.backTeleportDelay).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMEPROPERTIES_TELEPORTBACKDELAY.command)
                .setHoverText("Delay after the players get teleported back to their game spawnpoint.")
                .builder().nextLine()
    }
}