package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import com.github.shynixn.blockball.enumeration.ClickType
import com.github.shynixn.blockball.event.BallActionEvent
import com.github.shynixn.blockball.event.BallRayTraceEvent
import com.github.shynixn.blockball.event.BallRemoveEvent
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

    private var consecutiveBounceCount: Int = 0

    // Properties
    private var position: Vector3d = location.toVector3d()
    private var motion: Vector = Vector(0.0, -0.7, 0.0)
    private var rotationDegrees: Double = 0.0
    private var spinY: Double = 0.0
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
        this.spinY = 0.0
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
        setVelocity(velocity, Vector(0.0, 0.0, 0.0))
    }

    /**
     * Sets the velocity of the ball.
     * Sets the y value of the spin vector to the horizontal spin.
     */
    override fun setVelocity(velocity: Vector, spin: Vector) {
        if (!Bukkit.isPrimaryThread()) {
            throw IllegalArgumentException("Thread violation!")
        }

        this.motion = velocity.clone()
        this.spinY = spin.y
        this.isOnGround = false
    }

    /**
     * Checks if the player is interacting with the ball in some way.
     */
    override fun applyInteraction(
        player: Player, clickType: ClickType
    ) {
        if (!Bukkit.isPrimaryThread()) {
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

        // 1. Convert horizontal yaw angles into radians for direction
        val yawRadians = Math.toRadians(yawDegrees)

        // 2. Gather raw force configurations and process through configured mass matrix limits
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)
        val horizontalForce = interactionMeta.horizontalImpulse / ballMass
        val verticalForce = interactionMeta.verticalImpulse / ballMass

        // 3. Compute vector components flatly across the 2D horizontal X/Z plane.
        val x = horizontalForce * -Math.sin(yawRadians)
        val z = horizontalForce * Math.cos(yawRadians)

        // 4. Set the vertical velocity to directly follow your configuration value.
        val y = verticalForce

        // 5. Build the final Bukkit Vector and apply it to your soccer ball
        val calculatedVelocity = Vector(x, y, z)
        this.setVelocity(calculatedVelocity, Vector(0.0, interactionMeta.spinImpulse / ballMass, 0.0))

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

        // Check if ball is stuck
        checkAndHandleStuckBall()

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
        val triggerTypes = when {
            clickType == ClickType.LEFT && player.isSneaking ->
                arrayOf(BallTriggerActionType.SNEAK_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)

            clickType == ClickType.LEFT && player.isSprinting ->
                arrayOf(BallTriggerActionType.SPRINT_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)

            clickType == ClickType.LEFT && !player.isOnGround ->
                arrayOf(BallTriggerActionType.JUMP_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)

            clickType == ClickType.RIGHT && player.isSneaking ->
                arrayOf(BallTriggerActionType.SNEAK_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)

            clickType == ClickType.RIGHT && player.isSprinting ->
                arrayOf(BallTriggerActionType.SPRINT_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)

            clickType == ClickType.RIGHT && !player.isOnGround ->
                arrayOf(BallTriggerActionType.JUMP_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)

            clickType == ClickType.LEFT ->
                arrayOf(BallTriggerActionType.LEFT_CLICK)

            clickType == ClickType.RIGHT ->
                arrayOf(BallTriggerActionType.RIGHT_CLICK)

            clickType == ClickType.NONE && player.isSneaking ->
                arrayOf(BallTriggerActionType.SNEAK_COLLIDE, BallTriggerActionType.COLLIDE)

            clickType == ClickType.NONE && player.isSprinting ->
                arrayOf(BallTriggerActionType.SPRINT_COLLIDE, BallTriggerActionType.COLLIDE)

            clickType == ClickType.NONE && !player.isOnGround ->
                arrayOf(BallTriggerActionType.JUMP_COLLIDE, BallTriggerActionType.COLLIDE)

            clickType == ClickType.NONE ->
                arrayOf(BallTriggerActionType.COLLIDE)

            else ->
                emptyArray()
        }

        val itemSlot = player.inventory.heldItemSlot
        val interaction = triggerTypes.firstNotNullOfOrNull { type ->
            meta.interactions.firstOrNull { e ->
                e.triggerType == type && itemSlot >= e.conditionHotBarRangeStart && itemSlot <= e.conditionHotBarRangeEnd
            }
        }

        return interaction
    }

    private fun vectorToYaw(vector: Vector): Float {
        var yaw = Math.toDegrees(atan2(-vector.x, vector.z))

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
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)
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
                spinY *= (1.0 - meta.physics.rollingFriction * 2.0 * tickScale).coerceIn(0.0, 1.0)
            }
        }

        val velocity = Vector(motion.x, motion.y, motion.z)
        writeDump("Motion-BeforePhysics: " + motion.x + "-" + motion.y + "-" + motion.z + " | isOnGround = $isOnGround")
        writeDump("Position-BeforePhysics: " + position.x + "-" + position.y + "-" + position.z)

        // Magnus Effect Calculations
        if (!isOnGround && velocity.lengthSquared() > 0.01 && abs(spinY) > 0.01) {
            val spinVector = Vector(0.0, spinY, 0.0)
            val curveForce = velocity.clone().crossProduct(spinVector)
            curveForce.multiply(meta.physics.curveMultiplier * tickScale)
            velocity.add(curveForce)
            writeDump("Applied curve force vector: ${curveForce.x}, ${curveForce.y}, ${curveForce.z}")
        }

        if (isOnGround) {
            val friction = (1.0 - meta.physics.rollingFriction * tickScale).coerceAtLeast(0.0)
            velocity.x *= friction
            velocity.z *= friction
            velocity.y = 0.0
            writeDump("Applied Rolling Friction physics branch")
        } else {
            // Apply mass scaling directly onto falling velocity modifiers
            velocity.y -= (meta.physics.gravityModifier / ballMass) * tickScale
            val drag = (1.0 - meta.physics.airDrag * tickScale).coerceAtLeast(0.0)
            velocity.x *= drag
            velocity.y *= drag
            velocity.z *= drag
            writeDump("Applied Gravity and Drag physics branch")
        }

        if (abs(spinY) > 0.001) {
            val spinDragFactor = (1.0 - meta.physics.spinDrag * tickScale).coerceIn(0.0, 1.0)
            spinY *= spinDragFactor
        } else {
            spinY = 0.0
        }

        motion.x = velocity.x
        motion.y = velocity.y
        motion.z = velocity.z

        writeDump("Motion-AfterPhysics: " + motion.x + "-" + motion.y + "-" + motion.z)

        if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
            calculateNextRotation()
        }

        if (abs(motion.x) > 0.001 || abs(motion.z) > 0.001) {
            this.lastYaw = vectorToYaw(motion)
        }

        if (isOnGround && abs(motion.x) < meta.physics.restVelocityThreshold && abs(motion.z) < meta.physics.restVelocityThreshold) {
            motion.x = 0.0
            motion.y = 0.0
            motion.z = 0.0
            spinY = 0.0
            writeDump("Ball rolling speed dropped below threshold. Stopping completely.")
            return
        }

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            writeDump("Ball is currently stopped")
            return
        }

        rayTrace(position, motion)

        if (!isOnGround && motion.y < 0.0 && abs(motion.y) < meta.physics.restVelocityThreshold) {
            val groundProbe = Vector(0.0, -0.5, 0.0)
            val movementLength = groundProbe.length()
            val maxTraceDistance = movementLength.coerceAtLeast(0.01)
            val groundResult =
                rayTracingService.rayTrace(position.toLocation(), groundProbe, maxTraceDistance, false, false)
            if (groundResult.hasHitBlock && groundResult.blockFace!!.modY > 0.5) {
                position.y = groundResult.targetLocation.y
                isOnGround = true
                motion.y = 0.0
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

        val rayTraceResult = rayTracingService.rayTrace(
            rayTraceStartPosition.toLocation(),
            motion.clone(),
            maxTraceDistance,
            false,
            false
        )

        val rayTraceEvent =
            BallRayTraceEvent(this, rayTraceResult.hasHitBlock, rayTraceResult.targetLocation, rayTraceResult.blockFace)
        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        val targetPosition = rayTraceResult.targetLocation
        val hasHitBlock = rayTraceResult.hasHitBlock
        val blockDirectionHit = rayTraceResult.blockFace

        if (hasHitBlock) {
            consecutiveBounceCount++

            writeDump("HAS HIT BLOCK " + blockDirectionHit + " target position " + targetPosition)
            val normalX = blockDirectionHit!!.modX.toDouble()
            val normalY = blockDirectionHit.modY.toDouble()
            val normalZ = blockDirectionHit.modZ.toDouble()

            val velocity = Vector(motion.x, motion.y, motion.z)
            val normal = Vector(normalX, normalY, normalZ)
            val dot = velocity.dot(normal)

            velocity.subtract(normal.clone().multiply((1.0 + meta.physics.bounciness) * dot))

            motion.x = velocity.x
            motion.y = velocity.y
            motion.z = velocity.z

            spinY *= -0.25
            val epsilon = 0.002

            if (abs(normal.x) > 0.1 || abs(normal.z) > 0.1) {
                // Resolved Wall Collision: Add motion vector vectors to pass position boundaries outwards
                position.x = targetPosition.x + (normal.x * epsilon) + motion.x
                position.z = targetPosition.z + (normal.z * epsilon) + motion.z
                position.y = originalY + motion.y
                writeDump("HIT WALL")
            }
            else if (abs(normal.y) > 0.1) {
                position.x = targetPosition.x
                position.z = targetPosition.z

                if (normal.y > 0.1) {
                    if (motion.y <= 0.0 || abs(motion.y) < (meta.physics.restVelocityThreshold * 4.0)) {
                        position.y = targetPosition.y - 0.2
                        this.isOnGround = true
                        this.motion.y = 0.0
                        writeDump("HIT FLOAT -> Grounded smoothly. Velocity zeroed.")
                        return
                    }
                }

                position.y = (targetPosition.y - 0.2) + (normal.y * epsilon)
                writeDump("HIT FLOAT")
            }
        } else {
            consecutiveBounceCount = 0
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

            rotationDegrees = (rotationDegrees - degreesPerTick) % 360.0
            if (rotationDegrees < 0) {
                rotationDegrees += 360.0
            }
        }
    }

    private fun checkAndHandleStuckBall() {
        if (consecutiveBounceCount >= 10) {
            val ballLocation = getLocation()
            ballLocation.y += 1
            teleport(ballLocation)
            println("Simple recovery")
            consecutiveBounceCount = 0
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