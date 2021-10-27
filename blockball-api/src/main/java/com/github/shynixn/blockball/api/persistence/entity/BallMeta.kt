package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.enumeration.BallSize

/**
 * Ball properties.
 */
interface BallMeta {
    /**
     * Is leftclick kicking the ball enabled?
     */
    var enabledKick: Boolean

    /**
     * Optional nbt applied to the item of the ball. e.g. Skull.
     */
    var itemNbt: String?

    /**
     * Is the rightclick passing the ball enabled?
     */
    var enabledPass: Boolean

    /**
     * If set to true, the slime is visible instead of the ball.
     */
    var isSlimeVisible: Boolean

    /**
     * Is the moving into the ball enabled?
     */
    var enabledInteract: Boolean

    /** Spawning delay. TODO: Move this to game. */
    var delayInTicks: Int

    /** Spawnpoint of the ball. TODO: Move this to game. */
    var spawnpoint: Position?

    /** Size of the ball.**/
    var size: BallSize

    /** Skin of the ball.**/
    var skin: String

    /** Should the ball rotate? */
    var rotating: Boolean

    /**
     * Hitbox relocation value for ground heights.
     */
    var hitBoxRelocation: Double

    /**
     * Size of the hitbox used for interaction detecting.
     */
    var interactionHitBoxSize: Double

    /**
     * Size of the hitbox used for kicking and passing detecting.
     */
    var kickPassHitBoxSize: Double

    /**
     * Delay in ticks until the ball executes the kick pass request by the player.
     * Is useful for magnus force calculation.
     */
    var kickPassDelay: Int

    /**
     * Amount of ticks until the ball can intercept interaction again after
     * performing 1 interaction.
     */
    var interactionCoolDown: Int

    /**
     * Should the ball always bounce of walls?
     */
    var alwaysBounce: Boolean

    /**
     * Movement modifier.
     */
    val movementModifier: MovementConfiguration

    /**
     * Particle effects.
     */
    val particleEffects: MutableMap<BallActionType, Particle>

    /**
     * Particle effects.
     */
    val soundEffects: MutableMap<BallActionType, Sound>
}
