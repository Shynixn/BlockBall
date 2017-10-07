package com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties;

import com.github.shynixn.blockball.api.persistence.entity.ScoreboardMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.lib.YamlSerializer;

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
public class ScoreboardProperties extends PersistenceObject<ScoreboardMeta> implements ScoreboardMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "enabled")
    private boolean enabled = true;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "title")
    private String title;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "lines")
    private String[] lines;

    /**
     * Initializes new scoreboard properties
     *
     * @param title title
     * @param lines lines
     */
    public ScoreboardProperties(String title, String[] lines) {
        if (title == null)
            throw new IllegalArgumentException("Title cannot be null!");
        if (lines == null)
            throw new IllegalArgumentException("Lines cannot be null!");
        this.title = title;
        this.lines = lines.clone();
    }

    /**
     * Sets the title of the scoreboard.
     *
     * @param title title
     */
    @Override
    public void setTitle(String title) {
        if (title == null)
            throw new IllegalArgumentException("Title cannot be null!");
        this.title = title;
    }

    /**
     * Returns the title of the scoreboard.
     *
     * @return title
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Enables or disables the scoreboard.
     *
     * @param enabled scoreboard
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns if the scoreboard is enabled.
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the lines of the scoreboard.
     *
     * @param scoreboardLines scoreboardLines
     */
    @Override
    public void setScoreboardLines(String[] scoreboardLines) {
        if (scoreboardLines == null)
            throw new IllegalArgumentException("Lines cannot be null!");
        this.lines = scoreboardLines.clone();
    }

    /**
     * Returns the lines of the scoreboard.
     *
     * @return lines
     */
    @Override
    public String[] getScoreboardLines() {
        return this.lines.clone();
    }
}
