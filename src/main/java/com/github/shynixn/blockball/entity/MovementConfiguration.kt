package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment


class MovementConfiguration {
    @Comment("The gravity modifier how fast a ball falls to the ground after being kicked.")
    var gravityModifier: Double = 0.07

    @Comment("The speed reducement of the ball in the air.")
    var airResistance: Double = 0.001

    @Comment("The speed reducement of the ball on the ground.")
    var rollingResistance: Double = 0.1

    @Comment("The horizontal speed gain after touching the ball.")
    var horizontalTouchModifier: Double = 1.0

    @Comment("The vertical speed gain after touching the ball.")
    var verticalTouchModifier: Double = 1.0

    @Comment("The horizontal and vertical speed gain after left clicking the ball.")
    var shotVelocity: Double = 1.5

    @Comment("Allows to override the shotVelocity with a vertical modifier if it is any other value than 1.0.")
    var shotPassYVelocityOverwrite: Double = 1.0

    @Comment("The horizontal and vertical speed gain after right clicking the ball.")
    var passVelocity: Double = 1.2

    @Comment("Maximum spin velocity.")
    var maximumSpinVelocity: Double = 0.08

    @Comment("Maximum pitch velocity.")
    var maximumPitch: Int = 60

    @Comment("Maximum pitch velocity.")
    var minimumPitch: Int = 0

    @Comment("Default pitch velocity.")
    var defaultPitch: Int = 20
}
