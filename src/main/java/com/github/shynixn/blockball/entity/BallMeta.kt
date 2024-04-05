package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.BallSize

class BallMeta {

    /** Skin of the ball.**/
    var skin: String = "http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d"

    /** Size of the ball.**/
    var size: BallSize = BallSize.NORMAL

    /**
     * Optional nbt applied to the item of the ball. e.g. Skull.
     */
    var itemNbt: String? = ""

    /**
     * Item Type being used.
     */
    var itemType: String = "PLAYER_HEAD,397"

    /**
     * Item Damage.
     */
    var itemDamage: Int = 3

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
     * Should the ball always bounce of walls?
     */
    var alwaysBounce: Boolean = true

    /**
     * Movement modifier.
     */
    val movementModifier: MovementConfiguration = MovementConfiguration()

    /**
     * Particle effects.
     */
    val particleEffects: MutableMap<BallActionType, Particle> = HashMap()

    /**
     * Particle effects.
     */
    val soundEffects: MutableMap<BallActionType, Sound> = HashMap()

    /** Spawning delay. */
    var delayInTicks: Int = 0

    /** Spawnpoint of the ball. */
    var spawnpoint: Position? = null
}
