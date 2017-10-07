package com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties;

import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.ParticleEffectBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.SoundBuilder;
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
public class BallProperties extends PersistenceObject<BallMeta> implements BallMeta {
    public static final String FOOTBALL_SKIN = "http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d";

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "skin")
    private String skin = FOOTBALL_SKIN;

    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "rotating")
    private boolean rotating = true;

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "strength.vertical")
    private double verticalStrength = 0.8;

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "strength.horizontal")
    private double horizontalStrength = 1.8;

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "sounds.generic")
    private final SoundMeta genericHitSound = new SoundBuilder()
            .setName("ZOMBIE_WOOD")
            .setPitch(1.0)
            .setVolume(1.0);

    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "sounds.spawn")
    private final SoundMeta spawnSound = new SoundBuilder()
            .setName("NOTE_BASS")
            .setPitch(1.0)
            .setVolume(1.0);

    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "particles.generic")
    private final ParticleEffectMeta genericHitParticle = new ParticleEffectBuilder()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.EXPLOSION_HUGE)
            .setAmount(1)
            .setSpeed(0.0002)
            .setOffset(0.01, 0.01, 0.01);

    @YamlSerializer.YamlSerialize(orderNumber = 8, value = "particles.spawn")
    private final ParticleEffectMeta genericSpawnParticle = new ParticleEffectBuilder()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.SMOKE_LARGE)
            .setAmount(4)
            .setSpeed(0.0002)
            .setOffset(2, 2, 2);

    /**
     * Returns the sound played when the ball gets hit
     *
     * @return meta
     */
    @Override
    public SoundMeta getGenericHitSound() {
        return this.genericHitSound;
    }

    /**
     * Returns the sound played when the ball spawns
     *
     * @return meta
     */
    @Override
    public SoundMeta getSpawnSound() {
        return this.spawnSound;
    }

    /**
     * Returns the particleEffect played when the ball gets hit.
     *
     * @return particle
     */
    @Override
    public ParticleEffectMeta getGenericHitParticleEffect() {
        return this.genericHitParticle;
    }

    /**
     * Returns the particleEffect played when the ball spawns.
     *
     * @return particle
     */
    @Override
    public ParticleEffectMeta getSpawnParticleEffect() {
        return this.genericSpawnParticle;
    }

    /**
     * Returns the horizontal strength the ball is going to fly
     *
     * @return strength
     */
    @Override
    public double getHorizontalStrength() {
        return this.horizontalStrength;
    }

    /**
     * Sets the horizontal strength of the ball  is going to fly
     *
     * @param strength strength
     */
    @Override
    public void setHorizontalStrength(double strength) {
        this.horizontalStrength = strength;
    }

    /**
     * Returns the vertical strength the ball is going to fly
     *
     * @return strength
     */
    @Override
    public double getVerticalStrength() {
        return this.verticalStrength;
    }

    /**
     * Sets the vertical strength the ball is going to fly
     *
     * @param strength strength
     */
    @Override
    public void setVerticalStrength(double strength) {
        this.verticalStrength = strength;
    }

    /**
     * Sets if rotating is enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setRotatingEnabled(boolean enabled) {
        this.rotating = enabled;
    }

    /**
     * Returns if rotating is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isRotatingEnabled() {
        return this.rotating;
    }

    /**
     * Changes the skin of the ball. Has to be a skin-URL or name of a player
     *
     * @param skin skin
     */
    @Override
    public void setSkin(String skin) {
        if (skin == null)
            throw new IllegalArgumentException("Skin cannot be null!");
        this.skin = skin;
    }

    /**
     * Returns the skin of the ball
     *
     * @return skin
     */
    @Override
    public String getSkin() {
        return this.skin;
    }
}
