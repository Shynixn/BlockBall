package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.compatibility.EffectMeta;
import com.github.shynixn.blockball.api.compatibility.EffectingType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class EffectData implements EffectMeta, ConfigurationSerializable {

    private EffectingType effectingType = EffectingType.EVERYONE;

    /**
     * Initializes the effectData.
     * @param data data
     */
    public EffectData(Map<String, Object> data) {
        if(data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        this.effectingType = EffectingType.valueOf((String) data.get("effecting"));
    }

    /**
     * Initializes the effectData.
     */
    public EffectData() {
    }

    /**
     * Returns the effecting type.
     *
     * @return type
     */
    @Override
    public EffectingType getEffectingType() {
        return this.effectingType;
    }

    /**
     * Sets the effecting type.
     *
     * @param type type
     * @param <T> type of effect
     * @return effectType
     */
    @Override
    public <T extends EffectMeta> T setEffectingType(EffectingType type) {
        this.effectingType = type;
        return (T) this;
    }

    /**
     * Serializes the given data.
     * @return data
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("effecting", this.getEffectingType().name().toUpperCase());
        return data;
    }
}
