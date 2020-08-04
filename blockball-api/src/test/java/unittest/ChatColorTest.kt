@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Created by Shynixn 2020.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2020 by Shynixn
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
class ChatColorTest {
    /**
     * Given
     *      a text containing color codes
     * When
     *      ChatColor.stripChatColors is called
     * Then
     *     the color codes should be stripped.
     */
    @Test
    fun stripChatColor_TextContainingColorCodes_ShouldCorrectlyStrip() {
        // Arrange
        val expectedText = "This is a colored text!"
        val text = "This is a §ecolored §atext!"

        // Act
        val actualText = ChatColor.stripChatColors(text)

        // Assert
        Assertions.assertEquals(expectedText, actualText)
    }

    /**
     * Given
     *      a text containing no color codes
     * When
     *      ChatColor.stripChatColors is called
     * Then
     *     the color codes should be stripped.
     */
    @Test
    fun stripChatColor_TextContainingNoColorCodes_ShouldCorrectlyStrip() {
        // Arrange
        val expectedText = "This is a not a colored text!"

        // Act
        val actualText = ChatColor.stripChatColors(expectedText)

        // Assert
        Assertions.assertEquals(expectedText, actualText)
    }

    /**
     * Given
     *      a text containing color codes
     * When
     *      ChatColor.translateChatColorCodes is called
     * Then
     *     the color codes should be translated.
     */
    @Test
    fun translateChatColorCodes_TextContainingColorCodes_ShouldCorrectlyTranslate() {
        // Arrange
        val expectedText = "This §eis a §4colored §6text!"
        val text = "This §eis a &4colored &6text!"

        // Act
        val actualText = ChatColor.translateChatColorCodes('&', text)

        // Assert
        Assertions.assertEquals(expectedText, actualText)
    }

    /**
     * Given
     *      a text containing no color codes
     * When
     *      ChatColor.translateChatColorCodes is called
     * Then
     *     the color codes should be translated.
     */
    @Test
    fun translateChatColorCodes_TextContainingNoColorCodes_ShouldCorrectlyTranslate() {
        // Arrange
        val expectedText = "This is a text!"

        // Act
        val actualText = ChatColor.translateChatColorCodes('&', expectedText)

        // Assert
        Assertions.assertEquals(expectedText, actualText)
    }

    /**
     * Given
     *      a text containing hex color codes
     * When
     *      ChatColor.translateChatColorCodes is called
     * Then
     *     the color codes should be translated.
     */
    @Test
    fun translateChatColorCodes_TextContainingHexColorCodes_ShouldCorrectlyTranslate() {
        // Arrange
        val expectedText = "This §x§1§d§d§e§c§eis a §x§3§6§c§f§c§2Hex colored §x§a§6§f§7§f§0text!"
        val text = "This #1ddeceis a #36cfc2Hex colored #a6f7f0text!"

        // Act
        val actualText = ChatColor.translateChatColorCodes('&', text)

        // Assert
        Assertions.assertEquals(expectedText, actualText)
    }
}