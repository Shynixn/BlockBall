package com.github.shynixn.blockball.api.persistence.entity;

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
public interface BallMeta extends Persistenceable<BallMeta> {

    /**
     * Returns the sound played when the ball gets hit
     *
     * @return meta
     */
    SoundMeta getGenericHitSound();

    /**
     * Returns the sound played when the ball spawns
     *
     * @return meta
     */
    SoundMeta getSpawnSound();

    /**
     * Returns the particleEffect played when the ball gets hit.
     *
     * @return particle
     */
    ParticleEffectMeta getGenericHitParticleEffect();

    /**
     * Returns the particleEffect played when the ball spawns.
     *
     * @return particle
     */
    ParticleEffectMeta getSpawnParticleEffect();

    /**
     * Returns the horizontal strength the ball is going to fly
     *
     * @return strength
     */
    double getHorizontalStrength();

    /**
     * Sets the horizontal strength of the ball  is going to fly
     *
     * @param strength strength
     */
    void setHorizontalStrength(double strength);

    /**
     * Returns the vertical strength the ball is going to fly
     *
     * @return strength
     */
    double getVerticalStrength();

    /**
     * Sets the vertical strength the ball is going to fly
     *
     * @param strength strength
     */
    void setVerticalStrength(double strength);

    /**
     * Sets if rotating is enabled
     *
     * @param enabled enabled
     */
    void setRotatingEnabled(boolean enabled);

    /**
     * Returns if rotating is enabled
     *
     * @return enabled
     */
    boolean isRotatingEnabled();

    /**
     * Changes the skin of the ball. Has to be a skin-URL or name of a player
     *
     * @param skin skin
     */
    void setSkin(String skin);

    /**
     * Returns the skin of the ball
     *
     * @return skin
     */
    String getSkin();
}
