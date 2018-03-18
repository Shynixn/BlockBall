package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.display.HologramMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.helper.toSingleLine
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.display.HologramBuilder
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
        val arena = cache[0] as BukkitArena
        val holograms = arena.meta.hologramMetas
        if (command == BlockBallCommand.HOLOGRAM_OPEN) {
            cache[5] = null
        }
        if (command == BlockBallCommand.HOLOGRAM_CREATE) {
            val builder = HologramBuilder()
            builder.position = player.location.toPosition()
            holograms.add(builder)
            cache[5] = builder
        }
        if (command == BlockBallCommand.HOLOGRAM_CALLBACK && args.size >= 3) {
            val range = args[2].toInt()
            if (range >= 0 && range < holograms.size) {
                cache[5] = holograms[range]
            }
        }
        if (command == BlockBallCommand.HOLOGRAM_DELETE) {
            holograms.remove(cache[5])
            cache[5] = null
        }
        if (command == BlockBallCommand.HOLOGRAM_LOCATION) {
            val hologram = cache[5] as HologramMeta
            hologram.position = player.location.toPosition()
        }
        cache[2] = holograms.map { p -> printLocation(p.position!!) }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val arena = cache[0] as BukkitArena
        val selectedHologram = cache[5]
        var selectedHologramText = "none"
        var selectedHologramHover = "none"
        val hologramListText = arena.meta.hologramMetas.map { p -> printLocation(p.position!!) }.toSingleLine()
        if (selectedHologram != null) {
            selectedHologramText = printLocation((selectedHologram as HologramMeta).position!!)
            selectedHologramHover = selectedHologram.lines.toSingleLine()
            cache[2] = selectedHologram.lines
        }
        val holograms = arena.meta.hologramMetas
        val builder = ChatBuilder()
                .component("- Holograms: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(hologramListText)
                .builder().component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_HOLOGRAMS.command)
                .setHoverText("Opens the selectionbox for existing holograms.")
                .builder().component(" [add by location..]").setColor(ClickableComponent.ADD.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.HOLOGRAM_CREATE.command)
                .setHoverText("Creates a new hologram at your current location and select it.")
                .builder().nextLine()

        if (selectedHologram != null) {
            builder.component("- Selected hologram: " + selectedHologramText).builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                    .setHoverText(selectedHologramHover).builder()
                    .component(ClickableComponent.DELETE.text).setColor(ClickableComponent.DELETE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.HOLOGRAM_DELETE.command)
                    .setHoverText("Deletes the selected hologram.")
                    .builder().nextLine()
                    .component("- Location: " + selectedHologramText).builder()
                    .component(" [location..]").setColor(ChatColor.BLUE)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.HOLOGRAM_LOCATION.command)
                    .setHoverText("Sets the location of the hologram to your current location.")
                    .builder().nextLine()
                    .component("- Lines:").builder()
                    .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_HOLOGRAM.command)
                    .setHoverText("Configure the lines of the hologram.")
                    .builder().nextLine()

        }

        return builder
    }
}