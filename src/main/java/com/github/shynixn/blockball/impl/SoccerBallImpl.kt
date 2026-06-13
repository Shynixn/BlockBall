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
    location: Location,
    private val packetService: PacketService,
    private val particleEffectService: ParticleEffectService,
    private val rayTracingService: CustomRayTracingServiceNativeImpl,
    private val plugin: Plugin,
    private val itemService: ItemService,
    override val meta: SoccerBallMeta,
    override val hitBoxEntityId: Int,
    override val renderEntityId: Int
) : SoccerBall {

    companion object {
        const val SCALE: String = "scale"

        /**
         * Vertical buffer offset used during ray-tracing calculations to keep the point-mass
         * representation from slipping through solid block geometry over continuous steps.
         */
        const val RAYTRACE_OFFSET = 0.2
    }

    // --- Core Kinematic & Structural Properties ---
    private var position: Vector3d = location.toVector3d()
    private var motion: Vector = Vector(0.0, -0.7, 0.0)
    private var rotationDegrees: Double = 0.0
    private var spinY: Double = 0.0
    private var lastYaw: Float = 0.0F
    private var consecutiveBounceCount: Int = 0

    // --- Entity Trackers & Network Utilities ---
    private val playerTracker = GameObjectPlayerTracker(
        meta.render.renderDistance,
        { p -> spawnEntityForPlayer(p) },
        { p -> removeEntityForPlayer(p) }
    )

    // --- Throttling & Input Cooldown Engines ---
    private val perPlayerCooldown = HashMap<Player, GameObjectCooldownTimer>()
    private val globalCooldown = GameObjectCooldownTimer(meta.physics.globalInteractionCooldownTicks * 50)
    private val playerFetchTimer = GameObjectIntervalTimer(meta.physics.fetchPlayerPositionsIntervalTicks * 50)

    override var isDead: Boolean = false
    override var isOnGround: Boolean = false
    override val grabbingPlayer: Player? = null
    override var isInteractable: Boolean = true
    override var lockedPlayer: Player? = null

    /**
     * Instantly relocates the ball to a target destination and clears rotational energy.
     */
    override fun teleport(location: Location) {
        this.position = location.toVector3d()
        this.spinY = 0.0
    }

    override fun getLocation(): Location {
        return position.toLocation()
    }

    override fun getVelocity(): Vector {
        return motion.clone()
    }

    override fun setVelocity(velocity: Vector) {
        setVelocity(velocity, Vector(0.0, 0.0, 0.0))
    }

    /**
     * Explicitly updates the linear motion and vertical angular spin axes of the entity.
     * Must be called synchronized on the primary server thread.
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
     * Evaluates thread and gameplay constraints before routing player inputs into action impulses.
     */
    override fun applyInteraction(player: Player, clickType: ClickType) {
        if (!Bukkit.isPrimaryThread()) {
            throw IllegalArgumentException("Thread violation!")
        }

        if (!canPlayerInteractWithBall(player)) {
            return
        }

        applyInteractionToBall(player, clickType)
    }

    /**
     * Maps user action context to configured execution profiles, computes multi-axis impulse
     * distributions modified by ball mass constraints, and commits the resulting physics shifts.
     */
    private fun applyInteractionToBall(player: Player, clickType: ClickType) {
        val interactionMeta = findPlayerAction(player, clickType) ?: return

        // Fire plugin event framework step
        val ballTriggerActionEvent = BallActionEvent(this, player, interactionMeta.executionType, interactionMeta.triggerType)
        Bukkit.getPluginManager().callEvent(ballTriggerActionEvent)
        if (ballTriggerActionEvent.isCancelled) {
            return
        }

        // Calculate planar horizontal look directions
        val playerLocation = player.location
        val yawDegrees = playerLocation.yaw.toDouble()
        val yawRadians = Math.toRadians(yawDegrees)

        // Process impulse distribution against mass constraints to determine target acceleration
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)
        val horizontalForce = interactionMeta.horizontalImpulse / ballMass
        val verticalForce = interactionMeta.verticalImpulse / ballMass

        // Distribute planar components flatly across the 2D horizontal coordinates
        val x = horizontalForce * -Math.sin(yawRadians)
        val z = horizontalForce * Math.cos(yawRadians)
        val y = verticalForce

        // Apply derived linear velocities and structural curve spin factors
        val calculatedVelocity = Vector(x, y, z)
        this.setVelocity(calculatedVelocity, Vector(0.0, interactionMeta.spinImpulse / ballMass, 0.0))

        // Play particle assets if defined
        val effect = particleEffectService.getEffectMetaFromName(interactionMeta.effectName)
        if (effect != null) {
            particleEffectService.startEffect(effect, { getLocation() }, null, null)
        }
    }

    /**
     * Destroys virtual entity registrations and clears them from all active client-side view contexts.
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
     * The master ticker cycle managing positional fetches, environmental physics simulations,
     * stuck-state resolution, and client-side visualization synchronization.
     */
    fun update(deltaMs: Int) {
        if (playerFetchTimer.update(deltaMs)) {
            playerTracker.update(getLocation())
            checkPlayerTouchInteractions()
        }

        calculatePhysics(deltaMs)
        checkAndHandleStuckBall()
        updateEntityForAllPlayers()
    }

    /**
     * Scans proximity profiles of tracked players to execute automatic collision contacts (touches)
     * when a player breaks inside the structural bounds of the ball configuration.
     */
    private fun checkPlayerTouchInteractions() {
        val playerLocationPairs = HashSet(playerTracker.cache.entries)
        val hitboxSize = meta.physics.collisionBoundsSize
        val ballLocation = getLocation()

        val playerHittingTheBall = playerLocationPairs.asSequence()
            .map { e -> Pair(e.key, e.value.distance(ballLocation)) }
            .filter { p -> p.second < hitboxSize }
            .sortedBy { e -> e.second }
            .firstOrNull { e -> canPlayerInteractWithBall(e.first) }

        if (playerHittingTheBall != null) {
            applyInteractionToBall(playerHittingTheBall.first, ClickType.NONE)
        }
    }

    /**
     * Despawns virtual tracked packets from a targeted player's game client.
     */
    private fun removeEntityForPlayer(player: Player) {
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(hitBoxEntityId, renderEntityId)
        })
    }

    /**
     * Generates and transmits virtual packet movements, client-side metadata updates, and structural
     * roll rotations to all players within the visualization distance threshold.
     */
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

    /**
     * Builds and sends custom entity spawning sequences to newly tracking clients, selecting
     * targeted entities (Interaction vs. Slime blocks) based on the active server engine version.
     */
    private fun spawnEntityForPlayer(player: Player) {
        val renderLocation = getLocation()

        // Handle structural base ArmorStand initialization
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

        // Apply scale matrices according to server generation rules
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

        // Initialize structural physical tracking boundaries
        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset

        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_19_R3)) {
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
            packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
                it.entityId = hitBoxEntityId
                it.entityType = EntityType.SLIME
                it.target = hitBoxLocation
            })
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.slimeSize = meta.physics.interactionBoundsSize.toInt()
                it.entityId = hitBoxEntityId
                if (!meta.render.slimeVisible) it.isInvisible = true
            })
        }
    }

    /**
     * Assesses interaction state and tracks personal/global throttling cooldown thresholds
     * to safeguard the physics thread from spam input corruption exploits.
     */
    private fun canPlayerInteractWithBall(player: Player): Boolean {
        if (!isInteractable) return false
        if (lockedPlayer != null && player != lockedPlayer) return false
        if (!globalCooldown.canExecute()) return false

        var playerCooldown = perPlayerCooldown[player]
        if (playerCooldown == null) {
            playerCooldown = GameObjectCooldownTimer(meta.physics.perPlayerInteractionCooldownTicks * 50)
            perPlayerCooldown[player] = playerCooldown
        }
        if (!playerCooldown.canExecute()) return false

        globalCooldown.execute()
        playerCooldown.execute()
        return true
    }

    /**
     * Evaluates a player's physical context (sneaking, sprinting, jumping, hotbar selections)
     * against asset maps to determine the matching interaction target profile.
     */
    private fun findPlayerAction(player: Player, clickType: ClickType): SoccerBallMeta.InteractionMeta? {
        val triggerTypes = when {
            clickType == ClickType.LEFT && player.isSneaking -> arrayOf(BallTriggerActionType.SNEAK_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)
            clickType == ClickType.LEFT && player.isSprinting -> arrayOf(BallTriggerActionType.SPRINT_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)
            clickType == ClickType.LEFT && !player.isOnGround -> arrayOf(BallTriggerActionType.JUMP_LEFT_CLICK, BallTriggerActionType.LEFT_CLICK)
            clickType == ClickType.RIGHT && player.isSneaking -> arrayOf(BallTriggerActionType.SNEAK_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)
            clickType == ClickType.RIGHT && player.isSprinting -> arrayOf(BallTriggerActionType.SPRINT_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)
            clickType == ClickType.RIGHT && !player.isOnGround -> arrayOf(BallTriggerActionType.JUMP_RIGHT_CLICK, BallTriggerActionType.RIGHT_CLICK)
            clickType == ClickType.LEFT -> arrayOf(BallTriggerActionType.LEFT_CLICK)
            clickType == ClickType.RIGHT -> arrayOf(BallTriggerActionType.RIGHT_CLICK)
            clickType == ClickType.NONE && player.isSneaking -> arrayOf(BallTriggerActionType.SNEAK_COLLIDE, BallTriggerActionType.COLLIDE)
            clickType == ClickType.NONE && player.isSprinting -> arrayOf(BallTriggerActionType.SPRINT_COLLIDE, BallTriggerActionType.COLLIDE)
            clickType == ClickType.NONE && !player.isOnGround -> arrayOf(BallTriggerActionType.JUMP_COLLIDE, BallTriggerActionType.COLLIDE)
            clickType == ClickType.NONE -> arrayOf(BallTriggerActionType.COLLIDE)
            else -> emptyArray()
        }

        val itemSlot = player.inventory.heldItemSlot
        return triggerTypes.firstNotNullOfOrNull { type ->
            meta.interactions.firstOrNull { e ->
                e.triggerType == type && itemSlot >= e.conditionHotBarRangeStart && itemSlot <= e.conditionHotBarRangeEnd
            }
        }
    }

    /**
     * Direct Translation vector converter yielding angular horizontal degrees mapped to
     * standard Minecraft specifications. (-180 to 180 Range).
     */
    private fun vectorToYaw(vector: Vector): Float {
        var yaw = Math.toDegrees(atan2(-vector.x, vector.z))
        if (yaw < -180) yaw += 360.0
        if (yaw > 180) yaw -= 360.0
        return yaw.toFloat()
    }

    /**
     * Simulates continuous external physics over custom time steps. Tracks ground contact profiles,
     * executes Magnus aerodynamic curved shots logic, calculates dampening frictions, and resolves
     * rest thresholds.
     */
    private fun calculatePhysics(deltaMs: Int) {
        val tickScale = (deltaMs / 50.0).coerceAtMost(2.0)
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)

        // Verify continuous structural surface attachment profiles
        if (isOnGround) {
            val groundProbe = Vector(0.0, -0.05, 0.0)
            val groundCheck = rayTracingService.rayTrace(position.toLocation(), groundProbe, groundProbe.length().coerceAtLeast(0.01), false, false)
            if (!groundCheck.hasHitBlock) {
                isOnGround = false
            } else {
                // Decay airborne rotational spin immediately via turf frictional contact
                spinY *= (1.0 - meta.physics.rollingFriction * 2.0 * tickScale).coerceIn(0.0, 1.0)
            }
        }

        val velocity = Vector(motion.x, motion.y, motion.z)

        // -------------------------------------------------------------------------
        // AERODYNAMIC MAGNUS EFFECT SIMULATION (CURVED SHOTS)
        // -------------------------------------------------------------------------
        if (!isOnGround && velocity.lengthSquared() > 0.01 && abs(spinY) > 0.01) {
            val spinVector = Vector(0.0, spinY, 0.0)
            val curveForce = velocity.clone().crossProduct(spinVector)
            curveForce.multiply(meta.physics.curveMultiplier * tickScale)
            velocity.add(curveForce)
        }

        // Apply environmental resistance branches based on surface states
        if (isOnGround) {
            val friction = (1.0 - meta.physics.rollingFriction * tickScale).coerceAtLeast(0.0)
            velocity.x *= friction
            velocity.z *= friction
            velocity.y = 0.0
        } else {
            velocity.y -= (meta.physics.gravityModifier / ballMass) * tickScale
            val drag = (1.0 - meta.physics.airDrag * tickScale).coerceAtLeast(0.0)
            velocity.x *= drag
            velocity.y *= drag
            velocity.z *= drag
        }

        // Decay structural spin over time
        if (abs(spinY) > 0.001) {
            spinY *= (1.0 - meta.physics.spinDrag * tickScale).coerceIn(0.0, 1.0)
        } else {
            spinY = 0.0
        }

        // Commit calculated updates back to storage vectors
        motion.x = velocity.x
        motion.y = velocity.y
        motion.z = velocity.z

        if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
            calculateNextRotation()
        }

        if (abs(motion.x) > 0.001 || abs(motion.z) > 0.001) {
            this.lastYaw = vectorToYaw(motion)
        }

        // Enforce hard-stop thresholds if rolling speed falls completely out of steam
        if (isOnGround && abs(motion.x) < meta.physics.restVelocityThreshold && abs(motion.z) < meta.physics.restVelocityThreshold) {
            motion.x = 0.0
            motion.y = 0.0
            motion.z = 0.0
            spinY = 0.0
            return
        }

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        // Delegate structural block intersection queries to the raytrace processing loop
        rayTrace(position, motion)

        // Prevent mathematical micro-bounces near standard surfaces by snapping down safely
        if (!isOnGround && motion.y < 0.0 && abs(motion.y) < meta.physics.restVelocityThreshold) {
            val groundProbe = Vector(0.0, -0.5, 0.0)
            val groundResult = rayTracingService.rayTrace(position.toLocation(), groundProbe, groundProbe.length().coerceAtLeast(0.01), false, false)
            if (groundResult.hasHitBlock && groundResult.blockFace!!.modY > 0.5) {
                position.y = groundResult.targetLocation.y
                isOnGround = true
                motion.y = 0.0
            }
        }
    }

    /**
     * Executes line-of-sight intersection tests across target block bounds. Computes physical vector
     * reflections based on hit surface normals, factoring in bounciness coefficients and spin flips.
     */
    private fun rayTrace(position: Vector3d, motion: Vector) {
        val originalY = position.y
        val rayTraceStartPosition = position.copy().add(0.0, RAYTRACE_OFFSET, 0.0)
        val maxTraceDistance = motion.length().coerceAtLeast(0.01)

        val rayTraceResult = rayTracingService.rayTrace(rayTraceStartPosition.toLocation(), motion.clone(), maxTraceDistance, false, false)

        val rayTraceEvent = BallRayTraceEvent(this, rayTraceResult.hasHitBlock, rayTraceResult.targetLocation, rayTraceResult.blockFace)
        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        val targetPosition = rayTraceResult.targetLocation
        val hasHitBlock = rayTraceResult.hasHitBlock
        val blockDirectionHit = rayTraceResult.blockFace

        if (hasHitBlock) {
            consecutiveBounceCount++

            val normalX = blockDirectionHit!!.modX.toDouble()
            val normalY = blockDirectionHit.modY.toDouble()
            val normalZ = blockDirectionHit.modZ.toDouble()

            val velocity = Vector(motion.x, motion.y, motion.z)
            val normal = Vector(normalX, normalY, normalZ)
            val dot = velocity.dot(normal)

            // Safely execute mathematical vector reflection logic against structural geometry normals
            velocity.subtract(normal.clone().multiply((1.0 + meta.physics.bounciness) * dot))

            motion.x = velocity.x
            motion.y = velocity.y
            motion.z = velocity.z

            // Invert and dampen rotation energy upon rigid impact structures
            spinY *= -0.25
            val epsilon = 0.002

            // Vertical structural wall boundaries (North, South, East, West)
            if (abs(normal.x) > 0.1 || abs(normal.z) > 0.1) {
                position.x = targetPosition.x + (normal.x * epsilon)
                position.z = targetPosition.z + (normal.z * epsilon)
                position.y = targetPosition.y
            }
            // Horizontal surface structural configurations (Floor and Ceiling bounds)
            else if (abs(normal.y) > 0.1) {
                position.x = targetPosition.x
                position.z = targetPosition.z

                if (normal.y > 0.1) { // Ground Contact Hit (UP)
                    if (motion.y <= 0.0 || abs(motion.y) < (meta.physics.restVelocityThreshold * 4.0)) {
                        position.y = targetPosition.y - RAYTRACE_OFFSET
                        this.isOnGround = true
                        this.motion.y = 0.0
                        return
                    }
                }
                position.y = (targetPosition.y - RAYTRACE_OFFSET) + (normal.y * epsilon)
            }
        } else {
            // Re-zero collision counters if a clear structural flight line is achieved
            consecutiveBounceCount = 0

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

    /**
     * Processes structural angular step parameters per update cycle based on linear horizontal
     * velocities to produce progressive visual models.
     */
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

    /**
     * Safety recovery fallback engine that forces a step reset if an unresolvable infinite geometry
     * looping trap condition is registered by consecutive loop structures.
     */
    private fun checkAndHandleStuckBall() {
        if (consecutiveBounceCount >= 10) {
            val ballLocation = getLocation()
            ballLocation.y += 1
            teleport(ballLocation)
            consecutiveBounceCount = 0
        }
    }
}