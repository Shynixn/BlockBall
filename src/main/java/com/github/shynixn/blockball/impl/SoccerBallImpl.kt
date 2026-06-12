package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import com.github.shynixn.blockball.enumeration.ClickType
import com.github.shynixn.blockball.event.BallActionEvent
import com.github.shynixn.blockball.event.BallRayTraceEvent
import com.github.shynixn.blockball.event.BallRemoveEvent
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.EntityAttribute
import com.github.shynixn.mcutils.packet.api.meta.InteractionMetadata
import com.github.shynixn.mcutils.packet.api.meta.enumeration.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.*
import com.github.shynixn.shyparticles.contract.ParticleEffectService
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.atan2

class SoccerBallImpl(
    location: Location, private val packetService: PacketService,
    private val particleEffectService: ParticleEffectService,
    private val rayTracingService: CustomRayTracingServiceNativeImpl,
    private val plugin: Plugin,
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
    private var motion: Vector = Vector(0.0, -0.7, 0.0)
    private var rotationDegrees: Double = 0.0
    private var lastYaw: Float = 0.0F

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
        return motion.clone()
    }

    /**
     * Sets the velocity of the ball.
     */
    override fun setVelocity(velocity: Vector) {
        if(!Bukkit.isPrimaryThread()){
            throw IllegalArgumentException("Thread violation!")
        }

        this.motion = velocity.clone()
        if (this.motion.y > 0.0) {
            this.isOnGround = false
            writeDump("[DEBUG-VELOCITY] External velocity applied with upward force (${this.motion.y}). Setting isOnGround = false")
        }
    }

    /**
     * Checks if the player is interacting with the ball in some way.
     */
    override fun applyInteraction(
        player: Player, clickType: ClickType
    ) {
        if(!Bukkit.isPrimaryThread()){
            throw IllegalArgumentException("Thread violation!")
        }

        if (!canPlayerInteractWithBall(player)) {
            return
        }

        applyInteractionToBall(player, clickType)
    }

    /**
     * Performs the actual player action to ball trigger action conversion.
     */
    private fun applyInteractionToBall(player: Player, clickType: ClickType) {
        val interactionMeta = findPlayerAction(player, clickType) ?: return

        println("SHOOT!")

        // Check Event
        val ballTriggerActionEvent =
            BallActionEvent(this, player, interactionMeta.executionType, interactionMeta.triggerType)
        Bukkit.getPluginManager().callEvent(ballTriggerActionEvent)
        if (ballTriggerActionEvent.isCancelled) {
            return
        }

        val playerLocation = player.location
        val yawDegrees = playerLocation.yaw.toDouble()

        // 1. Gather look pitch and invert it (Minecraft looking UP is negative, but math needs UP positive)
        var launchPitch = -playerLocation.pitch.toInt()

        // 2. Clamp the launch pitch using your SoccerBallMeta constraints
        val clampedPitch = launchPitch.coerceIn(meta.physics.minLaunchPitch, meta.physics.maxLaunchPitch)

        // 3. Convert pitch and yaw angles into radians for trigonometry
        val pitchRadians = Math.toRadians(clampedPitch.toDouble())
        val yawRadians = Math.toRadians(yawDegrees)

        // 4. Compute vector components combining directional angles with configuration impulses
        val horizontalForce = interactionMeta.horizontalImpulse
        val verticalForce = interactionMeta.verticalImpulse * Math.sin(pitchRadians)

        // Minecraft vectors: -sin(yaw) for X, cos(yaw) for Z, and scaled by horizontal look direction cos(pitch)
        val x = horizontalForce * -Math.sin(yawRadians) * Math.cos(pitchRadians)
        val z = horizontalForce * Math.cos(yawRadians) * Math.cos(pitchRadians)
        val y = 0.2

        // 5. Build the final Bukkit Vector and apply it to your soccer ball
        val calculatedVelocity = Vector(x, y, z)
        this.setVelocity(calculatedVelocity)

        // Apply Effect
        val effect = particleEffectService.getEffectMetaFromName(interactionMeta.effectName)
        if (effect != null) {
            particleEffectService.startEffect(effect, { getLocation() }, null, null)
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
        for (player in playerTracker.cache.keys.toHashSet()) {
            removeEntityForPlayer(player)
        }
    }

    /**
     * Delta Ms.
     */
    fun update(deltaMs: Int) {
        // Fetch players
        if (playerFetchTimer.update(deltaMs)) {
            playerTracker.update(getLocation())
            checkPlayerTouchInteractions()
        }

        // Calculate physics
        calculatePhysics(deltaMs)

        // Send changes
        updateEntityForAllPlayers()
    }

    private fun checkPlayerTouchInteractions() {
        val playerLocationPairs = HashSet(playerTracker.cache.entries)
        val hitboxSize = meta.physics.collisionBoundsSize
        val ballLocation = getLocation()
        val playerHittingTheBall = playerLocationPairs.asSequence()
            .map { e ->
                Pair(e.key, e.value.distance(ballLocation))
            }.filter { p -> p.second < hitboxSize }
            .sortedBy { e -> e.second }.firstOrNull { e -> canPlayerInteractWithBall(e.first) }

        if (playerHittingTheBall != null) {
            applyInteractionToBall(playerHittingTheBall.first, ClickType.NONE)
        }
    }

    private fun removeEntityForPlayer(player: Player) {
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(hitBoxEntityId, renderEntityId)
        })
    }

    private fun updateEntityForAllPlayers() {
        val yaw = if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            lastYaw
        } else {
            vectorToYaw(getVelocity())
        }
        val renderLocation = getLocation()
        renderLocation.y += meta.render.visualVerticalOffset
        renderLocation.yaw = yaw

        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset
        hitBoxLocation.yaw = yaw

        val players = HashSet(playerTracker.cache.keys)

        for (player in players) {
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = renderEntityId
                it.target = renderLocation
            })

            if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
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

        if (!meta.render.slimeVisible) {
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

            if (meta.render.slimeVisible) {
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
        println("TRY")

        if (!isInteractable) {
            return false
        }

        if (lockedPlayer != null && player != lockedPlayer) {
            return false
        }

        if (!globalCooldown.canExecute()) {
            println("GLOBAL COOLDOWN")
            return false
        }

        var playerCooldown = perPlayerCooldown[player]
        if (playerCooldown == null) {
            playerCooldown = GameObjectCooldownTimer(meta.physics.perPlayerInteractionCooldownTicks * 50)
            perPlayerCooldown[player] = playerCooldown
        }

        if (!playerCooldown.canExecute()) {
            println("PLAYER COOL")
            return false
        }

        globalCooldown.execute()
        playerCooldown.execute()
        println("EXECUTE")
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
        var interaction = meta.interactions.firstOrNull { e ->
            e.triggerType == triggerType && itemSlot >= e.hotbarRangeStart && itemSlot <= e.hotbarRangeEnd
        }
        if (interaction == null) {
            // Fallback to collide
            interaction = meta.interactions.firstOrNull { e ->
                e.triggerType == BallTriggerActionType.COLLIDE && itemSlot >= e.hotbarRangeStart && itemSlot <= e.hotbarRangeEnd
            }
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
        val tickScale = (deltaMs / 50.0).coerceAtMost(2.0)
        writeDump("---- TICK SCALE: $tickScale")

        if (isOnGround) {
            writeDump("[DEBUG-GROUND] Evaluating ground check. Current Position Y: ${position.y}")
            val groundProbe = Vector(0.0, -0.05, 0.0)
            val movementLength = groundProbe.length()
            val maxTraceDistance = movementLength.coerceAtLeast(0.01)
            val groundCheck =
                rayTracingService.rayTrace(position.toLocation(), groundProbe, maxTraceDistance, false, false)
            if (!groundCheck.hasHitBlock) {
                isOnGround = false
                writeDump("[DEBUG-GROUND] Detached from surface! Setting isOnGround = false")
            } else {
                writeDump("[DEBUG-GROUND] Maintained surface contact. Staying grounded.")
            }
        }

        val velocity = Vector(motion.x, motion.y, motion.z)
        writeDump("Motion-BeforePhysics: " + motion.x + "-" + motion.y + "-" + motion.z + " | isOnGround = $isOnGround")
        writeDump("Position-BeforePhysics: " + position.x + "-" + position.y + "-" + position.z)

        if (isOnGround) {
            val friction = (1.0 - meta.physics.rollingFriction * tickScale).coerceAtLeast(0.0)
            velocity.x *= friction
            velocity.z *= friction
            velocity.y = 0.0
            writeDump("Applied Rolling Friction physics branch")
        } else {
            velocity.y -= meta.physics.gravityModifier * tickScale
            val drag = (1.0 - meta.physics.airDrag * tickScale).coerceAtLeast(0.0)
            velocity.x *= drag
            velocity.y *= drag
            velocity.z *= drag
            writeDump("Applied Gravity and Drag physics branch")
        }

        motion.x = velocity.x
        motion.y = velocity.y
        motion.z = velocity.z

        writeDump("Motion-AfterPhysics: " + motion.x + "-" + motion.y + "-" + motion.z)

        if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
            calculateNextRotation()
        }

        // Capture dynamic yaw during high-velocity ticks before physics checks clear it
        if (abs(motion.x) > 0.001 || abs(motion.z) > 0.001) {
            this.lastYaw = vectorToYaw(motion)
        }

        // Hard stop for ground state rolling when it runs completely out of steam
        if (isOnGround && abs(motion.x) < meta.physics.restVelocityThreshold && abs(motion.z) < meta.physics.restVelocityThreshold) {
            motion.x = 0.0
            motion.y = 0.0
            motion.z = 0.0
            writeDump("Ball rolling speed dropped below threshold. Stopping completely.")
            return
        }

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            writeDump("Ball is currently stopped")
            return
        }

        // Perform RayTracing step
        rayTrace(position, motion)

        // Snap to ground to avoid micro bounces.
        if (!isOnGround && motion.y < 0.0 && abs(motion.y) < meta.physics.restVelocityThreshold) {
            val groundProbe = Vector(0.0, -0.5, 0.0)
            val movementLength = groundProbe.length()
            val maxTraceDistance = movementLength.coerceAtLeast(0.01)
            val groundResult =
                rayTracingService.rayTrace(position.toLocation(), groundProbe, maxTraceDistance, false, false)
            if (groundResult.hasHitBlock && groundResult.blockFace!!.modY > 0.5) {
                position.y = groundResult.targetLocation.y
                isOnGround = true
                motion.y = 0.0 // Clear only vertical velocity!
                println("SNAP")
                writeDump("Stop vertical fallback flight. Snapped to ground floor. Remaining XZ velocity preserved for rolling.")
            }
        }
    }

    private fun rayTrace(position: Vector3d, motion: Vector) {
        val originalY = position.y
        val rayTraceStartPosition = position.copy().add(0.0, 0.2, 0.0)
        val movementLength = motion.length()
        val maxTraceDistance = movementLength.coerceAtLeast(0.01)

        val customRaytracing = CustomRayTracingServiceNativeImpl()
        val rayTraceResult = customRaytracing.rayTrace(
            rayTraceStartPosition.toLocation(),
            motion.clone(),
            maxTraceDistance,
            false,
            false
        )

        val rayTraceEvent = BallRayTraceEvent(this, rayTraceResult.hasHitBlock, rayTraceResult.targetLocation, rayTraceResult.blockFace)
        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        val targetPosition = rayTraceResult.targetLocation
        val hasHitBlock = rayTraceResult.hasHitBlock
        val blockDirectionHit = rayTraceResult.blockFace

        if (hasHitBlock) {
            writeDump("HAS HIT BLOCK " + blockDirectionHit + " target position " + targetPosition)
            val normalX = blockDirectionHit!!.modX.toDouble()
            val normalY = blockDirectionHit.modY.toDouble()
            val normalZ = blockDirectionHit.modZ.toDouble()

            val velocity = Vector(motion.x, motion.y, motion.z)
            val normal = Vector(normalX, normalY, normalZ)
            val dot = velocity.dot(normal)

            // Reflect velocity safely using the cloned vector normal
            velocity.subtract(normal.clone().multiply((1.0 + meta.physics.bounciness) * dot))

            motion.x = velocity.x
            motion.y = velocity.y
            motion.z = velocity.z

            val epsilon = 0.002

            // If we hit a vertical wall (EAST, WEST, NORTH, SOUTH)
            if (abs(normal.x) > 0.1 || abs(normal.z) > 0.1) {
                position.x = targetPosition.x + (normal.x * epsilon)
                position.z = targetPosition.z + (normal.z * epsilon)
                position.y = originalY + motion.y
                writeDump("HIT WALL")
            }
            // If we hit a floor or ceiling (UP, DOWN)
            else if (abs(normal.y) > 0.1) {
                position.x = targetPosition.x
                position.z = targetPosition.z

                // FIX: If we hit a floor surface (BlockFace.UP) from above
                if (normal.y > 0.1) {
                    // Check if vertical speed is low enough to drop into rolling physics
                    if (motion.y <= 0.0 || abs(motion.y) < (meta.physics.restVelocityThreshold * 4.0)) {
                        position.y = targetPosition.y - 0.2 // Anchor perfectly to the block floor
                        this.isOnGround = true
                        this.motion.y = 0.0 // Completely kill the vertical bounce bounce loop
                        writeDump("HIT FLOAT -> Grounded smoothly. Velocity zeroed.")
                        return
                    }
                }

                // Normal bouncing tracking for high speed drops
                position.y = (targetPosition.y - 0.2) + (normal.y * epsilon)
                writeDump("HIT FLOAT")
            }
        } else {
            writeDump("HAS NOT HIT " + blockDirectionHit)

            position.x = targetPosition.x
            position.z = targetPosition.z

            if (motion.y == 0.0 && isOnGround) {
                position.y = originalY
            } else {
                position.y = originalY + motion.y
            }

            if (motion.y != 0.0) {
                isOnGround = false
            }
        }
    }

    private fun calculateNextRotation() {
        val minimumSpeedToShowRotation = 0.01
        val degreesPerSpeedValueMultiplier = 15
        val minimumDegreesWhenStillMoving = 7
        val velocity = getVelocity()
        val horizontalSpeed = velocity.length()

        if (horizontalSpeed > minimumSpeedToShowRotation) {
            val degreesPerTick = horizontalSpeed * degreesPerSpeedValueMultiplier + minimumDegreesWhenStillMoving

            // Accumulate rotation smoothly, wrapping cleanly between 0.0 and 360.0
            rotationDegrees = (rotationDegrees - degreesPerTick) % 360.0
            if (rotationDegrees < 0) {
                rotationDegrees += 360.0
            }
        }
    }

    private val fileId = Bukkit.getPluginManager().getPlugin("BlockBall")!!.dataFolder.resolve(
        System.currentTimeMillis().toString() + "ball.txt"
    )
    var enabledDump = true

    private fun writeDump(text: String) {
        if (enabledDump) {
            fileId.appendText(text + "\n")
        }
    }
}