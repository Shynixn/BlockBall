package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.contract.RayTracingService
import com.github.shynixn.blockball.entity.BallMeta
import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.enumeration.BlockDirection
import com.github.shynixn.blockball.event.BallLeftClickEvent
import com.github.shynixn.blockball.event.BallRightClickEvent
import com.github.shynixn.blockball.event.BallRayTraceEvent
import com.github.shynixn.blockball.event.BallTouchPlayerEvent
import com.github.shynixn.blockball.impl.extension.toLocation
import com.github.shynixn.blockball.impl.extension.toVector
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.packet.api.EntityType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.packet.*
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * LICENCE: This code in this file licenced differently and is not allowed to
 * be used commercially.
 */
class BallHitboxEntity(val entityId: Int) {
    /**
     * Origin coordinate to make relative rotations in the world.
     */
    private val origin = Position(0.0, 0.0, -1.0).normalize()

    /**
     * Remaining time until the players can interact with the ball again.
     */
    private var skipCounter = 20

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    private var angularVelocity: Double = 0.0

    /**
     * Position of the ball.
     */
    var position: Position = Position()

    /**
     * Motion of the ball.
     * Apply gravity at spawn to start with physic calculations.
     */
    var motion: Position = Position(0.0, -0.7, 0.0)

    /**
     * Request a teleport at next tick.
     */
    var requestTeleport: Boolean = false

    /**
     * Gets if the ball is on ground or in air.
     */
    var isOnGround: Boolean = false

    /**
     * Raytracing service dependency.
     */
    lateinit var rayTracingService: RayTracingService

    /**
     * Proxy packet dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Proxy dependency.
     */
    lateinit var proxyService: ProxyService

    /**
     * Ball Proxy.
     */
    lateinit var ball: BallCrossPlatformProxy

    lateinit var plugin: Plugin

