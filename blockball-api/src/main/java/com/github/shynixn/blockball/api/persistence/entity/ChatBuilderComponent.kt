package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.ChatClickAction
import com.github.shynixn.mcutils.common.ChatColor

interface ChatBuilderComponent {
    /**
     * Gets the root builder of the component.
     */
    fun builder(): ChatBuilder

    /**
     * Sets the click action.
     */
    fun setClickAction(clickAction: ChatClickAction, payload: String): ChatBuilderComponent

    /**
     * Sets the hover text.
     */
    fun setHoverText(text: String): ChatBuilderComponent

    /**
     * Sets the color of the text.
     */
    fun setColor(color: ChatColor): ChatBuilderComponent
}
