package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.sound.SoundMeta

class SoccerBallMeta {
    @Comment("A ball consists of 2 entities. Everything below render are parts being visible for your players. e.g. skin and size.")
    var render: SoccerBallRenderMeta = SoccerBallRenderMeta()

    @Comment("A ball consists of 2 entities. Everything below hitbox are parts being used to let players interact with the ball.")
    var hitbox: SoccerBallHitBoxMeta = SoccerBallHitBoxMeta()

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

    @Comment("Sound effects.")
    val effects: MutableMap<BallActionType, String> = HashMap()

    class SoccerBallHitBoxMeta {
        @Comment("Size of the click-able hitbox. Press F3 + B in-game to view it.")
        var clickHitBoxSize: Double = 1.5

        @Comment("Is leftClicking the ball enabled?")
        var leftClickEnabled: Boolean = true

        @Comment("Is rightClicking the ball enabled?")
        var rightClickEnabled: Boolean = true

        @Comment("Size of the running-into hitbox.")
        var touchHitBoxSize: Double = 1.0

        @Comment("Is running-into the ball enabled?")
        var touchEnabled: Boolean = true

        @Comment("Allows to move the hitbox up and down on the y-axe to make the ball appear more in the air or closer to the ground.")
        var offSetY: Double = -0.3

        @Comment("Amount of ticks until the ball can intercept interaction again.")
        var interactionCoolDownTicks: Int = 20

        @Comment("Delay in ticks until the ball executes the kick pass requested by the player. If you set this to 0, the ball will be more smooth but direction manipulations may not be as accurate anymore.")
        var leftClickRightClickDelayTicks: Int = 1

        @Comment("Amount of ticks until the ball can intercept interaction by the same player again.")
        var interactionCoolDownPerPlayerTicks: Int = 7

        @Comment("In older Minecraft versions BlockBall uses a Slime instead of an InteractionEntity to simulate a hitbox. If true, you can make this slime visible in older versions.")
        var slimeVisible: Boolean = false
    }

    class SoccerBallRenderMeta {
        @Comment("Size of the rendered ball. These days you can set an arbitrary size. In older Minecraft versions, this version only supported 1.0 (STANDARD) and 0.5 (SMALL). ")
        var scale: Double = 1.0

        @Comment("The item rendering the ball.")
        var item: Item = Item().also {
            it.typeName = "PLAYER_HEAD,397"
            it.durability = "3"
            it.skinBase64 =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ=="
        }

        @Comment("Should the ball rotate?")
        var rotating: Boolean = true

        @Comment("Allows to move the ball up and down on the y-axe to make the ball appear more in the air or closer to the ground.")
        var offSetY: Double = -1.0
    }

    init {
        effects[BallActionType.ONKICK] = "ball_kick"
    }
}
