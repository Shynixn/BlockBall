package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.BallSize
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.sound.SoundMeta

class BallMeta {
    /** Size of the ball.**/
    var size: BallSize = BallSize.NORMAL

    /**
     * Item.
     */
    var item: Item = Item().also {
        it.typeName = "PLAYER_HEAD,397"
        it.durability = "3"
        it.skinBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhlNGE3MGI3YmJjZDdhOGMzMjJkNTIyNTIwNDkxYTI3ZWE2YjgzZDYwZWNmOTYxZDJiNGVmYmJmOWY2MDVkIn19fQ=="
    }
    /**
     * If set to true, the slime is visible instead of the ball.
     */
    var isSlimeVisible: Boolean = false

    /**
     * Size of the hitbox used for interaction detecting.
     */
    var interactionHitBoxSize: Double = 2.0

    /**
     * Size of the hitbox used for kicking and passing detecting.
     */
    var kickPassHitBoxSize: Double = 5.0

    /**
     * Delay in ticks until the ball executes the kick pass request by the player.
     * Is useful for magnus force calculation.
     */
    var kickPassDelay: Int = 5

    /**
     * Amount of ticks until the ball can intercept interaction again after
     * performing 1 interaction.
     */
    var interactionCoolDown: Int = 20


    /** Should the ball rotate? */
    var rotating: Boolean = true

    /**
     * Is leftclick kicking the ball enabled?
     */
    var enabledKick: Boolean = true

    /**
     * Is the rightclick passing the ball enabled?
     */
    var enabledPass: Boolean = true

    /**
     * Is the moving into the ball enabled?
     */
    var enabledInteract: Boolean = true

    /**
     * Hitbox relocation value for ground heights.
     */
    var hitBoxRelocation: Double = 0.0

    /**
     * Movement modifier.
     */
    val movementModifier: MovementConfiguration = MovementConfiguration()

    /**
     * Particle effects.
     */
    val soundEffects: MutableMap<BallActionType, SoundMeta> = HashMap()

    /** Spawning delay. */
    var delayInTicks: Int = 0

    /** Spawnpoint of the ball. */
    var spawnpoint: Vector3d? = null
}
