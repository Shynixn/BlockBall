package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
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
class BossbarPage : Page(BossbarPage.ID, EffectsSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 9
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.BOSSBAR
    }


    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player?, command: BlockBallCommand?, cache: Array<Any>?, args: Array<out String>?): CommandResult {
        val arena = cache!![0] as BukkitArena
        val bossbar = arena.meta.bossBarMeta
        cache[5] = bossbar.flags.map { p -> p.name }
        if (command == BlockBallCommand.BOSSBAR_OPEN && args!!.size == 3) {
            bossbar.style = BossBarMeta.Style.values()[args[2].toInt()]
        } else if (command == BlockBallCommand.BOSSBAR_CALLBACKCOLORS && args!!.size == 3) {
            bossbar.color = BossBarMeta.Color.values()[args[2].toInt()]
        } else if (command == BlockBallCommand.BOSSBAR_CALLBACKFLAGS && args!!.size == 3) {
            bossbar.flags.clear()
            bossbar.flags.add(BossBarMeta.Flag.values()[args[2].toInt()])
        } else if (command == BlockBallCommand.BOSSBAR_MESSAGE) {
            bossbar.message = this.mergeArgs(2, args)
        } else if (command == BlockBallCommand.BOSSBAR_TOGGLE) {
            bossbar.enabled = !bossbar.enabled
        } else if (command == BlockBallCommand.BOSSBAR_PERCENT) {
            val result = args!![2].toDoubleOrNull()
            if (result != null) {
                bossbar.percentage = result
            }
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any>?): ChatBuilder {
        val arena = cache!![0] as BukkitArena
        val bossbar = arena.meta.bossBarMeta
        if (bossbar.flags.isEmpty())
            bossbar.flags.add(BossBarMeta.Flag.NONE)
        return ChatBuilder()
                .component("- Message: " + bossbar.message).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BOSSBAR_MESSAGE.command)
                .setHoverText("Edit the message of the bossbar.")
                .builder().nextLine()
                .component("- Enabled: " + bossbar.enabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.BOSSBAR_TOGGLE.command)
                .setHoverText("Toggle the bossbar.")
                .builder().nextLine()
                .component("- Percentage: " + bossbar.percentage).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.BOSSBAR_PERCENT.command)
                .setHoverText("Edit the amount of percentage the bossbar is filled.")
                .builder().nextLine()
                .component("- Color: " + bossbar.color).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_BOSSBARCOLORS.command)
                .setHoverText("Opens the selectionbox for colors.")
                .builder().nextLine()
                .component("- Style: " + bossbar.style).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_BOSSBARSTYLES.command)
                .setHoverText("Opens the selectionbox for styles.")
                .builder().nextLine()
                .component("- Flags: " + bossbar.flags[0]).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_BOSSBARFLAGS.command)
                .setHoverText("Opens the selectionbox for flags.")
                .builder().nextLine()
    }
}