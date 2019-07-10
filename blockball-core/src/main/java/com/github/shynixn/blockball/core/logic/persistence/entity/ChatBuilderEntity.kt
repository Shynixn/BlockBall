package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilderComponent
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import java.util.ArrayList

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class ChatBuilderEntity : ChatBuilder {
    private val components = ArrayList<Any>()

    /**
     * Sets the color of the text.
     */
    override fun setColor(color: ChatColor): ChatBuilder {
        this.components.add(color)
        return this
    }

    /**
     * Creates a new component with the given [text].
     */
    override fun component(text: String): ChatBuilderComponent {
        val component = ChatBuilderComponentEntity(this, text)
        this.components.add(component)
        return component
    }

    /**
     * Appends a line break.
     */
    override fun nextLine(): ChatBuilder {
        this.components.add("\n")
        return this
    }

    /**
     * Appends text to the builder.
     */
    override fun text(text: String): ChatBuilder {
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