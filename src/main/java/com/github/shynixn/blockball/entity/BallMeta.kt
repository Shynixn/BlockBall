package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.BallSize

class BallMeta {

    /** Skin of the ball.**/
    @YamlSerialize(orderNumber = 2, value = "skin")
    var skin: String = "http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d"

    /** Size of the ball.**/
    @YamlSerialize(orderNumber = 1, value = "size")
    var size: BallSize = BallSize.NORMAL

    /**
     * Optional nbt applied to the item of the ball. e.g. Skull.
     */
    @YamlSerialize(orderNumber = 2, value = "item-nbt")
    var itemNbt: String? = ""

    /**
     * Item Type being used.
     */
    @YamlSerialize(orderNumber = 2, value = "item-type")
    var itemType: String = "PLAYER_HEAD,397"

    /**
     * Item Damage.
     */
    @YamlSerialize(orderNumber = 2, value = "item-damage")
    var itemDamage: Int = 3

    /**
     * If set to true, the slime is visible instead of the ball.
     */
    @YamlSerialize(orderNumber = 3, value = "slime-visible")
    var isSlimeVisible: Boolean = false

    /**
     * Size of the hitbox used for interaction detecting.
     */
    @YamlSerialize(orderNumber = 4, value = "interaction-hitbox-size")
    var interactionHitBoxSize: Double = 2.0

    /**
     * Size of the hitbox used for kicking and passing detecting.
     */
    @YamlSerialize(orderNumber = 5, value = "kickpass-hitbox-size")
    var kickPassHitBoxSize: Double = 5.0

    /**
     * Delay in ticks until the ball executes the kick pass request by the player.
     * Is useful for magnus force calculation.
     */
    @YamlSerialize(orderNumber = 6, value = "kickpass-delay-ticks")
    var kickPassDelay: Int = 5

    /**
     * Amount of ticks until the ball can intercept interaction again after
     * performing 1 interaction.
     */
    @YamlSerialize(orderNumber = 7, value = "interaction-cooldown-ticks")
    var interactionCoolDown: Int = 20


    /** Should the ball rotate? */
    @YamlSerialize(orderNumber = 8, value = "rotating")
    var rotating: Boolean = true

    /**
     * Is leftclick kicking the ball enabled?
     */
    @YamlSerialize(orderNumber = 9, value = "enable-kick")
    var enabledKick: Boolean = true

    /**
     * Is the rightclick passing the ball enabled?
     */
    @YamlSerialize(orderNumber = 10, value = "enable-pass")
    var enabledPass: Boolean = true

    /**
     * Is the moving into the ball enabled?
     */
    @YamlSerialize(orderNumber = 11, value = "enable-interact")
    var enabledInteract: Boolean = true

    /**
     * Hitbox relocation value for ground heights.
     */
    @YamlSerialize(orderNumber = 12, value = "hitbox-relocation")
    var hitBoxRelocation: Double = 0.0

    /**
     * Should the ball always bounce of walls?
     */
    @YamlSerialize(orderNumber = 13, value = "always-bounce")
    var alwaysBounce: Boolean = true

    /**
     * Movement modifier.
     */
    @YamlSerialize(orderNumber = 14, value = "modifiers")
    val movementModifier: MovementConfiguration = MovementConfiguration()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 15, value = "particle-effects", implementation = Particle::class)
    val particleEffects: MutableMap<BallActionType, Particle> = HashMap()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 16, value = "sound-effects", implementation = Sound::class)
    val soundEffects: MutableMap<BallActionType, Sound> = HashMap()

    /** Spawning delay. */
    @YamlSerialize(orderNumber = 17, value = "spawn-delay")
    var delayInTicks: Int = 0

    /** Spawnpoint of the ball. */
    @YamlSerialize(orderNumber = 18, value = "spawnpoint", implementation = Position::class)
    var spawnpoint: Position? = null
}
