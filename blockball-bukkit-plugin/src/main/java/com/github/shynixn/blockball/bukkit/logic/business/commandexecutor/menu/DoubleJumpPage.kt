package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.google.inject.Inject
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
class DoubleJumpPage : Page(DoubleJumpPage.ID, OpenPage.ID) {
    companion object {
        /** Id of the page. */
        const val ID = 18
    }

    @Inject
    private var arenaRepository: ArenaRepository? = null

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.DOUBLEJUMP
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as BukkitArena
        if (command == BlockBallCommand.DOUBLEJUMP_TOGGLE) {
            arena.meta.doubleJumpMeta.enabled = !arena.meta.doubleJumpMeta.enabled
        } else if (command == BlockBallCommand.DOUBLEJUMP_COOLDOWN && args[2].toIntOrNull() != null) {
            arena.meta.doubleJumpMeta.cooldown = args[2].toInt()
        } else if (command == BlockBallCommand.DOUBLEJUMP_VERTICAL_STRENGTH && args[2].toDoubleOrNull() != null) {
            arena.meta.doubleJumpMeta.verticalStrength = args[2].toDouble()
        } else if (command == BlockBallCommand.DOUBLEJUMP_HORIZONTAL_STRENGTH && args[2].toDoubleOrNull() != null) {
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
        val arena = cache[0] as BukkitArena
        val meta = arena.meta.doubleJumpMeta
        return ChatBuilder()
                .component("- Enabled: " + meta.enabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.DOUBLEJUMP_TOGGLE.command)
                .setHoverText("Toggles the double jump.")
                .builder().nextLine()
                .component("- Cooldown: " + meta.cooldown).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.DOUBLEJUMP_COOLDOWN.command)
                .setHoverText("Changes the cooldown between double jumps.")
                .builder().nextLine()
                .component("- Vertical strength: " + meta.verticalStrength).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.DOUBLEJUMP_VERTICAL_STRENGTH.command)
                .setHoverText("Changes the vertical strength modifier a player is flying.")
                .builder().nextLine()
                .component("- Horizontal strength: " + meta.horizontalStrength).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.DOUBLEJUMP_HORIZONTAL_STRENGTH.command)
                .setHoverText("Changes the horizontal strength modifier a player is flying.")
                .builder().nextLine()
                .component("- Particleeffect:").builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.PARTICLE_DOUBLEJUMP.command)
                .setHoverText("Opens the particleEffect page.")
                .builder().nextLine()
                .component("- Soundeffect:").builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SOUND_DOUBLEJUMP.command)
                .setHoverText("Opens the soundEffect page.").builder()
    }
}