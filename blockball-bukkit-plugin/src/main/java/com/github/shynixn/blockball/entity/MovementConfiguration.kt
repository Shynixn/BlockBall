package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class MovementConfiguration {
    /**
     * The gravity modifier how fast a ball falls to the ground after being kicked or
     * thrown in to the sky.
     */
    @YamlSerialize(orderNumber = 1, value = "gravity-mod")
    var gravityModifier: Double = 0.07

    /**
     * The speed reducement of the ball in the air is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-airDrag)
     */
    @YamlSerialize(orderNumber = 2, value = "air-resistance")
    var airResistance: Double = 0.001

    /**
     * The speed reducement of the ball on the ground is calculated by
     *
     * NewVelocity = Current Velocity * (1.0-groundDrag)
     */
    @YamlSerialize(orderNumber = 3, value = "rolling-resistance")
    var rollingResistance: Double = 0.1

    /**
     * Horizontal touch modifier.
     */
    @YamlSerialize(orderNumber = 4, value = "horizontal-touch")
    var horizontalTouchModifier: Double = 1.0

    /**
     * Vertical touch modifier.
     */
    @YamlSerialize(orderNumber = 5, value = "vertical-touch")
    var verticalTouchModifier: Double = 1.0

    /**
     * The overall shot velocity.
     */
    @YamlSerialize(orderNumber = 6, value = "shot-velocity")
    var shotVelocity: Double = 1.5

    /**
     * The overall pass velocity.
     */
    @YamlSerialize(orderNumber = 7, value = "pass-velocity")
    var passVelocity: Double = 1.2

    /**
     * The maximum strength of spin.
     */
    @YamlSerialize(orderNumber = 8, value = "max-spin")
    var maximumSpinVelocity: Double = 0.08

    /**
     * Maximum vertical angle (in degrees) when launching a ball
     */
    @YamlSerialize(orderNumber = 11, value = "max-pitch")
    var maximumPitch: Int = 60

    /**
     * Minimum vertical angle (in degrees) when launching a ball
     */
    @YamlSerialize(orderNumber = 12, value = "min-pitch")
    var minimumPitch: Int = 0

    /**
     * Initial value of vertical angle (in degrees) when launching a ball
     */
    @YamlSerialize(orderNumber = 13, value = "default-pitch")
    var defaultPitch: Int = 20
}
