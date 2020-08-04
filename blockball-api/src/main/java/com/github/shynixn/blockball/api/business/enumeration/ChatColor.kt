@file:Suppress("unused")

package com.github.shynixn.blockball.api.business.enumeration

import java.util.regex.Pattern

/**
 * Cross Platform ChatColors.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
enum class ChatColor(
    /**
     * Unique code.
     */
    val code: Char,
    /**
     * Unique int code.
     */
    val internalCode: Int,
    /**
     * Is color being used for formatting.
     */
    val isFormatting: Boolean = false,
    /**
     * Internal string description of the color.
     */
    private val internalString: String = String(charArrayOf('§', code))
) {
    /**
     * Black.
     */
    BLACK('0', 0),

    /**
     * Dark_Blue.
     */
    DARK_BLUE('1', 1),

    /**
     * Dark_Green.
     */
    DARK_GREEN('2', 2),

    /**
     * Dark_Aqua.
     */
    DARK_AQUA('3', 3),

    /**
     * Dark_Red.
     */
    DARK_RED('4', 4),

    /**
     * Dark_Purple.
     */
    DARK_PURPLE('5', 5),

    /**
     * Gold.
     */
    GOLD('6', 6),

    /**
     * Gray.
     */
    GRAY('7', 7),

    /**
     * Dark Gray.
     */
    DARK_GRAY('8', 8),

    /**
     * Blue.
     */
    BLUE('9', 9),

    /**
     * Green.
     */
    GREEN('a', 10),

    /**
     * Aqua.
     */
    AQUA('b', 11),

    /**
     * Red.
     */
    RED('c', 12),

    /**
     * Light_Purple.
     */
    LIGHT_PURPLE('d', 13),

    /**
     * Yellow.
     */
    YELLOW('e', 14),

    /**
     * White.
     */
    WHITE('f', 15),

    /**
     * Magic.
     */
    MAGIC('k', 16, true),

    /**
     * Bold.
     */
    BOLD('l', 17, true),

    /**
     * Strikethrough.
     */
    STRIKETHROUGH('m', 18, true),

    /**
     * Underline.
     */
    UNDERLINE('n', 19, true),

    /**
     * Italic.
     */
    ITALIC('o', 20, true),

    /**
     * Reset.
     */
    RESET('r', 21);

    /**
     * Returns the string code.
     */
    override fun toString(): String {
        return this.internalString
    }

    companion object {
        private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§'.toString() + "[0-9A-FK-OR]")
        private val HEX_PATTERN = Pattern.compile("(#\\w{6})")
        private const val COLOR_CHAR = '\u00A7'

        /**
         * Strips the chat colors from the string.
         */
        fun stripChatColors(text: String): String {
            return STRIP_COLOR_PATTERN.matcher(text).replaceAll("")
        }

        /**
         * Translates the given [colorCodeChar] in the given [text] to the color code chars of minecraft.
         */
        fun translateChatColorCodes(colorCodeChar: Char, text: String): String {
            val letters = text.toMutableList()

            for (i in 0 until letters.size - 1) {
                if (letters[i] == colorCodeChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(letters[i + 1]) > -1) {
                    letters[i] = 167.toChar()
                    letters[i + 1] = (letters[i + 1]).toLowerCase()
                }
            }

            val standardColorCodes = String(letters.toCharArray())
            val matcher = HEX_PATTERN.matcher(standardColorCodes)
            val buffer = StringBuffer()

            while (matcher.find()) {
                val group = matcher.group(1)
                val stringBuilder = StringBuilder(COLOR_CHAR + "x")

                for (c in group.substring(1).toCharArray()) {
                    stringBuilder.append(COLOR_CHAR)
                    stringBuilder.append(c)
                }

                matcher.appendReplacement(buffer, stringBuilder.toString())
            }

            return matcher.appendTail(buffer).toString()
        }
    }
}