package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.ball.api.bukkit.persistence.controller.BukkitBounceController;
import com.github.shynixn.ball.api.bukkit.persistence.entity.BukkitParticleEffectMeta;
import com.github.shynixn.ball.api.bukkit.persistence.entity.BukkitSoundEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder;

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
public class BallData extends com.github.shynixn.ball.bukkit.core.logic.persistence.entity.BallData implements BallMeta<BukkitParticleEffectMeta, BukkitSoundEffectMeta, BukkitBounceController> {
    private long id;
    private IPosition spawnpoint;

    /**
     * Deserializes a ballData.
     *
     * @param data data
     */
    public BallData(Map<String, Object> data) throws Exception {
        super(data);
        this.spawnpoint = new LocationBuilder((Map<String, Object>) data.get("spawnpoint"));
    }

    /**
     * Initializes the ball data with a new skin.
     *
     * @param skin skin
     */
    public BallData(String skin) {
        super(skin);
    }

    /**
     * Sets the spawnpoint of the ball.
     *
     * @param position position
     */
    @Override
    public void setSpawnpoint(IPosition position) {
        this.spawnpoint = position;
    }

    /**
     * Returns the spawnpoint of the ball.
     *
     * @return spawnpoint
     */
    @Override
    public IPosition getSpawnpoint() {
        return this.spawnpoint;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Serializes the given content.
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();
        data.put("spawnpoint", ((LocationBuilder) this.spawnpoint).serialize());
        return data;
    }
}
