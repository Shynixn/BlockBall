package com.github.shynixn.blockball.api.business.enumeration

import com.github.shynixn.mcutils.common.ChatColor

enum class MenuClickableItem(
        /*** Returns the displayed text of the component.*/
        val text: String,
        /*** Returns the displayed color of the component.*/
        val color: ChatColor
) {
    /**
     * Clickable Edit.
     */
    EDIT(" [edit..]", ChatColor.GREEN),
    /**
     * Clickable copy armor.
     */
    COPY_ARMOR(" [copy armor..]", ChatColor.GOLD),
    /**
     *  Clickable copy inventory.
     */
    COPY_INVENTORY(" [copy inventory..]", ChatColor.GOLD),
    /**
     * Clickable page.
     */
    PAGE(" [page..]", ChatColor.YELLOW),
    /**
     * Clickable preview.
     */
    PREVIEW(" [preview..]", ChatColor.GRAY),
    /**
     *  Clickable add.
     */
    ADD(" [add..]", ChatColor.BLUE),
    /**
     * Clickable delete.
     */
    DELETE(" [delete..]", ChatColor.DARK_RED),
    /**
     * Clickable select.
     */
    SELECT(" [select..]", ChatColor.AQUA),
    /**
     * Clickable selection.
     */
    SELECTION(" [selection..]", ChatColor.GOLD),
    /**
     * Clickable location.
     */
    LOCATION(" [location..]", ChatColor.BLUE),
    /**
     * Clickable invalid.
     */
    INVALID(" [page..]", ChatColor.BLACK),
    /**
     * Clickable toggle.
     */
    TOGGLE(" [toggle..]", ChatColor.LIGHT_PURPLE);
}
