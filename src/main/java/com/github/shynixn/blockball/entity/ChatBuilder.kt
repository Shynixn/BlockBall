package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.translateChatColors

class ChatBuilder {
    private val components = ArrayList<Any>()

    /**
     * Sets the color of the text.
     */
    fun setColor(color: ChatColor): ChatBuilder {
        this.components.add(color)
        return this
    }

    /**
     * Creates a new component with the given [text].
     */
    fun component(text: String): ChatBuilderComponent {
        val component = ChatBuilderComponent(this, text)
        this.components.add(component)
        return component
    }

    /**
     * Appends a line break.
     */
    fun nextLine(): ChatBuilder {
        this.components.add("\n")
        return this
    }

    /**
     * Appends text to the builder.
     */
    fun text(text: String): ChatBuilder {
        this.components.add(text)
        return this
    }

    /**
     * ToString.
     */
    override fun toString(): String {
        val finalMessage = StringBuilder()
        val cache = StringBuilder()
        finalMessage.append("{\"text\": \"\"")
        finalMessage.append(", \"extra\" : [")
        var firstExtra = false
        for (component in this.components) {
            if (component !is ChatColor && firstExtra) {
                finalMessage.append(", ")
            }
            when (component) {
                is ChatColor -> cache.append(component)
                is String -> {
                    finalMessage.append("{\"text\": \"")
                    finalMessage.append((cache.toString() + component).translateChatColors())
                    finalMessage.append("\"}")
                    cache.setLength(0)
                    firstExtra = true
                }
                else -> {
                    finalMessage.append(component)
                    firstExtra = true
                }
            }
        }

        finalMessage.append("]}")

        return finalMessage.toString()
    }
}
