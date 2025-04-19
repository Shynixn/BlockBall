package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.BallSize
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.sound.SoundMeta

class SoccerBallSettings {
    @Comment("Size of the ball. Possible values: NORMAL, SMALL")
    var size: BallSize = BallSize.NORMAL

    @Comment("The item rendering the ball.")
    var item: Item = Item().also {
        it.typeName = "PLAYER_HEAD,397"
        it.durability = "3"
        it.skinBase64 =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ=="
    }

    @Comment("If true, the slime is visible. This only works in older Minecraft versions where a slime is used instead of the interaction entity.")
    var isSlimeVisible: Boolean = false

    @Comment("Hitbox size for touching the ball.")
    var interactionHitBoxSize: Double = 2.0

    @Comment("Hitbox size for left and rightclicking the ball.")
    var kickPassHitBoxSize: Double = 2.0

    @Comment("Delay in ticks until the ball executes the kick pass requested by the player. If you set this to 0, the ball will be more smooth but direction manipulations may not be as accurate anymore.")
    var kickPassDelay: Int = 2


    @Comment("Amount of ticks until the ball can intercept interaction again.")
    var interactionCoolDown: Int = 20

    @Comment("Amount of ticks until the ball can intercept interaction by the same player again.")
    var interactionCoolDownPerPlayerMs: Int = 400

    @Comment("Should the ball rotate?")
    var rotating: Boolean = true

    @Comment("Is leftclicking the ball enabled?")
    var enabledKick: Boolean = true

    @Comment("Is rightclicking the ball enabled?")
    var enabledPass: Boolean = true

    @Comment("Is touching the ball enabled?")
    var enabledInteract: Boolean = true

    @Comment("Allows to move the ball up and down on the y-axe to make the ball appear more in the air or closer to the ground.")
    var hitBoxRelocation: Double = 0.0

    @Comment("All movement modifiers.")
    val movementModifier: MovementConfiguration = MovementConfiguration()
    @Comment("Sound effects.")
    val soundEffects: MutableMap<BallActionType, SoundMeta> = HashMap()

    @Comment("The spawn delay of the ball in ticks. 1 tick = 50ms")
    var delayInTicks: Int = 60

    @Comment("Spawn location of the ball.")
    var spawnpoint: Vector3d? = null
}
