package com.github.shynixn.blockball.api.persistence.entity.meta.display;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

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
public interface ScoreboardMeta extends Persistenceable<ScoreboardMeta> {
    /**
     * Sets the title of the scoreboard.
     *
     * @param title title
     */
    void setTitle(String title);

    /**
     * Returns the title of the scoreboard.
     *
     * @return title
     */
    String getTitle();

    /**
     * Enables or disables the scoreboard.
     *
     * @param enabled scoreboard
     */
    void setEnabled(boolean enabled);

    /**
     * Returns if the scoreboard is enabled.
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Sets the lines of the scoreboard.
     *
     * @param scoreboardLines scoreboardLines
     */
    void setScoreboardLines(String[] scoreboardLines);

    /**
     * Returns the lines of the scoreboard.
     *
     * @return lines
     */
    String[] getScoreboardLines();
}
