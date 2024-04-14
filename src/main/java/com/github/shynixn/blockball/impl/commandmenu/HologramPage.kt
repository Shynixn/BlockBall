package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.entity.HologramMeta
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.impl.extension.toPosition
import com.github.shynixn.mcutils.common.ChatColor
import com.google.inject.Inject
import org.bukkit.entity.Player

class HologramPage @Inject constructor() : Page(HologramPage.ID, EffectsSettingsPage.ID) {
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
        require(player is Player)
        val arena = cache[0] as Arena
        val holograms = arena.meta.hologramMetas
        if (command == MenuCommand.HOLOGRAM_OPEN) {
            cache[5] = null
        }
        if (command == MenuCommand.HOLOGRAM_CREATE) {
            val builder = HologramMeta()
            builder.position = player.location.toPosition()
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
            hologram.position = player.location.toPosition()
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
        val builder = ChatBuilder()
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
