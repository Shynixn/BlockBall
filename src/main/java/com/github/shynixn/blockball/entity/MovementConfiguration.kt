package com.github.shynixn.blockball.entity


class MovementConfiguration {
    /**
     * The gravity modifier how fast a ball falls to the ground after being kicked or
     * thrown in to the sky.
     */
    var gravityModifier: Double = 0.07

    /**
     * The speed reducement of the ball in the air is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-airDrag)
     */
    var airResistance: Double = 0.001

    /**
     * The speed reducement of the ball on the ground is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-groundDrag)
     */
    var rollingResistance: Double = 0.1

    /**
     * Horizontal touch modifier.
     */
    var horizontalTouchModifier: Double = 1.0

    /**
     * Vertical touch modifier.
     */
    var verticalTouchModifier: Double = 1.0

    /**
     * The overall shot velocity.
     */
    var shotVelocity: Double = 1.5

    /**
     * Overwrite for y velocity.
     */
    var shotPassYVelocityOverwrite: Double = 1.0

    /**
     * The overall pass velocity.
     */
    var passVelocity: Double = 1.2

    /**
     * The maximum strength of spin.
     */
    var maximumSpinVelocity: Double = 0.08

    /**
     * Maximum vertical angle (in degrees) when launching a ball
     */
    var maximumPitch: Int = 60

    /**
     * Minimum vertical angle (in degrees) when launching a ball
     */
    var minimumPitch: Int = 0

    /**
     * Initial value of vertical angle (in degrees) when launching a ball
     */
    var defaultPitch: Int = 20
}
