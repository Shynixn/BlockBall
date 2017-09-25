package com.github.shynixn.blockball.bukkit.logic.arena;

import com.github.shynixn.blockball.api.entities.DoubleJumpMeta;
import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.blockball.api.entities.LightParticle;
import com.github.shynixn.blockball.lib.ParticleEffect;
import com.github.shynixn.blockball.lib.SParticle;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
class DoubleJumpMetaEntity implements DoubleJumpMeta {

    private boolean allowJump = true;
    private double horizontalStrength = 2.6D;
    private double verticalStrength = 1.0D;

    private LightParticle particle = new SParticle(ParticleEffect.EXPLOSION_NORMAL, 4, 0.0002, 2, 2, 2);
    private SoundMeta soundEffect = new SoundBuilder("GHAST_FIREBALL", 100.0, 1.0);

    /**
     * Enables the double jump
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.allowJump = enabled;
    }

    /**
     * Checks if the double jump is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.allowJump;
    }

    /**
     * Sets the horizontal strength of the double jump
     *
     * @param strength strength
     */
    @Override
    public void setHorizontalStrength(double strength) {
        this.horizontalStrength = strength;
    }

    /**
     * Returns the horizontal strength of the double jump
     *
     * @return strength
     */
    @Override
    public double getHorizontalStrength() {
        return this.horizontalStrength;
    }

    /**
     * Sets the vertical strength of the double jump
     *
     * @param strength strength
     */
    @Override
    public void setVerticalStrength(double strength) {
        this.verticalStrength = strength;
    }

    /**
     * Returns the vertical strength of the double jump
     *
     * @return strength
     */
    @Override
    public double getVerticalStrength() {
        return this.verticalStrength;
    }

    /**
     * Returns the particleEffect of the double jump
     *
     * @return particleEffect
     */
    @Override
    public LightParticle getParticleEffect() {
        return this.particle;
    }

    /**
     * Returns the soundEffect of the double jump
     *
     * @return soundEffect
     */
    @Override
    public SoundMeta getSoundEffect() {
        return this.soundEffect;
    }

    /**
     * Sets the soundEffect of the double jump
     *
     * @param meta meta
     */
    void setSoundEffect(SoundMeta meta) {
        this.soundEffect = meta;
    }

    /**
     * Sets the particleEffect of the double jump
     *
     * @param particle particleEffect
     */
    void setParticle(LightParticle particle) {
        this.particle = particle;
    }
}
