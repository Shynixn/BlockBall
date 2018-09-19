package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.compatibility.BallModifiers;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

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
public class BallModifications implements BallModifiers, ConfigurationSerializable {

    private double rollingStrength = 1.0;
    private double gravityStrength = 1.0;
    private double horizontalKickStrength = 1.0;
    private double verticalKickStrength = 1.0;
    private double horizontalThrowStrength = 1.0;
    private double verticalThrowStrength = 1.0;
    private double horizontalTouchStrength = 1.0;
    private double verticalTouchStrength = 1.0;
    private double bouncingStrength = 1.0;

    public BallModifications() {
        super();
    }

    public BallModifications(Map<String, Object> data) {
        super();
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        this.horizontalKickStrength = (double) data.get("horizontal-kick");
        this.verticalKickStrength = (double) data.get("vertical-kick");
        this.horizontalThrowStrength = (double) data.get("horizontal-throw");
        this.verticalThrowStrength = (double) data.get("vertical-throw");
        this.horizontalTouchStrength = (double) data.get("horizontal-touch");
        this.verticalTouchStrength = (double) data.get("vertical-touch");
        this.rollingStrength = (double) data.get("rolling-distance");
        this.gravityStrength = (double) data.get("gravity");

        if (data.containsKey("bouncing")) {
            this.bouncingStrength = (double) data.get("bouncing");
        }
    }

    /**
     * Returns the bounce modifier how fast a ball bounces of any block if he has got always-bounce enabled.
     *
     * @return strength
     */
    @Override
    public double getBounceModifier() {
        return this.bouncingStrength;
    }

    /**
     * Sets the bounce modifier how fast a ball bounces of any block if he has got always-bounce enabled.
     *
     * @param strength strength
     */
    @Override
    public void setBounceModifier(double strength) {
        this.bouncingStrength = strength;
    }

    /**
     * Returns the gravity modifier how fast a ball falls to the ground after being kicked or
     * thrown in to the sky.
     *
     * @return strength
     */
    @Override
    public double getGravityModifier() {
        return this.gravityStrength;
    }

    /**
     * Sets the gravity modifier how fast a ball falls to the ground after being kicked or
     * thrown in to the sky.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setGravityModifier(double strength) {
        this.gravityStrength = strength;
        return this;
    }

    /**
     * Returns the rolling distance modifier.
     *
     * @return modifier
     */
    @Override
    public double getRollingDistanceModifier() {
        return this.rollingStrength;
    }

    /**
     * Sets the rolling distance modifier
     *
     * @param strength strength
     */
    @Override
    public BallModifications setRollingDistanceModifier(double strength) {
        this.rollingStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the horizontal velocity a player touches the ball.
     *
     * @return modifier
     */
    @Override
    public double getHorizontalTouchModifier() {
        return this.horizontalTouchStrength;
    }

    /**
     * Sets the modifier value for the horizontal velocity a player touches the ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setHorizontalTouchModifier(double strength) {
        this.horizontalTouchStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the vertical velocity a player touches the ball.
     *
     * @return modifier
     */
    @Override
    public double getVerticalTouchModifier() {
        return this.verticalTouchStrength;
    }

    /**
     * Sets the modifier value for the vertical velocity a player touches the ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setVerticalTouchModifier(double strength) {
        this.verticalTouchStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the horizontal velocity a player kicks the ball.
     *
     * @return strength
     */
    @Override
    public double getHorizontalKickStrengthModifier() {
        return this.horizontalKickStrength;
    }

    /**
     * Sets the modifier value for the horizontal velocity a player kicks the ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setHorizontalKickStrengthModifier(double strength) {
        this.horizontalKickStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the vertical velocity a player kicks the ball.
     *
     * @return strength
     */
    @Override
    public double getVerticalKickStrengthModifier() {
        return this.verticalKickStrength;
    }

    /**
     * Sets the modifier value for the vertical velocity a player kicksthe ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setVerticalKickStrengthModifier(double strength) {
        this.verticalKickStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the horizontal velocity a player throws the ball.
     *
     * @return strength
     */
    @Override
    public double getHorizontalThrowStrengthModifier() {
        return this.horizontalThrowStrength;
    }

    /**
     * Sets the modifier value for the horizontal velocity a player throws the ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setHorizontalThrowStrengthModifier(double strength) {
        this.horizontalThrowStrength = strength;
        return this;
    }

    /**
     * Returns the modifier value for the vertical velocity a player throws the ball.
     *
     * @return strength
     */
    @Override
    public double getVerticalThrowStrengthModifier() {
        return this.verticalThrowStrength;
    }

    /**
     * Sets the modifier value for the vertical velocity a player throws the ball.
     *
     * @param strength strength
     */
    @Override
    public BallModifications setVerticalThrowStrengthModifier(double strength) {
        this.verticalThrowStrength = strength;
        return this;
    }

    /**
     * Serializes the content.
     *
     * @return data
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("bouncing", this.getBounceModifier());
        data.put("horizontal-touch", this.getHorizontalTouchModifier());
        data.put("vertical-touch", this.getVerticalTouchModifier());
        data.put("horizontal-kick", this.getHorizontalKickStrengthModifier());
        data.put("vertical-kick", this.getVerticalKickStrengthModifier());
        data.put("horizontal-throw", this.getHorizontalThrowStrengthModifier());
        data.put("vertical-throw", this.getVerticalThrowStrengthModifier());
        data.put("rolling-distance", this.getRollingDistanceModifier());
        data.put("gravity", this.getGravityModifier());
        return data;
    }
}
