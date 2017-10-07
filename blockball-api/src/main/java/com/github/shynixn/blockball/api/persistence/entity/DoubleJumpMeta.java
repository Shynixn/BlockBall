package com.github.shynixn.blockball.api.persistence.entity;

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
public interface DoubleJumpMeta extends Persistenceable<DoubleJumpMeta> {

    /**
     * Enables the double jump.
     *
     * @param enabled enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Checks if the double jump is enabled.
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Sets the horizontal strength of the double jump.
     *
     * @param strength strength
     */
    void setHorizontalStrength(double strength);

    /**
     * Returns the horizontal strength of the double jump.
     *
     * @return strength
     */
    double getHorizontalStrength();

    /**
     * Sets the cooldown in seconds between each jump.
     *
     * @param amount amount
     */
    void setCooldownInSeconds(int amount);

    /**
     * Returns the cooldown in seconds between each jump.
     *
     * @return amount
     */
    int getCooldownInSeconds();

    /**
     * Sets the vertical strength of the double jump.
     *
     * @param strength strength
     */
    void setVerticalStrength(double strength);

    /**
     * Returns the vertical strength of the double jump.
     *
     * @return strength
     */
    double getVerticalStrength();

    /**
     * Returns the particleEffect of the double jump.
     *
     * @return particleEffect
     */
    ParticleEffectMeta getParticleEffect();

    /**
     * Returns the soundEffect of the double jump.
     *
     * @return soundEffect
     */
    SoundMeta getSoundEffect();
}
