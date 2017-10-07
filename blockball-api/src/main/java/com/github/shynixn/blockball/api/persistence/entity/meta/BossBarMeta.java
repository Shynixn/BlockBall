package com.github.shynixn.blockball.api.persistence.entity.meta;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public interface BossBarMeta extends Persistenceable<BossBarMeta> {
    /**
     * Returns the percentage of the bossbar.
     *
     * @return percentage
     */
    double getPercentage();

    /**
     * Sets the percentage of the bossbar.
     *
     * @param percentage percentage
     */
    void setPercentage(double percentage);

    /**
     * Returns the style of the bossbar.
     *
     * @return style
     */
    BossBarMeta getStyle();

    /**
     * Sets the style of the bossbar.
     *
     * @param style style
     */
    BossBarMeta setStyle(Style style);

    /**
     * Returns the color of the bossbar.
     *
     * @return color
     */
    Color getColor();

    /**
     * Sets the color of the bossbar.
     *
     * @param color color
     */
    BossBarMeta setColor(Color color);

    /**
     * Returns the flags of the bossbar.
     *
     * @return flags
     */
    Set<Flag> getFlags();

    /**
     * Adds a flag to list
     *
     * @param flag flag
     */
    void addFlag(Flag flag);

    /**
     * Removes flag from the list
     *
     * @param flag flag
     */
    void removeFlag(Flag flag);

    /**
     * Returns if the bossbar should be visible.
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Sets if the bossbar should be visible.
     *
     * @param enabled enabled
     */
    BossBarMeta setEnabled(boolean enabled);

    /**
     * Returns the message of the bossbar
     *
     * @return message
     */
    Optional<String> getMessage();

    /**
     * Sets the message of the bossbar
     *
     * @param message message
     */
    BossBarMeta setMessage(String message);

    /**
     * Bossbar styles.
     * <p>
     * Version 1.1
     * <p>
     * MIT License
     * <p>
     * Copyright (c) 2017 by Shynixn
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
    enum Style {
        SEGMENTED_6,
        SEGMENETED_10,
        SEGMENTED_12,
        SEGEMENTED_20,
        SOLID;

        /**
         * Returns the names of all styles
         *
         * @return names
         */
        public String[] getNames() {
            final String[] names = new String[Style.values().length];
            for (int i = 0; i < Style.values().length; i++) {
                names[i] = Style.values()[i].name().toUpperCase();
            }
            return names;
        }

        /**
         * Returns a style of the given name
         *
         * @param name name
         * @return style
         */
        public static Optional<Style> getFromName(String name) {
            for (final Style style : Style.values()) {
                if (style.name().equalsIgnoreCase(name))
                    return Optional.of(style);
            }
            return Optional.empty();
        }
    }

    /**
     * Bossbar colors.
     * <p>
     * Version 1.1
     * <p>
     * MIT License
     * <p>
     * Copyright (c) 2017 by Shynixn
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
    enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;

        /**
         * Returns the names of all colors.
         *
         * @return names
         */
        public String[] getNames() {
            final String[] names = new String[Color.values().length];
            for (int i = 0; i < Color.values().length; i++) {
                names[i] = Color.values()[i].name().toUpperCase();
            }
            return names;
        }

        /**
         * Returns a color from the given name.
         *
         * @param name name
         * @return color
         */
        public static Optional<Color> getFromName(String name) {
            for (final Color color : Color.values()) {
                if (color.name().equalsIgnoreCase(name))
                    return Optional.of(color);
            }
            return Optional.empty();
        }
    }

    /**
     * Bossbar flags.
     * <p>
     * Version 1.1
     * <p>
     * MIT License
     * <p>
     * Copyright (c) 2017 by Shynixn
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
    enum Flag {
        NONE,
        CREATE_FOG,
        DARKEN_SKY,
        PLAY_BOSS_MUSIC;

        /**
         * Returns the names of all flags.
         *
         * @return names
         */
        public String[] getNames() {
            final String[] names = new String[Flag.values().length];
            for (int i = 0; i < Flag.values().length; i++) {
                names[i] = Flag.values()[i].name().toUpperCase();
            }
            return names;
        }

        /**
         * Returns a flag from the given name.
         *
         * @param name name
         * @return flag
         */
        public static Optional<Flag> getFromName(String name) {
            for (final Flag flag : Flag.values()) {
                if (flag.name().equalsIgnoreCase(name))
                    return Optional.of(flag);
            }
            return Optional.empty();
        }
    }
}
