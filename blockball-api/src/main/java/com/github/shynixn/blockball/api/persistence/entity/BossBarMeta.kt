package com.github.shynixn.blockball.api.persistence.entity

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
interface BossBarMeta : PersistenceAble {

    /** Is bossbar visible. */
    var enabled: Boolean

    /** Displaying message. */
    var message: String

    /** Percentage filled in the bossbar. */
    var percentage: Double

    /** Style of the bossbar. */
    var style: Style

    /** Color of the bossbar. */
    var color: Color

    /** Flags of the bossbar. */
    val flags: MutableList<Flag>

    /** Style of the bossbar. */
    enum class Style {
        /**
         * Segmented Style 6.
         */
        SEGMENTED_6,

        /**
         * Segmented Style 10.
         */
        SEGMENTED_10,

        /**
         * Segmented Style 12.
         */
        SEGMENTED_12,

        /**
         * Segmented Style 20.
         */
        SEGMENTED_20,

        /**
         * Solid Style 6.
         */
        SOLID;
    }

    /** Color of the bossbar. */
    enum class Color {
        /**
         * Color Pink.
         */
        PINK,

        /**
         * Color Blue.
         */
        BLUE,

        /**
         * Color Red.
         */
        RED,

        /**
         * Color Green.
         */
        GREEN,

        /**
         * Color Yellow.
         */
        YELLOW,

        /**
         * Color Purple.
         */
        PURPLE,

        /**
         * Color White.
         */
        WHITE;
    }

    /** Flag of the bossbar. */
    enum class Flag {
        /**
         * No Flag.
         */
        NONE,

        /**
         * Flag creating fog.
         */
        CREATE_FOG,

        /**
         * Flag darken the sky.
         */
        DARKEN_SKY,

        /**
         * Flag playing boss music.
         */
        PLAY_BOSS_MUSIC
    }
}