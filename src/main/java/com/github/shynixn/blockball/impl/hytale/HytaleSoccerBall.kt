package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.event.BallLeftClickEvent
import com.github.shynixn.blockball.event.BallRayTraceEvent
import com.github.shynixn.blockball.event.BallRightClickEvent
import com.github.shynixn.blockball.event.BallTouchPlayerEvent
import com.github.shynixn.htutils.plugin.HytaleWorldProxy
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.commonServer
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityTeleport
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityVelocity
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.entity.Entity
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.modules.physics.component.Velocity
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class HytaleSoccerBall(
    override val meta: SoccerBallMeta,
    entityId: Int,
    private var entity: Entity?,
    private val coroutineHandler: CoroutineHandler,
    private val initLocation: Location,
    private val game: SoccerGame?,
    private val rayTracingService: RayTracingService
) : SoccerBall {
    private var server = commonServer!!
    override var isDead: Boolean = false
    override val hitBoxEntityId: Int = entityId
    override val designEntityId: Int = entityId
    override var isOnGround: Boolean = false
    override var isInteractable: Boolean = true
    private val playerInteractionCoolDown = HashMap<Player, Long>()
    private var position: Vector3d = initLocation.toVector3d()
    private var motion = Vector3d(0.0, 0.0, 0.0)
    private var stuckCounter = 0
    private var cachedLength = 0.5

    /**
     * Origin coordinate to make relative rotations in the world.
     */
    private val origin = Vector3d(0.0, 0.0, -1.0).normalize()

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    private var angularVelocity: Double = 0.0

    /**
     * Remaining time until the players can interact with the ball again.
     */
    private var skipCounter = 20

    init {
        coroutineHandler.execute(coroutineHandler.fetchLocationDispatcher(initLocation)) {
            while (!isDead && entity?.reference != null) {
                tick(initLocation.world!!.players!!.map { e -> Pair(e, e.location) })
                delay(33)
            }
        }
    }


    override fun teleport(location: Location) {
        val ref = entity?.reference
        val store: Store<EntityStore?> = ref!!.getStore()
        val position = com.hypixel.hytale.math.vector.Vector3d(
            location.x, location.y, location.z
        )
        val rotation = Vector3f(
            location.yaw, location.pitch, 0.0f
        )
        val transform = Transform(
            position, rotation
        )

        val targetWorld = (location.world as HytaleWorldProxy).handle

        val teleport = Teleport.createForPlayer(
            targetWorld, transform
        )
        store.addComponent(
            ref,
            Teleport.getComponentType(),
            teleport
        )
    }

    private fun teleportWithoutY(location: Location){
        val ref = entity?.reference

        if(ref == null){
            return
        }

        val store: Store<EntityStore?> = ref!!.getStore()
        val oldTransform = store.getComponent(ref, TransformComponent.getComponentType())!!

        val position = com.hypixel.hytale.math.vector.Vector3d(
            location.x, oldTransform.position.y, location.z
        )
        val rotation = Vector3f(
            location.yaw, location.pitch, 0.0f
        )
        val transform = Transform(
            position, rotation
        )

        val targetWorld = (location.world as HytaleWorldProxy).handle

        val teleport = Teleport.createForPlayer(
            targetWorld, transform
        )
        store.addComponent(
            ref,
            Teleport.getComponentType(),
            teleport
        )
    }

    override fun getLocation(): Location {
        val ref = entity?.getReference()
        val store: Store<EntityStore?> = ref!!.getStore()
        val transform = store.getComponent(ref, TransformComponent.getComponentType())!!
        val position = transform.position
        val x = position.getX()
        val y = position.getY()
        val z = position.getZ()
        val rotation = transform.rotation
        val yaw = rotation.yaw
        val pitch = rotation.pitch
        return Location(HytaleWorldProxy(entity!!.world!!), x, y, z, yaw, pitch)
    }

    override fun getVelocity(): Vector {
        val ref = entity?.getReference()
        val store: Store<EntityStore?> = ref!!.getStore()
        val velocityComponent = store.getComponent(ref, Velocity.getComponentType())!!
        return Vector(
            velocityComponent.velocity.getX(),
            velocityComponent.velocity.getY(),
            velocityComponent.velocity.getZ()
        )
    }

    override fun kickByPlayer(player: Player) {
        if (isInCoolDown(player)) {
            return
        }

        if (skipCounter > 0) {
            return
        }

        if (!isInteractable) {
            return
        }

        // Referee and game player check.
        if (game != null && !game.redTeam.contains(player) && !game.blueTeam.contains(player)) {
            return
        }

        coroutineHandler.execute(coroutineHandler.fetchLocationDispatcher(initLocation)) {
            this.skipCounter = meta.hitbox.interactionCoolDownTicks
            executeKickPass(player,meta.shotVelocity, false)
        }
    }

    override fun passByPlayer(player: Player) {
    }

    override fun remove() {
        if (entity != null) {
            coroutineHandler.execute(coroutineHandler.fetchLocationDispatcher(initLocation)) {
                entity?.remove()
                entity = null
            }
        }
        playerInteractionCoolDown.clear()
    }


    private fun isInCoolDown(player: Player): Boolean {
        val currentMilliSeconds = System.currentTimeMillis()
        val timeStampOfLastHit = playerInteractionCoolDown[player]
        val milliSeconds = (meta.hitbox.interactionCoolDownPerPlayerTicks.toDouble() / 20.0 * 1000.0)

        if (timeStampOfLastHit != null) {
            if (currentMilliSeconds - timeStampOfLastHit < milliSeconds) {
                return true
            }
        }

        playerInteractionCoolDown[player] = currentMilliSeconds
        return false
    }


    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    suspend fun tick(players: List<Pair<Player, Location>>) {
        if (skipCounter > 0) {
            skipCounter--
        }

        checkMovementInteractions(players)
        val rayTraceResult =  rayTracingService.rayTraceMotion(
            position.toLocation().toVector3d(),
            motion.toVector().toVector3d(),
            false,
            true
        )

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        val rayTraceEvent = BallRayTraceEvent(
            this, rayTraceResult.hitBlock,
            rayTraceResult.targetPosition.toLocation(),
            rayTraceResult.blockDirection
        )
        commonServer!!.pluginManager.callEvent(rayTraceEvent)

        if (rayTraceEvent.isCancelled()) {
            return
        }

        val targetPosition = rayTraceEvent.targetLocation.toVector3d()

        if (rayTraceEvent.hitBlock) {
            if (rayTraceEvent.blockDirection == BlockDirection.UP) {
                this.stuckCounter = 0
                this.cachedLength = this.motion.length()
                calculateBallOnGround(players, targetPosition)
                return
            } else {
                stuckCounter++
                this.motion = calculateWallBounce(this.motion, rayTraceEvent.blockDirection)
                // Fix ball getting stuck in wall by moving back in the direction of its spawnpoint.
                if (stuckCounter > 4) {
                    val velocity =
                        this.initLocation.toVector3d().copy().subtract(this.position).normalize().multiply(cachedLength)
                    this.motion = velocity
                    this.motion.y = 0.1
                    this.position = this.position.add(this.motion.x, this.motion.y, this.motion.z)
                }

                // Correct the yaw of the ball after bouncing.
                this.position.yaw = getYawFromVector(origin, this.motion.copy().normalize()) * -1
                return
            }
        }

        this.stuckCounter = 0
        this.cachedLength = this.motion.length()
        calculateBallOnAir(players, targetPosition)
    }


    /**
     * Handles movement of the ball on ground.
     */
    private fun calculateBallOnGround(players: List<Pair<Player, Location>>, targetPosition: Vector3d) {
        targetPosition.y = this.position.y
        motion.y = 0.0
        this.position = targetPosition

        // Visible position makes the slime hitbox better align with the ball.
        val visiblePosition = position.toLocation()
        teleportWithoutY(visiblePosition)

        val rollingResistance = (1.0 - this.meta.rollingResistance)
        this.motion = this.motion.multiply(rollingResistance)
        this.isOnGround = true
    }

    /**
     * Handles movement of the ball in air.
     */
    private fun calculateBallOnAir(players: List<Pair<Player, Location>>, targetPosition: Vector3d) {
        val airResistance = 1.0 - this.meta.airResistance
        this.motion = this.motion.multiply(airResistance)
        this.motion.y -= this.meta.gravityModifier
        this.position = targetPosition
        this.isOnGround = false

        // Visible position makes the slime hitbox better align with the ball.
        teleportWithoutY(position.toLocation())

        // Handles angular velocity spinning in air.
        if (abs(angularVelocity) < 0.01) {
            return
        }

        val addVector = Vector3d(-motion.z, 0.0, motion.x).multiply(angularVelocity)
        this.motion = Vector3d(motion.x + addVector.x, motion.y, motion.z + addVector.z)
        angularVelocity /= 2
    }

    private fun setVelocity(){
        val ref = entity?.getReference()
        val store: Store<EntityStore?> = ref!!.getStore()
        val velocityComponent = store.getComponent(ref, Velocity.getComponentType())!!
        velocityComponent.addForce(motion.x, motion.y, motion.z)
    }

    /**
     * Checks movement interactions with the ball.
     */
    private fun checkMovementInteractions(players: List<Pair<Player, Location>>) {
        if (!meta.hitbox.touchEnabled || skipCounter > 0) {
            return
        }

        if (!isInteractable) {
            return
        }

        val hitboxSize = meta.hitbox.touchHitBoxSize

        for (player in players) {
            if (game != null && !game.redTeam.contains(player.first) && !game.blueTeam.contains(player.first)) {
                continue
            }

            val playerLocation = player.second.toVector3d()

            if (playerLocation.distance(position) < hitboxSize
            ) {
                val vector = position
                    .copy()
                    .subtract(playerLocation)
                    .normalize().multiply(meta.horizontalTouchModifier)
                vector.y = 0.1 * meta.verticalTouchModifier

                val ballTouchPlayerEvent = BallTouchPlayerEvent(this, player.first, vector.toVector())
               server.pluginManager.callEvent(ballTouchPlayerEvent)

                if (ballTouchPlayerEvent.isCancelled()) {
                    continue
                }

                // Correct the yaw of the ball after bouncing.
                this.position.yaw =
                    getYawFromVector(origin, ballTouchPlayerEvent.velocity.toVector3d().normalize()) * -1
                this.angularVelocity = 0.0
                // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
                this.position.y += 0.25
                this.motion = ballTouchPlayerEvent.velocity.toVector3d()
                this.skipCounter = meta.hitbox.interactionCoolDownTicks
            }
        }
    }

    /**
     * Executes the kick.
     */
    private fun executeKickPass(player: Player, baseMultiplier: Double, isPass: Boolean) {
        var kickVector = position
            .copy()
            .subtract(player.location.toVector3d())
            .normalize()
        val eyeLocation = player.eyeLocation.clone()
        val spinV = calculateSpinVelocity(eyeLocation.direction.toVector3d(), kickVector)

        val spinDrag = 1.0 - abs(spinV) / (3.0 * meta.maximumSpinVelocity)
        val angle =
            calculatePitchToLaunch(player.location.toVector3d(), eyeLocation.toVector3d())

        val verticalMod = baseMultiplier * spinDrag * sin(angle) * meta.shotPassYVelocityOverwrite
        val horizontalMod = baseMultiplier * spinDrag * cos(angle)
        kickVector = kickVector.normalize().multiply(horizontalMod)
        kickVector.y = verticalMod

        val event = if (isPass) {
            val event = BallRightClickEvent(this, player, kickVector.toVector())
           server.pluginManager.callEvent(event)
            Pair(event, event.velocity.toVector3d())
        } else {
            val event = BallLeftClickEvent(this, player, kickVector.toVector())
           server.pluginManager.callEvent(event)
            Pair(event, event.velocity.toVector3d())
        }

        if (!event.first.isCancelled()) {
            this.motion = event.second
            // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
            this.position.y += 0.25
            // Multiply the angular velocity by 2 to make it more visible.
            this.angularVelocity = spinV * 2
            // Correct the yaw of the ball after bouncing.
            this.position.yaw = getYawFromVector(origin, this.motion.copy().normalize()) * -1

            println("Motion" + this.motion)

        }
    }


    /**
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    private fun calculateSpinVelocity(
        postVector: com.github.shynixn.mcutils.common.Vector3d,
        initVector: com.github.shynixn.mcutils.common.Vector3d
    ): Double {
        val angle = Math.toDegrees(getHorizontalDeviation(initVector, postVector))
        val absAngle = abs(angle).toFloat()
        val maxV = meta.maximumSpinVelocity
        var velocity: Double

        velocity = when (absAngle < 90) {
            true -> maxV * absAngle / 90
            false -> maxV * (180 - absAngle) / 90
        }

        if (angle < 0.0) {
            velocity *= -1f
        }

        return velocity
    }

    /**
     * Calculates the pitch when launching the ball.
     * Result depends on the change of pitch. For example,
     * positive value implies that entity raised the pitch of its head.
     *
     * @param preLoc The eye location of entity before a certain event occurs
     * @param postLoc The eye location of entity after a certain event occurs
     * @return Angle measured in Radian
     */
    private fun calculatePitchToLaunch(
        preLoc: com.github.shynixn.mcutils.common.Vector3d,
        postLoc: com.github.shynixn.mcutils.common.Vector3d
    ): Double {
        val maximum = meta.maximumPitch
        val minimum = meta.minimumPitch
        val default = meta.defaultPitch

        if (default > maximum || default < minimum) {
            throw IllegalArgumentException("Default value must be in range of minimum and maximum!")
        }

        val delta = (preLoc.pitch - postLoc.pitch)
        val plusBasis = 90 + preLoc.pitch

        val result = when {
            (delta >= 0) -> default + (maximum - default) * delta / plusBasis
            else -> default + (default - minimum) * delta / (180 - plusBasis)
        }

        return Math.toRadians(result)
    }

    /**
     * Calculates the angle deviation between two vectors in X-Z dimension.
     * The angle never exceeds PI. If the calculated value is negative,
     * then subseq vector is actually not subsequent to precede vector.
     * @param subseq The vector subsequent to precede vector in clock-wised order.
     * @param precede The vector preceding subseq vector in clock-wised order.
     * @return A radian angle in the range of -PI to PI
     */
    private fun getHorizontalDeviation(
        subseq: com.github.shynixn.mcutils.common.Vector3d,
        precede: com.github.shynixn.mcutils.common.Vector3d
    ): Double {
        val s = subseq.normalize()
        val p = precede.normalize()
        val dot = s.x * p.x + s.z * p.z
        val det = s.x * p.z - s.z * p.x

        return atan2(det, dot)
    }

    /**
     * Calculates the outgoing vector from the incoming vector and the wall block direction.
     */
    private fun calculateWallBounce(
        incomingVector: com.github.shynixn.mcutils.common.Vector3d,
        blockDirection: BlockDirection
    ): com.github.shynixn.mcutils.common.Vector3d {
        val normalVector = when (blockDirection) {
            BlockDirection.WEST -> {
                Vector3d(-1.0, 0.0, 0.0)
            }

            BlockDirection.EAST -> {
                Vector3d(1.0, 0.0, 0.0)
            }

            BlockDirection.NORTH -> {
                Vector3d(0.0, 0.0, -1.0)
            }

            BlockDirection.SOUTH -> {
                Vector3d(0.0, 0.0, 1.0)
            }

            else -> if (blockDirection == BlockDirection.DOWN) {
                Vector3d(0.0, -1.0, 0.0)
            } else {
                Vector3d(0.0, 1.0, 1.0)
            }.normalize()
        }

        val radianAngle = 2 * incomingVector.dot(normalVector)
        val outgoingVector =
            incomingVector.copy().subtract(normalVector.multiply(radianAngle))

        return outgoingVector
    }

    /**
     * Gets the angle in degrees from 0 - 360 between the given 2 vectors.
     */
    private fun getYawFromVector(origin: Vector3d, position: Vector3d): Double {
        var angle = atan2(origin.z, origin.x) - atan2(position.z, position.x)
        angle = angle * 360 / (2 * Math.PI)

        if (angle < 0) {
            angle += 360.0
        }

        return angle
    }
}