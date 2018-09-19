package com.github.shynixn.blockball.api.compatibility;

import com.github.shynixn.blockball.api.business.enumeration.BallSize;
import com.github.shynixn.blockball.api.persistence.entity.Particle;
import com.github.shynixn.blockball.api.persistence.entity.Sound;

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
public interface BallMeta {

    /**
     * Returns the particle effect for the given action.
     *
     * @param effect effect
     * @return particleEffect
     */
    Particle getParticleEffectOf(ActionEffect effect);

    /**
     * Returns the sound effect for the given action.
     *
     * @param effect effect
     * @return soundEffect
     */
    Sound getSoundEffectOf(ActionEffect effect);

    /**
     * Returns the modifiers of the ball.
     *
     * @return modifiers
     */
    BallModifiers getModifiers();

    /**
     * Returns a controller for all bounce Objects.
     *
     * @return list
     */
    BounceController getBounceObjectController();

    /**
     * Sets always bouncing back from blocks regardless of bounceController.
     *
     * @param enabled enabled
     */
    void setAlwaysBounceBack(boolean enabled);

    /**
     * Returns if always bouncing back from blocks regardless of bounceController.
     *
     * @return enabled
     */
    boolean isAlwaysBounceBack();

    /**
     * Sets if the ball is carry able.
     *
     * @param enabled enabled
     */
    void setCarryable(boolean enabled);

    /**
     * Returns if the ball is carry able.
     *
     * @return carryAble
     */
    boolean isCarryable();

    /**
     * Sets if the ball should display a rotation animation when being kicked or thrown.
     *
     * @param enabled enabled
     */
    void setRotatingEnabled(boolean enabled);

    /**
     * Sets the size of the hitbox of the ball. Default 2.
     *
     * @param size size
     */
    void setHitBoxSize(double size);

    /**
     * Returns the size of the hitbox of the ball.
     *
     * @return size
     */
    double getHitBoxSize();

    /**
     * Sets the hitbox relocation distance the hitbox of the ball is in reality.
     *
     * @param distance distance
     */
    void setHitBoxRelocationDistance(double distance);

    /**
     * Returns the hitbox relocation distance.
     *
     * @return distance
     */
    double getHitBoxRelocationDistance();

    /**
     * Returns if the ball displays a rotation animation when being kicked or thrown.
     *
     * @return enabled
     */
    boolean isRotatingEnabled();

    /**
     * Changes the skin of the ball. Has to be a skin-URL or name of a player.
     *
     * @param skin skin
     */
    void setSkin(String skin);

    /**
     * Returns the skin of the ball.
     *
     * @return skin
     */
    String getSkin();

    /**
     * Returns the size of the ball.
     *
     * @return size
     */
    BallSize getSize();

    /**
     * Sets the size of the ball.
     *
     * @param size size
     */
    void setSize(BallSize size);
}