    /**
     * Ball Meta.
     */
    val meta: BallMeta
        get() {
            return ball.meta
        }

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Any, position: Position) {
        require(player is Player)
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.entityId = entityId
            it.entityType = EntityType.SLIME
            it.target = position.toLocation()
        })

        if (meta.isSlimeVisible) {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.slimeSize = meta.kickPassHitBoxSize.toInt()
                it.entityId = entityId
            })
        } else {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.slimeSize = meta.kickPassHitBoxSize.toInt()
                it.entityId = entityId
                it.isInvisible = true
            })
        }
    }

    /**
     * Destroys the ball for the given player.
     */
    fun destroy(player: Player) {
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(entityId)
        })
    }

    /**
     * Kicks the hitbox for the given player interaction.
     */
    fun kickPlayer(player: Player, baseMultiplier: Double, isPass: Boolean) {
        if (skipCounter > 0) {
            return
        }

        val prevEyeLoc = proxyService.getPlayerEyeLocation<Any, Any>(player)
        this.skipCounter = meta.interactionCoolDown

        if (meta.kickPassDelay == 0) {
            executeKickPass(player, prevEyeLoc, baseMultiplier, isPass)
        } else {
            plugin.launch {
                delay(meta.kickPassDelay.ticks)
                executeKickPass(player, prevEyeLoc, baseMultiplier, isPass)
            }
        }
    }

    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun tick(players: List<Player>) {
        if (skipCounter > 0) {
            skipCounter--
        }

        if (requestTeleport) {
            requestTeleport = false

            // Visible position makes the slime hitbox better align with the ball.
            val visiblePosition = position.clone().add(0.0, -0.5, 0.0).add(0.0, meta.hitBoxRelocation, 0.0)
            for (player in players) {
                packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                    it.entityId = entityId
                    it.target = visiblePosition.toLocation()
                })
            }

            motion = Position(0.0, -0.7, 0.0)
            return
        }

        checkMovementInteractions(players)

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        val rayTraceResult = rayTracingService.rayTraceMotion(position, motion)

        val rayTraceEvent = BallRayTraceEvent(
            ball, rayTraceResult.hitBlock,
            proxyService.toLocation(rayTraceResult.targetPosition),
            rayTraceResult.blockdirection
        )
        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        if (rayTraceEvent.isCancelled) {
            return
        }

        val targetPosition = proxyService.toPosition(rayTraceEvent.targetLocation)

        if (rayTraceEvent.hitBlock) {
            if (rayTraceEvent.blockDirection == BlockDirection.UP) {
                calculateBallOnGround(players, targetPosition)
                return
            } else {
                this.motion = calculateWallBounce(this.motion, rayTraceEvent.blockDirection)
                // Correct the yaw of the ball after bouncing.
                this.position.yaw = getYawFromVector(origin, this.motion.clone().normalize()) * -1
                return
            }
        }

        calculateBallOnAir(players, targetPosition)
    }

    /**
     * Executes the kick.
     */
    private fun executeKickPass(player: Player, prevEyeLoc: Any, baseMultiplier: Double, isPass: Boolean) {
        var kickVector = proxyService.getLocationDirection(prevEyeLoc)
        val eyeLocation = proxyService.getPlayerEyeLocation<Any, Any>(player)
        val spinV = calculateSpinVelocity(proxyService.getLocationDirection(eyeLocation), kickVector)

        val spinDrag = 1.0 - abs(spinV) / (3.0 * meta.movementModifier.maximumSpinVelocity)
        val angle =
            calculatePitchToLaunch(proxyService.toPosition(prevEyeLoc), proxyService.toPosition(eyeLocation))

        val verticalMod = baseMultiplier * spinDrag * sin(angle)
        val horizontalMod = baseMultiplier * spinDrag * cos(angle)
        kickVector = kickVector.normalize().multiply(horizontalMod)
        kickVector.y = verticalMod

        val event = if (isPass) {
            val event = BallRightClickEvent(ball, player, proxyService.toVector(kickVector))
            Bukkit.getPluginManager().callEvent(event)
            Pair(event, proxyService.toPosition(event.velocity))
        } else {
            val event = BallLeftClickEvent(ball, player, proxyService.toVector(kickVector))
            Bukkit.getPluginManager().callEvent(event)
            Pair(event, proxyService.toPosition(event.velocity))
        }

        if (!event.first.isCancelled) {
            this.motion = event.second
            // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
            this.position.y += 0.25
            // Multiply the angular velocity by 2 to make it more visible.
            this.angularVelocity = spinV * 2
            // Correct the yaw of the ball after bouncing.
            this.position.yaw = getYawFromVector(origin, this.motion.clone().normalize()) * -1
        }
    }

    /**
     * Checks movement interactions with the ball.
     */
    private fun checkMovementInteractions(players: List<Player>) {
        if (!meta.enabledInteract || skipCounter > 0) {
            return
        }

        // Reduce hitbox size in order to stay compatible to old arena files.
        val hitboxSize = (meta.interactionHitBoxSize - 1)

        for (player in players) {
            val playerLocation = proxyService.toPosition(proxyService.getEntityLocation<Any, Any>(player))

            if (proxyService.getPlayerGameMode(player) != GameMode.SPECTATOR &&
                playerLocation.distance(position) < hitboxSize
            ) {
                val vector = position
                    .clone()
                    .subtract(playerLocation)
                    .normalize().multiply(meta.movementModifier.horizontalTouchModifier)
                vector.y = 0.1 * meta.movementModifier.verticalTouchModifier

                val ballTouchPlayerEvent = BallTouchPlayerEvent(ball, player, proxyService.toVector(vector))
                Bukkit.getPluginManager().callEvent(ballTouchPlayerEvent)

                if (ballTouchPlayerEvent.isCancelled) {
                    continue
                }

                // Correct the yaw of the ball after bouncing.
                this.position.yaw =
                    getYawFromVector(origin, proxyService.toPosition(ballTouchPlayerEvent.velocity).normalize()) * -1
                this.angularVelocity = 0.0
                // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
                this.position.y += 0.25
                this.motion = proxyService.toPosition(ballTouchPlayerEvent.velocity)
                this.skipCounter = meta.interactionCoolDown
            }
        }
    }

    /**
     * Handles movement of the ball on ground.
     */
    private fun calculateBallOnGround(players: List<Any>, targetPosition: Position) {
        targetPosition.y = this.position.y
        motion.y = 0.0

        for (player in players) {
            require(player is Player)
            packetService.sendPacketOutEntityVelocity(player, PacketOutEntityVelocity().also {
                it.entityId = entityId
                it.target = motion.toVector()
            })
        }

        this.position = targetPosition

        // Visible position makes the slime hitbox better align with the ball.
        val visiblePosition = position.clone().add(0.0, -0.5, 0.0).add(0.0, meta.hitBoxRelocation, 0.0)
        for (player in players) {
            require(player is Player)
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = entityId
                it.target = visiblePosition.toLocation()
            })
        }

        val rollingResistance = 1.0 - this.meta.movementModifier.rollingResistance
        this.motion = this.motion.multiply(rollingResistance)
        this.isOnGround = true
    }

    /**
     * Handles movement of the ball in air.
     */
    private fun calculateBallOnAir(players: List<Any>, targetPosition: Position) {
        val airResistance = 1.0 - this.meta.movementModifier.airResistance
        this.motion = this.motion.multiply(airResistance)
        this.motion.y -= this.meta.movementModifier.gravityModifier
        this.position = targetPosition
        this.isOnGround = false

        // Visible position makes the slime hitbox better align with the ball.
        val visiblePosition = position.clone().add(0.0, -0.5, 0.0).add(0.0, meta.hitBoxRelocation, 0.0)
        for (player in players) {
            require(player is Player)
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = entityId
                it.target = visiblePosition.toLocation()
            })
        }

        // Handles angular velocity spinning in air.
        if (abs(angularVelocity) < 0.01) {
            return
        }

        val addVector = Position(-motion.z, 0.0, motion.x).multiply(angularVelocity)
        this.motion = Position(motion.x + addVector.x, motion.y, motion.z + addVector.z)
        angularVelocity /= 2
    }

    /**
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    private fun calculateSpinVelocity(postVector: Position, initVector: Position): Double {
        val angle = Math.toDegrees(getHorizontalDeviation(initVector, postVector))
        val absAngle = abs(angle).toFloat()
        val maxV = meta.movementModifier.maximumSpinVelocity
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
    private fun calculatePitchToLaunch(preLoc: Position, postLoc: Position): Double {
        val maximum = meta.movementModifier.maximumPitch
        val minimum = meta.movementModifier.minimumPitch
        val default = meta.movementModifier.defaultPitch

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
    private fun getHorizontalDeviation(subseq: Position, precede: Position): Double {
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
        incomingVector: Position,
        blockDirection: BlockDirection
    ): Position {
        val normalVector = when (blockDirection) {
            BlockDirection.WEST -> {
                Position(-1.0, 0.0, 0.0)
            }
            BlockDirection.EAST -> {
                Position(1.0, 0.0, 0.0)
            }
            BlockDirection.NORTH -> {
                Position(0.0, 0.0, -1.0)
            }
            BlockDirection.SOUTH -> {
                Position(0.0, 0.0, 1.0)
            }
            else -> if (blockDirection == BlockDirection.DOWN) {
                Position(0.0, -1.0, 0.0)
            } else {
                Position(0.0, 1.0, 1.0)
            }.normalize()
        }

        val radianAngle = 2 * incomingVector.dot(normalVector)
        val outgoingVector =
            incomingVector.clone().subtract(normalVector.multiply(radianAngle))

        return outgoingVector
    }

    /**
     * Gets the angle in degrees from 0 - 360 between the given 2 vectors.
     */
    private fun getYawFromVector(origin: Position, position: Position): Double {
        var angle = atan2(origin.z, origin.x) - atan2(position.z, position.x)
        angle = angle * 360 / (2 * Math.PI)

        if (angle < 0) {
            angle += 360.0
        }

        return angle
    }
}
