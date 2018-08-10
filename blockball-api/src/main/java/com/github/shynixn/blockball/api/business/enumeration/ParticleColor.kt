package com.github.shynixn.blockball.api.business.enumeration

/**
 * Created by Shynixn 2018.
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
enum class ParticleColor(
        /**
         * Red color value.
         */
        val red: Int,
        /**
         * Green color value.
         */
        val green: Int,
        /**
         * Blue color value.
         */
        val blue: Int) {
    /**
     * Black.
     */
    BLACK(0, 0, 0),
    /**
     * Dark blue.
     */
    DARK_BLUE(0, 0, 170),
    /**
     * Dark green.
     */
    DARK_GREEN(0, 170, 0),
    /**
     * Dark aqua.
     */
    DARK_AQUA(0, 170, 170),
    /**
     * Dark red.
     */
    DARK_RED(170, 0, 0),
    /**
     * Dark purple.
     */
    DARK_PURPLE(170, 0, 170),
    /**
     * Gold.
     */
    GOLD(255, 170, 0),
    /**
     * Gray.
     */
    GRAY(170, 170, 170),
    /**
     * Dark blue.
     */
    DARK_GRAY(85, 85, 85),
    /**
     * Blue.
     */
    BLUE(85, 85, 255),
    /**
     * Green.
     */
    GREEN(85, 255, 85),
    /**
     * Aqua.
     */
    AQUA(85, 255, 255),
    /**
     * Red.
     */
    RED(255, 85, 85),
    /**
     * Light Purple.
     */
    LIGHT_PURPLE(255, 85, 255),
    /**
     * Yellow.
     */
    YELLOW(255, 255, 85),
    /**
     * White.
     */
    WHITE(255, 255, 255);
}