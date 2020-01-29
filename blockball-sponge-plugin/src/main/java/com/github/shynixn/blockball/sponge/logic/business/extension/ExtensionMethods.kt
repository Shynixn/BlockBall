package com.github.shynixn.blockball.sponge.logic.business.extension

import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import org.spongepowered.api.command.source.ConsoleSource
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

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

/**
 * Sends the [message] to the console source.
 */
fun ConsoleSource.sendMessage(message: String) {
    this.sendMessage(message.toText())
}

/**
 * Converts the given string to a text.
 */
fun String.toText(): Text {
    return TextSerializers.formattingCode('§').deserialize(this.translateChatColors())
}

/**
 * Gets/Sets allow to flight.
 */
var Player.allowFlight: Boolean
    get() {
        return this.get(Keys.CAN_FLY).orElse(false)
    }
    set(value) {
        this.offer(Keys.CAN_FLY, value)
    }

/**
 * Converts the given text to a string.
 */
fun Text.toTextString(): String {
    return TextSerializers.formattingCode('§').serialize(this)
}

/**
 * Sends a text message to the player.
 */
fun Player.sendMessage(text: String) {
    this.sendMessage(text.toText())
}