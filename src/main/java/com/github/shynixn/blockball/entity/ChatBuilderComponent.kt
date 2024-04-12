package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.mcutils.common.ChatColor

class ChatBuilderComponent(private val builder: ChatBuilder, payloadtext: String)  {
    val text = StringBuilder()
    var clickAction: ChatClickAction? = null
    var clickActionData: String? = null
    var hoverActionData: ChatBuilderComponent? = null
    var color: ChatColor? = null

    init {
        text.append(payloadtext)
    }

    /**
     * Gets the root builder of the component.
     */
    fun builder(): ChatBuilder {
        return builder
    }

    /**
     * Sets the click action.
     */
    fun setClickAction(clickAction: ChatClickAction, payload: String): ChatBuilderComponent {
        this.clickAction = clickAction
        this.clickActionData = payload
        return this
    }

    /**
     * Sets the hover text.
     */
    fun setHoverText(text: String): ChatBuilderComponent {
        this.hoverActionData = ChatBuilderComponent(this.builder, text)
        return this
    }

    /**
     * Sets the color of the text.
     */
    fun setColor(color: ChatColor): ChatBuilderComponent {
        this.color = color
        return this
    }
}
