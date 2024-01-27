package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.mcutils.common.ChatColor

interface ChatBuilder {
    /**
     * Creates a new component with the given [text].
     */
    fun component(text: String): ChatBuilderComponent

    /**
     * Appends a line break.
     */
    fun nextLine(): ChatBuilder

    /**
     * Sets the color of the text.
     */
    fun setColor(color: ChatColor): ChatBuilder

    /**
     * Appends text to the builder.
     */
    fun text(text: String): ChatBuilder
}
