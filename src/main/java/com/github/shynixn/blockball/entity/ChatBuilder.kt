package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.chat.*

class ChatBuilder {
    private val components = ArrayList<ChatBuilderComponent>()

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
        this.components.add(ChatBuilderComponent(this, "\n"))
        return this
    }

    /**
     * Appends text to the builder.
     */
    fun text(text: String): ChatBuilder {
        this.components.add(ChatBuilderComponent(this, text))
        return this
    }

    /**
     * Compatibility layer. Will be removed in a future update.
     */
    fun convertToTextComponent(): TextComponent {
        val rootComponent = TextComponent()
        rootComponent.text = ""

        for (component in this.components) {
            val subComponent = TextComponent().also {
                it.text = component.text.toString()
                it.color = component.color
            }
            rootComponent.components.add(subComponent)

            if (component.clickAction != null) {
                subComponent.clickEvent =
                    ClickEvent(ClickEventType.valueOf(component.clickAction!!.name), component.clickActionData!!)
            }

            if (component.hoverActionData != null) {
                subComponent.hoverEvent = HoverEvent(HoverEventType.SHOW_TEXT, TextComponent().also {
                    it.text = component.hoverActionData!!.text.toString()
                    it.color = component.hoverActionData!!.color
                })
            }
        }

        return rootComponent
    }
}
