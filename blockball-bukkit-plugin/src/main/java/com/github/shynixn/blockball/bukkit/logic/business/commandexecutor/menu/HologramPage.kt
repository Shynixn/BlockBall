package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.display.HologramMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toSingleLine
import org.bukkit.ChatColor
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
class HologramPage : Page(HologramPage.ID, EffectsSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 17
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.HOLOGRAM
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        if(command == BlockBallCommand.HOLOGRAM_OPEN)
        {
            cache[5] = null;
        }
        val arena = cache!![0] as BukkitArena
        val scoreboard = arena.meta.hologramMetas
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val arena = cache!![0] as BukkitArena
        val selectedHologram = cache[5];
        var selectedHologramText = "none"
        var selectedHologramHover = "none"
        if(selectedHologram != null)
        {
            selectedHologramText = printLocation((selectedHologram as HologramMeta).position!!);
            selectedHologramHover = selectedHologram.lines.toSingleLine()
        }
        val holograms = arena.meta.hologramMetas
        return ChatBuilder()
                .component("- HOLOGRAMS: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText("HOWVER")
                .builder()
                .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_ADDTEAMRED.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the red team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
                .component("- Selected hologram: " + selectedHologramText).builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(selectedHologramHover)
                .builder()
                .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_ADDTEAMRED.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the red team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
    }
}