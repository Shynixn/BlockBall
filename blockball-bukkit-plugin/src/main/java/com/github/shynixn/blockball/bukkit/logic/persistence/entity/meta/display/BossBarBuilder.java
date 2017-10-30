package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.display;

import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;

import java.util.*;

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
public class BossBarBuilder extends PersistenceObject<BossBarMeta> implements BossBarMeta {
    private BossBarMeta.Style style = Style.SOLID;
    private BossBarMeta.Color color = Color.PURPLE;
    private final Set<Flag> flags = new HashSet<>();
    private boolean enabled;
    private String message;
    private double percentage = 1.0;

    /**
     * Initialize
     */
    public BossBarBuilder() {
    }

    /**
     * Initialize
     *
     * @param data data
     */
    public BossBarBuilder(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        this.enabled = (boolean) data.get("enabled");
        this.message = (String) data.get("message");
        this.color = Color.getFromName((String) data.get("color")).get();
        this.style = Style.getFromName((String) data.get("style")).get();
        this.percentage = (double) data.get("percentage");
        final List<String> flags = (List<String>) data.get("flags");
        for (final String flag : flags) {
            this.flags.add(Flag.getFromName(flag).get());
        }
    }

    /**
     * Returns the percentage of the bossbar.
     *
     * @return percentage
     */
    @Override
    public double getPercentage() {
        return this.percentage;
    }

    /**
     * Sets the percentage of the bossbar.
     *
     * @param percentage percentage
     */
    @Override
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Returns the style of the bossbar.
     *
     * @return style
     */
    @Override
    public BossBarMeta.Style getStyle() {
        return this.style;
    }

    /**
     * Sets the style of the bossbar.
     *
     * @param style style
     */
    @Override
    public  BossBarMeta setStyle(Style style) {
        if (style == null)
            throw new IllegalArgumentException("Style cannot be null!");
        this.style = style;
        return this;
    }

    /**
     * Returns the color of the bossbar.
     *
     * @return color
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the color of the bossbar.
     *
     * @param color color
     */
    @Override
    public BossBarMeta setColor(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color cannot be null!");
        this.color = color;
        return this;
    }

    /**
     * Returns the flags of the bossbar.
     *
     * @return flags
     */
    @Override
    public Set<Flag> getFlags() {
        return Collections.unmodifiableSet(this.flags);
    }

    /**
     * Adds a flag to list
     *
     * @param flag flag
     */
    @Override
    public void addFlag(Flag flag) {
        if (flag == null)
            throw new IllegalArgumentException("Flag cannot be null!");
        this.flags.add(flag);
    }

    /**
     * Removes flag from the list
     *
     * @param flag flag
     */
    @Override
    public void removeFlag(Flag flag) {
        if (flag == null)
            throw new IllegalArgumentException("Flag cannot be null!");
        if (this.flags.contains(flag)) {
            this.flags.remove(flag);
        }
    }

    /**
     * Returns if the bossbar should be visible.
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets if the bossbar should be visible.
     *
     * @param enabled enabled
     */
    @Override
    public  BossBarMeta setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Returns the message of the bossbar
     *
     * @return message
     */
    @Override
    public Optional<String> getMessage() {
        return Optional.ofNullable(this.message);
    }

    /**
     * Sets the message of the bossbar
     *
     * @param message message
     */
    @Override
    public BossBarMeta setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Serializes this object
     *
     * @return map
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("enabled", this.enabled);
        data.put("message", this.message);
        data.put("percentage", this.percentage);
        data.put("color", this.color.name().toUpperCase());
        data.put("style", this.style.name().toUpperCase());
        final List<String> flags = new ArrayList<>();
        for (final Flag flag : this.flags) {
            flags.add(flag.name().toUpperCase());
        }
        data.put("flag", flags);
        return null;
    }
}
