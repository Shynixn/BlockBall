package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.HologramMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.HologramMetaEntity
import com.google.inject.Inject

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
class HologramPage @Inject constructor(private val proxyService: ProxyService) : Page(HologramPage.ID, EffectsSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 17
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.HOLOGRAM
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        val holograms = arena.meta.hologramMetas
        if (command == MenuCommand.HOLOGRAM_OPEN) {
            cache[5] = null
        }
        if (command == MenuCommand.HOLOGRAM_CREATE) {
            val builder = HologramMetaEntity()
            builder.position = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
            holograms.add(builder)
            cache[5] = builder
        }
        if (command == MenuCommand.HOLOGRAM_CALLBACK && args.size >= 3) {
            val range = args[2].toInt()
            if (range >= 0 && range < holograms.size) {
                cache[5] = holograms[range]
            }
        }
        if (command == MenuCommand.HOLOGRAM_DELETE) {
            holograms.remove(cache[5])
            cache[5] = null
        }
        if (command == MenuCommand.HOLOGRAM_LOCATION) {
            val hologram = cache[5] as HologramMeta
            hologram.position = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        }
        cache[2] = holograms.map { p -> p.position!!.toString() }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val arena = cache[0] as Arena
        val selectedHologram = cache[5]
        var selectedHologramText = "none"
        var selectedHologramHover = "none"
        val hologramListText = arena.meta.hologramMetas.map { p -> p.position!!.toString() }.toSingleLine()
        if (selectedHologram != null) {
            selectedHologramText = (selectedHologram as HologramMeta).position!!.toString()
            selectedHologramHover = selectedHologram.lines.toSingleLine()
            cache[2] = selectedHologram.lines
        }
        val builder = ChatBuilderEntity()
            .component("- Holograms: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(hologramListText)
            .builder().component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_HOLOGRAMS.command)
            .setHoverText("Opens the selectionbox for existing holograms.")
            .builder().component(" [add by location..]").setColor(MenuClickableItem.ADD.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.HOLOGRAM_CREATE.command)
            .setHoverText("Creates a new hologram at your current location and select it.")
            .builder().nextLine()

        if (selectedHologram != null) {
            builder.component("- Selected hologram: $selectedHologramText").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(selectedHologramHover).builder()
                .component(MenuClickableItem.DELETE.text).setColor(MenuClickableItem.DELETE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.HOLOGRAM_DELETE.command)
                .setHoverText("Deletes the selected hologram.")
                .builder().nextLine()
                .component("- Location: $selectedHologramText").builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.HOLOGRAM_LOCATION.command)
                .setHoverText("Sets the location of the hologram to your current location.")
                .builder().nextLine()
                .component("- Lines:").builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_HOLOGRAM.command)
                .setHoverText("Configure the lines of the hologram.")
                .builder().nextLine()

        }

        return builder
    }
}