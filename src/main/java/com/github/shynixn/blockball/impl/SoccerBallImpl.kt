package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import com.github.shynixn.blockball.enumeration.ClickType
import com.github.shynixn.blockball.event.BallRemoveEvent
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.api.meta.EntityAttribute
import com.github.shynixn.mcutils.packet.api.meta.InteractionMetadata
import com.github.shynixn.mcutils.packet.api.meta.enumeration.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityAttributes
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityDestroy
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityEquipment
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityMetadata
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntitySpawn
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityTeleport
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.atan2

class SoccerBallImpl(
    location: Location, private val packetService: PacketService,
    private val rayTracingService: RayTracingService,
    private val itemService: ItemService,
    /**
     * Gets the metadata.
     */
    override val meta: SoccerBallMeta,

    /**
     * Entity id of the hitbox.
     */
    override val hitBoxEntityId: Int,

    /**
     * Entity id of the render.
     */
    override val renderEntityId: Int
) : SoccerBall {
    companion object {
        const val SCALE: String = "scale"
    }

    // Properties
    private var position: Vector3d = location.toVector3d()
    private var motion: Vector3d = Vector3d(0.0, -0.7, 0.0)
    private var rotationDegrees: Int = 0
    private var spinVelocity: Double = 0.0

    // Tracker
    private val playerTracker = GameObjectPlayerTracker(
        meta.render.renderDistance,
        { p -> spawnEntityForPlayer(p) },
        { p -> removeEntityForPlayer(p) }
    )

    // Interact Cooldowns
    private val perPlayerCooldown = HashMap<Player, GameObjectCooldownTimer>()
    private val globalCooldown = GameObjectCooldownTimer(meta.physics.globalInteractionCooldownTicks * 50)

    // Interval Timers
    private val playerFetchTimer = GameObjectIntervalTimer(meta.physics.fetchPlayerPositionsIntervalTicks * 50)

    /**
     * Gets if the entity is dead.
     */
    override var isDead: Boolean = false

    /**
     * Gets if the ball is on ground.
     */
    override var isOnGround: Boolean = false

    /**
     * If set, the ball is currently grabbed by a player.
     */
    override val grabbingPlayer: Player? = null

    /**
     * Sets or gets if the ball can be interacted with.
     */
    override var isInteractable: Boolean = true

    /**
     * If set, only this player can interact with the ball.
     */
    override var lockedPlayer: Player? = null

    /**
     * Teleports the ball to the given [location].
     */
    override fun teleport(location: Location) {
        this.position = location.toVector3d()
    }

    /**
     * Gets the location of the ball.
     */
    override fun getLocation(): Location {
        return position.toLocation()
    }

    /**
     * Gets the velocity of the ball.
     */
    override fun getVelocity(): Vector {
        return motion.toVector()
    }

    /**
     * Sets the velocity of the ball.
     */
    override fun setVelocity(velocity: Vector) {
        this.motion = velocity.toVector3d()
    }

    /**
     * Checks if the player is interacting with the ball in some way.
     */
    override fun applyInteraction(
        player: Player, clickType: ClickType
    ) {
        if (!canPlayerInteractWithBall(player)) {
            return
        }
    }


    /**
     * Removes the ball.
     */
    override fun remove() {
        if (isDead) {
            return
        }

        val ballDeathEvent = BallRemoveEvent(this)
        Bukkit.getPluginManager().callEvent(ballDeathEvent)

        if (ballDeathEvent.isCancelled) {
            return
        }

        isDead = true
    }

    /**
     * Delta Ms.
     */
    fun update(deltaMs: Int) {
        // Fetch players
        if (playerFetchTimer.update(deltaMs)) {
            playerTracker.update(getLocation())
        }

        // Calculate physics
        calculatePhysics(deltaMs)

        // Send changes
        updateEntityForAllPlayers()
    }

    private fun removeEntityForPlayer(player: Player) {
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(hitBoxEntityId, renderEntityId)
        })
    }

    private fun updateEntityForAllPlayers() {
        val renderLocation = getLocation()
        renderLocation.y += meta.render.visualVerticalOffset
        renderLocation.yaw = vectorToYaw(getVelocity())

        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset
        hitBoxLocation.yaw = vectorToYaw(getVelocity())

        val players = HashSet(playerTracker.cache.keys)

        for (player in players) {
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = renderEntityId
                it.target = renderLocation
            })

            if (meta.render.isRotationEnabled && !meta.render.isSlimeVisible) {
                packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                    it.armorStandHeadRotation = EulerAngle(-1 * rotationDegrees.toDouble(), 0.0, 0.0)
                    it.entityId = renderEntityId
                })
            }

            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = hitBoxEntityId
                it.target = hitBoxLocation
            })
        }
    }

    private fun spawnEntityForPlayer(player: Player) {
        val renderLocation = getLocation()

        // Armorstand
        renderLocation.y += meta.render.visualVerticalOffset
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.target = renderLocation
            it.entityId = renderEntityId
            it.entityType = EntityType.ARMOR_STAND
        })

        if (!meta.render.isSlimeVisible) {
            val stack = itemService.toItemStack(meta.render.visualItem)
            packetService.sendPacketOutEntityEquipment(player, PacketOutEntityEquipment().also {
                it.entityId = renderEntityId
                it.items = listOf(Pair(ArmorSlotType.HELMET, stack))
            })
        }

        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_20_R4)) {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = renderEntityId
                it.isInvisible = true
            })
            packetService.sendPacketOutEntityAttributes(player, PacketOutEntityAttributes().also {
                it.entityId = renderEntityId
                it.attributes = listOf(EntityAttribute().also { at ->
                    at.id = SCALE
                    at.base = meta.render.modelScale
                })
            })
        } else {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = renderEntityId
                it.isInvisible = true
                it.isArmorstandSmall = meta.render.modelScale == 0.5
            })
        }

        // HitBox
        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset

        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_19_R3)) {
            // We use the Interaction Entity since 1.19.4.
            packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
                it.entityId = hitBoxEntityId
                it.entityType = EntityType.INTERACTION
                it.target = hitBoxLocation
            })

            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = hitBoxEntityId
                it.interactionMetadata = InteractionMetadata().also {
                    it.height = meta.physics.interactionBoundsSize
                    it.width = meta.physics.interactionBoundsSize
                }
            })
        } else {
            // Use Slime for older versions.
            packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
                it.entityId = hitBoxEntityId
                it.entityType = EntityType.SLIME
                it.target = hitBoxLocation
            })

            if (meta.render.isSlimeVisible) {
                packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                    it.slimeSize = meta.physics.interactionBoundsSize.toInt()
                    it.entityId = hitBoxEntityId
                })
            } else {
                packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                    it.slimeSize = meta.physics.interactionBoundsSize.toInt()
                    it.entityId = hitBoxEntityId
                    it.isInvisible = true
                })
            }
        }
    }

    private fun canPlayerInteractWithBall(player: Player): Boolean {
        if (!isInteractable) {
            return false
        }

        if (lockedPlayer != null && player != lockedPlayer) {
            return false
        }

        if (!globalCooldown.canExecute()) {
            return false
        }

        var playerCooldown = perPlayerCooldown[player]
        if (playerCooldown == null) {
            playerCooldown = GameObjectCooldownTimer(meta.physics.perPlayerInteractionCooldownTicks * 50)
            perPlayerCooldown[player] = playerCooldown
        }

        if (!playerCooldown.canExecute()) {
            return false
        }

        globalCooldown.tryExecute(0)
        playerCooldown.tryExecute(0)
        return true
    }

    private fun findPlayerAction(player: Player, clickType: ClickType): SoccerBallMeta.InteractionMeta? {
        val triggerType = if (clickType == ClickType.LEFT && player.isSneaking) {
            BallTriggerActionType.SNEAK_LEFT_CLICK
        } else if (clickType == ClickType.LEFT && player.isSprinting) {
            BallTriggerActionType.SPRINT_LEFT_CLICK
        } else if (clickType == ClickType.LEFT && !player.isOnGround) {
            BallTriggerActionType.JUMP_LEFT_CLICK
        } else if (clickType == ClickType.RIGHT && player.isSneaking) {
            BallTriggerActionType.SNEAK_RIGHT_CLICK
        } else if (clickType == ClickType.RIGHT && player.isSprinting) {
            BallTriggerActionType.SPRINT_RIGHT_CLICK
        } else if (clickType == ClickType.RIGHT && !player.isOnGround) {
            BallTriggerActionType.JUMP_RIGHT_CLICK
        } else if (clickType == ClickType.LEFT) {
            BallTriggerActionType.LEFT_CLICK
        } else if (clickType == ClickType.RIGHT) {
            BallTriggerActionType.RIGHT_CLICK
        } else if (clickType == ClickType.NONE && player.isSneaking) {
            BallTriggerActionType.SNEAK_COLLIDE
        } else if (clickType == ClickType.NONE && player.isSprinting) {
            BallTriggerActionType.SPRINT_COLLIDE
        } else if (clickType == ClickType.NONE && !player.isOnGround) {
            BallTriggerActionType.JUMP_COLLIDE
        } else {
            BallTriggerActionType.COLLIDE
        }

        val itemSlot = player.inventory.heldItemSlot
        val interaction = meta.interactions.firstOrNull { e ->
            e.triggerType == triggerType && itemSlot >= e.hotbarRangeStart && itemSlot <= e.hotbarRangeEnd
        }
        return interaction
    }

    private fun vectorToYaw(vector: Vector): Float {
        // Invert X because Minecraft yaw is reversed around the Y-axis
        var yaw = Math.toDegrees(atan2(-vector.x, vector.z))

        // Normalize to the range (-180, 180]
        if (yaw < -180) {
            yaw += 360.0
        }
        if (yaw > 180) {
            yaw -= 360.0
        }

        return yaw.toFloat()
    }


    private fun calculatePhysics(deltaMs: Int) {
        // Scale physics values from per-tick (50 ms) to actual elapsed time, capped to avoid instability on lag spikes
        val tickScale = (deltaMs / 50.0).coerceAtMost(2.0)

        // Ground check: cast a short ray downward to detect if the ball is still resting on a surface
        if (isOnGround) {
            val groundProbe = Vector3d(0.0, -0.05, 0.0)
            val groundCheck = rayTracingService.rayTraceMotion(position, groundProbe, false, false)
            if (!groundCheck.hitBlock) {
                isOnGround = false
            }
        }

        val velocity = Vector(motion.x, motion.y, motion.z)

        if (isOnGround) {
            // Apply surface rolling friction to horizontal axes
            val friction = (1.0 - meta.physics.rollingFriction * tickScale).coerceAtLeast(0.0)
            velocity.x *= friction
            velocity.z *= friction
            velocity.y = 0.0
        } else {
            // Apply gravity
            velocity.y -= meta.physics.gravityModifier * tickScale
            // Apply air drag to all axes
            val drag = (1.0 - meta.physics.airDrag * tickScale).coerceAtLeast(0.0)
            velocity.x *= drag
            velocity.y *= drag
            velocity.z *= drag
        }

        motion.x = velocity.x
        motion.y = velocity.y
        motion.z = velocity.z

        // Update visual rotation once per physics tick
        if (meta.render.isRotationEnabled && !meta.render.isSlimeVisible) {
            calculateNextRotation()
        }

        // Stop the ball completely when speed drops below the rest threshold
        if (velocity.length() < meta.physics.restVelocityThreshold) {
            motion.x = 0.0
            motion.y = 0.0
            motion.z = 0.0
            return
        }

        // Apply motion and resolve block collisions
        rayTrace(position, motion)
    }

    private fun rayTrace(position: Vector3d, motion: Vector3d) {
        val rayTraceResult = rayTracingService.rayTraceMotion(position, motion, false, false)
        val targetPosition = rayTraceResult.targetPosition
        val hasHitBlock = rayTraceResult.hitBlock
        val blockDirectionHit = rayTraceResult.blockDirection

        // Move the ball to where the ray trace ended (collision point or position + motion)
        position.x = targetPosition.x
        position.y = targetPosition.y
        position.z = targetPosition.z

        if (hasHitBlock) {
            // Build the block face outward normal from the hit direction
            val normalX = blockDirectionHit.modX.toDouble()
            val normalY = blockDirectionHit.modY.toDouble()
            val normalZ = blockDirectionHit.modZ.toDouble()

            // Reflect velocity: v' = (v - 2*(v·n)*n) * bounciness
            val velocity = Vector(motion.x, motion.y, motion.z)
            val normal = Vector(normalX, normalY, normalZ)
            val dot = velocity.dot(normal)
            velocity.subtract(normal.multiply(2.0 * dot)).multiply(meta.physics.bounciness)

            motion.x = velocity.x
            motion.y = velocity.y
            motion.z = velocity.z

            // Upward-facing normal means the ball landed on the top of a block
            isOnGround = normalY > 0.5

            // Suppress residual vertical bounce when the reflected speed is negligible
            if (isOnGround && abs(motion.y) < meta.physics.restVelocityThreshold) {
                motion.y = 0.0
            }
        } else {
            isOnGround = false
        }
    }

    private fun calculateNextRotation() {
        // 360 0 0 is a full forward rotation.
        // Length of the velocity is the speed of the ball.
        val velocity = getVelocity()
        val speed = if (isOnGround) {
            Vector(velocity.x, 0.0, velocity.z).length()
        } else {
            velocity.length()
        }

        // Ease spin velocity toward the ball's current speed, capped at maxSpinVelocity
        val targetSpin = speed.coerceAtMost(meta.physics.maxSpinVelocity)
        spinVelocity += (targetSpin - spinVelocity) * meta.physics.spinDampening
        spinVelocity = spinVelocity.coerceIn(0.0, meta.physics.maxSpinVelocity)

        if (spinVelocity > 0.001) {
            // Map spin velocity linearly to degrees per tick (20 deg/tick at maxSpinVelocity)
            val degreesPerTick = (spinVelocity / meta.physics.maxSpinVelocity) * 20.0
            rotationDegrees = (rotationDegrees - degreesPerTick.toInt()) % 360
        }
    }
}