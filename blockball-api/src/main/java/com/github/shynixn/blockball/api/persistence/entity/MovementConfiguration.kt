package com.github.shynixn.blockball.api.persistence.entity

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
interface MovementConfiguration {
    /**
     * The gravity modifier how fast a ball falls to the ground after being kicked or
     * thrown in to the sky.
     */
    var gravityModifier: Double

    /**
     * The speed reducement of the ball in the air is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-airDrag)
     */
    var airResistance: Double

    /**
     * The speed reducement of the ball on the ground is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-groundDrag)
     */
    var rollingResistance: Double

    /**
     * Horizontal touch modifier.
     */
    var horizontalTouchModifier: Double

    /**
     * Vertical touch modifier.
     */
    var verticalTouchModifier: Double

    /**
     * The overall shot velocity.
     */
    var shotVelocity: Double

    /**
     * The overall pass velocity.
     */
    var passVelocity: Double

    /**
     * The maximum strength of spin.
     */
    var maximumSpinVelocity: Double

    /**
     * Maximum vertical angle (in degrees) when launching a ball
     */
    var maximumPitch: Int

    /**
     * Minimum vertical angle (in degrees) when launching a ball
     */
    var minimumPitch: Int

    /**
     * Initial value of vertical angle (in degrees) when launching a ball
     */
    var defaultPitch: Int
}
