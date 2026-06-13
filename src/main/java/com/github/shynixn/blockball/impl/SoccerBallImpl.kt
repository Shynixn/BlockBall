package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import com.github.shynixn.blockball.enumeration.BallInputActionType
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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Implementation of the SoccerBall contract representing a physics-based ball entity in the game.
 */
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
        /**
         * The attribute identifier used for scaling entities in Minecraft.
         */
        const val SCALE: String = "scale"

        /**
         * Vertical buffer offset used during ray-tracing calculations to keep the point-mass
         * representation from slipping through solid block geometry over continuous steps.
         */
        const val RAYTRACE_OFFSET = 0.2
    }

    /**
     * The current 3D position of the ball.
     */
    private var position: Vector3d = location.toVector3d()

    /**
     * The current velocity vector of the ball.
     */
    private var motion: Vector = Vector(0.0, -0.7, 0.0)

    /**
     * The current visual rotation of the ball in degrees.
     */
    private var rotationDegrees: Double = 0.0

    /**
     * The current horizontal spin applied to the ball.
     */
    private var spinY: Double = 0.0

    /**
     * The last recorded directional yaw of the ball.
     */
    private var lastYaw: Float = 0.0F

    /**
     * Tracker for consecutive physics bounces used to detect stuck states.
     */
    private var consecutiveBounceCount: Int = 0

    /**
     * Tracker for managing player visibility and network packets relative to the ball.
     */
    private val playerTracker = GameObjectPlayerTracker(
        meta.render.renderDistance,
        { p -> spawnEntityForPlayer(p) },
        { p -> removeEntityForPlayer(p) }
    )

    /**
     * Cooldown timers mapped per player to throttle interaction rates.
     */
    private val perPlayerCooldown = HashMap<Player, GameObjectCooldownTimer>()

    /**
     * Global cooldown timer to throttle total continuous interactions with the ball.
     */
    private val globalCooldown = GameObjectCooldownTimer(meta.physics.globalInteractionCooldownTicks * 50)

    /**
     * Interval timer used to regulate how frequently player positions are polled.
     */
    private val playerFetchTimer = GameObjectIntervalTimer(meta.physics.fetchPlayerPositionsIntervalTicks * 50)

    /**
     * Indicates whether the ball has been removed or destroyed.
     */
    override var isDead: Boolean = false

    /**
     * Indicates whether the ball is currently contacting the ground.
     */
    override var isOnGround: Boolean = false

    /**
     * The player currently holding or grabbing the ball, if any.
     */
    override val grabbingPlayer: Player? = null

    /**
     * Indicates whether players can interact with the ball.
     */
    override var isInteractable: Boolean = true

    /**
     * A specific player that has exclusive locking rights to interact with this ball.
     */
    override var lockedPlayer: Player? = null

    /**
     * Instantly relocates the ball to a target destination and clears rotational energy.
     */
    override fun teleport(location: Location) {
        this.position = location.toVector3d()
        this.spinY = 0.0
    }

    /**
     * Retrieves the current location of the ball.
     */
    override fun getLocation(): Location {
        return position.toLocation()
    }

    /**
     * Retrieves a copy of the current velocity vector of the ball.
     */
    override fun getVelocity(): Vector {
        return motion.clone()
    }

    /**
     * Sets the linear velocity of the ball while clearing its rotational spin.
     */
    override fun setVelocity(velocity: Vector) {
        setVelocity(velocity, Vector(0.0, 0.0, 0.0))
    }

    /**
     * Explicitly updates the linear motion and vertical angular spin axes of the entity.
     * Must be called synchronized on the primary server thread.
     */
    override fun setVelocity(velocity: Vector, spin: Vector) {
        this.motion = velocity.clone()
        this.spinY = spin.y
        this.isOnGround = false
    }

    /**
     * Evaluates thread and gameplay constraints before routing player inputs into action impulses.
     */
    override fun applyInteraction(player: Player, ballInputActionType: BallInputActionType) {
        if (!Bukkit.isPrimaryThread()) {
            throw IllegalArgumentException("Thread violation!")
        }

        if (!canPlayerInteractWithBall(player)) {
            return
        }

        applyInteractionToBall(player, ballInputActionType)
    }

    /**
     * Destroys virtual entity registrations and clears them from all active client-side view contexts.
     */
    override fun remove() {
        if (!Bukkit.isPrimaryThread()) {
            throw IllegalArgumentException("Thread violation!")
        }

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
     * Maps user action context to configured execution profiles, computes multi-axis impulse
     * distributions modified by ball mass constraints, and commits the resulting physics shifts.
     */
    private fun applyInteractionToBall(player: Player, ballInputActionType: BallInputActionType) {
        val interactionMeta = findPlayerAction(player, ballInputActionType)
        if (interactionMeta == null) {
            return
        }

        val ballTriggerActionEvent =
            BallActionEvent(this, player, interactionMeta.executionType, interactionMeta.triggerType)
        Bukkit.getPluginManager().callEvent(ballTriggerActionEvent)
        if (ballTriggerActionEvent.isCancelled) {
            // Reset the global cooldown if the event was canceled to prevent locked states
            globalCooldown.reset()
            return
        }

        // Grab directional looking coordinates to determine forward flat impulse values
        val playerLocation = player.location
        val yawDegrees = playerLocation.yaw.toDouble()
        val yawRadians = Math.toRadians(yawDegrees)

        // Protect against zero or negative mass definitions producing infinite values
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)
        val horizontalForce = interactionMeta.horizontalImpulse / ballMass
        val verticalForce = interactionMeta.verticalImpulse / ballMass

        // Distribute planar forces flatly across coordinate offsets using standard trigonometry
        val x = horizontalForce * -sin(yawRadians)
        val z = horizontalForce * cos(yawRadians)

        // Assign linear velocity adjustments along with horizontal aerodynamic spin components
        val calculatedVelocity = Vector(x, verticalForce, z)
        this.setVelocity(calculatedVelocity, Vector(0.0, interactionMeta.spinImpulse / ballMass, 0.0))

        // Play particle visual effects if configured
        val effect = particleEffectService.getEffectMetaFromName(interactionMeta.effectName)
        if (effect != null) {
            particleEffectService.startEffect(effect, { getLocation() }, null, null)
        }
    }

    /**
     * Scans proximity profiles of tracked players to execute automatic collision contacts (touches)
     * when a player breaks inside the structural bounds of the ball configuration.
     */
    private fun checkPlayerTouchInteractions() {
        val playerLocationPairs = HashSet(playerTracker.cache.entries)
        val hitboxSize = meta.physics.collisionBoundsSize
        val ballLocation = getLocation()

        // Track closest valid player violating interaction boundaries
        val playerHittingTheBall = playerLocationPairs.asSequence()
            .map { e -> Pair(e.key, e.value.distance(ballLocation)) }
            .filter { p -> p.second < hitboxSize }
            .sortedBy { e -> e.second }
            .firstOrNull { e -> canPlayerInteractWithBall(e.first) }

        if (playerHittingTheBall != null) {
            applyInteractionToBall(playerHittingTheBall.first, BallInputActionType.COLLIDE)
        }
    }

    /**
     * Despawns virtual tracked packets from a targeted player's game client.
     */
    private fun removeEntityForPlayer(player: Player) {
        // Instruct network pipelines to clear both collision and rendering objects instantly
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(hitBoxEntityId, renderEntityId)
        })
    }

    /**
     * Generates and transmits virtual packet movements, client-side metadata updates, and structural
     * roll rotations to all players within the visualization distance threshold.
     */
    private fun updateEntityForAllPlayers() {
        // Enforce fallback yaw properties if the ball is entirely stationary
        val yaw = if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            lastYaw
        } else {
            vectorToYaw(getVelocity())
        }

        // Adjust spatial offsets for visual models
        val renderLocation = getLocation()
        renderLocation.y += meta.render.visualVerticalOffset
        renderLocation.yaw = yaw

        // Adjust spatial offsets for physical interactable bounds
        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset
        hitBoxLocation.yaw = yaw

        // Process packets down to all localized tracking view clients
        val players = HashSet(playerTracker.cache.keys)
        for (player in players) {
            // Relocate the display armor stand object
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = renderEntityId
                it.target = renderLocation
            })

            // Perform armor stand head adjustments to visually simulate rolling rotations
            if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
                packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                    it.armorStandHeadRotation = EulerAngle(-1 * rotationDegrees, 0.0, 0.0)
                    it.entityId = renderEntityId
                })
            }

            // Relocate tracking physical interactive collision bounding box
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

        // Establish core baseline base armor stand visual model objects
        renderLocation.y += meta.render.visualVerticalOffset
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.target = renderLocation
            it.entityId = renderEntityId
            it.entityType = EntityType.ARMOR_STAND
        })

        // Apply custom textured items over helmet item slots if slime parameters are disabled
        if (!meta.render.slimeVisible) {
            val stack = itemService.toItemStack(meta.render.visualItem)
            packetService.sendPacketOutEntityEquipment(player, PacketOutEntityEquipment().also {
                it.entityId = renderEntityId
                it.items = listOf(Pair(ArmorSlotType.HELMET, stack))
            })
        }

        // Apply model scaling modifications matching modern server version configurations
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
            // Fall back onto small armor stand sizing structures on historical engines
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = renderEntityId
                it.isInvisible = true
                it.isArmorstandSmall = meta.render.modelScale == 0.5
            })
        }

        val hitBoxLocation = getLocation()
        hitBoxLocation.y += meta.physics.verticalOffset

        // Generate physical interactive boundaries (Modern Interaction entities vs legacy Slimes)
        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_19_R3)) {
            packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
                it.entityId = hitBoxEntityId
                it.entityType = EntityType.INTERACTION
                it.target = hitBoxLocation
            })
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = hitBoxEntityId
                it.interactionMetadata = InteractionMetadata().also { metaData ->
                    metaData.height = meta.physics.interactionBoundsSize
                    metaData.width = meta.physics.interactionBoundsSize
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
                if (!meta.render.slimeVisible) {
                    it.isInvisible = true
                }
            })
        }
    }

    /**
     * Assesses interaction state and tracks personal/global throttling cooldown thresholds
     * to safeguard the physics thread from spam input corruption exploits.
     */
    private fun canPlayerInteractWithBall(player: Player): Boolean {
        // Fail if ball settings explicitly lock all public user interaction mechanics
        if (!isInteractable) {
            return false
        }
        // Fail if an exclusive player lock profile is allocated to another player
        if (lockedPlayer != null && player != lockedPlayer) {
            return false
        }
        // Check if global anti-spam threshold parameters are currently active
        if (!globalCooldown.canExecute()) {
            return false
        }

        // Initialize personal cooldown tracking structures if none exist for this player
        var playerCooldown = perPlayerCooldown[player]
        if (playerCooldown == null) {
            playerCooldown = GameObjectCooldownTimer(meta.physics.perPlayerInteractionCooldownTicks * 50)
            perPlayerCooldown[player] = playerCooldown
        }

        // Check if individual player input limits are violated
        if (!playerCooldown.canExecute()) {
            return false
        }

        // Lock cooldown buckets to process this transaction frame safely
        globalCooldown.execute()
        playerCooldown.execute()
        return true
    }

    /**
     * Evaluates a player's physical context (sneaking, sprinting, jumping, hotbar selections)
     * against asset maps to determine the matching interaction target profile.
     */
    private fun findPlayerAction(player: Player, ballInputActionType: BallInputActionType): SoccerBallMeta.InteractionMeta? {
        // Map dynamic locomotion context sets into contextual prioritized arrays
        val triggerTypes = when {
            ballInputActionType == BallInputActionType.LEFT_CLICK && player.isSneaking -> arrayOf(
                BallTriggerActionType.SNEAK_LEFT_CLICK,
                BallTriggerActionType.LEFT_CLICK
            )

            ballInputActionType == BallInputActionType.LEFT_CLICK && player.isSprinting -> arrayOf(
                BallTriggerActionType.SPRINT_LEFT_CLICK,
                BallTriggerActionType.LEFT_CLICK
            )

            ballInputActionType == BallInputActionType.LEFT_CLICK && !player.isOnGround -> arrayOf(
                BallTriggerActionType.JUMP_LEFT_CLICK,
                BallTriggerActionType.LEFT_CLICK
            )

            ballInputActionType == BallInputActionType.RIGHT_CLICK && player.isSneaking -> arrayOf(
                BallTriggerActionType.SNEAK_RIGHT_CLICK,
                BallTriggerActionType.RIGHT_CLICK
            )

            ballInputActionType == BallInputActionType.RIGHT_CLICK && player.isSprinting -> arrayOf(
                BallTriggerActionType.SPRINT_RIGHT_CLICK,
                BallTriggerActionType.RIGHT_CLICK
            )

            ballInputActionType == BallInputActionType.RIGHT_CLICK && !player.isOnGround -> arrayOf(
                BallTriggerActionType.JUMP_RIGHT_CLICK,
                BallTriggerActionType.RIGHT_CLICK
            )

            ballInputActionType == BallInputActionType.LEFT_CLICK -> arrayOf(BallTriggerActionType.LEFT_CLICK)
            ballInputActionType == BallInputActionType.RIGHT_CLICK -> arrayOf(BallTriggerActionType.RIGHT_CLICK)
            ballInputActionType == BallInputActionType.COLLIDE && player.isSneaking -> arrayOf(
                BallTriggerActionType.SNEAK_COLLIDE,
                BallTriggerActionType.COLLIDE
            )

            ballInputActionType == BallInputActionType.COLLIDE && player.isSprinting -> arrayOf(
                BallTriggerActionType.SPRINT_COLLIDE,
                BallTriggerActionType.COLLIDE
            )

            ballInputActionType == BallInputActionType.COLLIDE && !player.isOnGround -> arrayOf(
                BallTriggerActionType.JUMP_COLLIDE,
                BallTriggerActionType.COLLIDE
            )

            ballInputActionType == BallInputActionType.COLLIDE -> arrayOf(BallTriggerActionType.COLLIDE)
            else -> emptyArray()
        }

        // Loop over resolved array keys matching configuration limits against active inventory item ranges
        val itemSlot = player.inventory.heldItemSlot
        return triggerTypes.firstNotNullOfOrNull { type ->
            meta.interactions.firstOrNull { e ->
                e.triggerType == type && itemSlot >= e.conditionHotBarRangeStart && itemSlot <= e.conditionHotBarRangeEnd
            }
        }
    }

    /**
     * Direct Translation vector converter yielding angular horizontal degrees mapped to
     * standard Minecraft specifications (-180 to 180 Range).
     */
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

    /**
     * Simulates continuous external physics over custom time steps. Tracks ground contact profiles,
     * executes Magnus aerodynamic curved shots logic, calculates dampening frictions, and resolves
     * rest thresholds.
     */
    private fun calculatePhysics(deltaMs: Int) {
        val tickScale = (deltaMs / 50.0).coerceAtMost(2.0)
        val ballMass = meta.physics.mass.coerceAtLeast(0.01)

        // Verify that ground attachment profiles remain valid via downward trace rays
        if (isOnGround) {
            val groundProbe = Vector(0.0, -0.05, 0.0)
            val groundCheck = rayTracingService.rayTrace(
                position.toLocation(),
                groundProbe,
                groundProbe.length().coerceAtLeast(0.01),
                false,
                false
            )
            if (!groundCheck.hasHitBlock) {
                isOnGround = false
            } else {
                // Dampen rotation quickly due to contact friction on solid terrain surfaces
                spinY *= (1.0 - meta.physics.rollingFriction * 2.0 * tickScale).coerceIn(0.0, 1.0)
            }
        }

        val velocity = Vector(motion.x, motion.y, motion.z)

        // Aerodynamic Magnus Effect Simulation for curved shots
        if (!isOnGround && velocity.lengthSquared() > 0.01 && abs(spinY) > 0.01) {
            val spinVector = Vector(0.0, spinY, 0.0)
            val curveForce = velocity.clone().crossProduct(spinVector)
            curveForce.multiply(meta.physics.curveMultiplier * tickScale)
            velocity.add(curveForce)
        }

        // Apply rolling friction modifiers vs airborne resistance drag updates
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

        // Gradually decay angular spinning velocity over time
        if (abs(spinY) > 0.001) {
            spinY *= (1.0 - meta.physics.spinDrag * tickScale).coerceIn(0.0, 1.0)
        } else {
            spinY = 0.0
        }

        // Commit resolved calculations back to primary motion metrics
        motion.x = velocity.x
        motion.y = velocity.y
        motion.z = velocity.z

        // Track model rotations based on current speed factors
        if (meta.render.rotationEnabled && !meta.render.slimeVisible) {
            calculateNextRotation()
        }

        // Sync directional view orientations if significant motion forces are active
        if (abs(motion.x) > 0.001 || abs(motion.z) > 0.001) {
            this.lastYaw = vectorToYaw(motion)
        }

        // Stop the ball completely if energy drops below configured minimum thresholds
        if (isOnGround && abs(motion.x) < meta.physics.restVelocityThreshold && abs(motion.z) < meta.physics.restVelocityThreshold) {
            motion.x = 0.0
            motion.y = 0.0
            motion.z = 0.0
            spinY = 0.0
            return
        }

        // Skip trace checks entirely if velocity is completely zeroed out
        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        // Evaluate physical pathing collisions via ray-tracing service steps
        rayTrace(position, motion)

        // Prevent mathematical micro-bounces near surfaces by snapping down safely
        if (!isOnGround && motion.y < 0.0 && abs(motion.y) < meta.physics.restVelocityThreshold) {
            val groundProbe = Vector(0.0, -0.5, 0.0)
            val groundResult = rayTracingService.rayTrace(
                position.toLocation(),
                groundProbe,
                groundProbe.length().coerceAtLeast(0.01),
                false,
                false
            )
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

        // Issue bounding inquiries down into block collision maps
        val rayTraceResult = rayTracingService.rayTrace(
            rayTraceStartPosition.toLocation(),
            motion.clone(),
            maxTraceDistance,
            false,
            false
        )

        // Fire custom tracking events representing raw raytrace calculations
        val rayTraceEvent =
            BallRayTraceEvent(this, rayTraceResult.hasHitBlock, rayTraceResult.targetLocation, rayTraceResult.blockFace)
        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        val targetPosition = rayTraceResult.targetLocation
        val hasHitBlock = rayTraceResult.hasHitBlock
        val blockDirectionHit = rayTraceResult.blockFace

        // Process geometric bounce calculations if a block collision interface is hit
        if (hasHitBlock) {
            consecutiveBounceCount++

            val normalX = blockDirectionHit!!.modX.toDouble()
            val normalY = blockDirectionHit.modY.toDouble()
            val normalZ = blockDirectionHit.modZ.toDouble()

            val velocity = Vector(motion.x, motion.y, motion.z)
            val normal = Vector(normalX, normalY, normalZ)
            val dot = velocity.dot(normal)

            // Reflect the velocity vector over the surface normal vector combined with bounciness properties
            velocity.subtract(normal.clone().multiply((1.0 + meta.physics.bounciness) * dot))

            motion.x = velocity.x
            motion.y = velocity.y
            motion.z = velocity.z

            // Invert and reverse spin properties due to physical energy dispersion on surface impact
            spinY *= -0.25
            val epsilon = 0.002

            // Adjust coordinates slightly outward using epsilon offsets to prevent phase-shifting through blocks
            if (abs(normal.x) > 0.1 || abs(normal.z) > 0.1) {
                position.x = targetPosition.x + (normal.x * epsilon)
                position.z = targetPosition.z + (normal.z * epsilon)
                position.y = targetPosition.y
            } else if (abs(normal.y) > 0.1) {
                position.x = targetPosition.x
                position.z = targetPosition.z

                // Handle floor collisions specifically
                if (normal.y > 0.1) {
                    // Snap the ball down onto the terrain layer if vertical velocity drops low enough
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
            // Reset bounce counters if a clean unobstructed trajectory path segment is executed
            consecutiveBounceCount = 0

            position.x = targetPosition.x
            position.z = targetPosition.z

            // Progress tracking coordinates across standard displacement variables
            if (motion.y == 0.0 && isOnGround) {
                position.y = originalY
            } else {
                position.y = originalY + motion.y
            }

            // Flag as airborne if vertical changes occur
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

        // Calculate dynamic incremental pitch updates matching movement speeds
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