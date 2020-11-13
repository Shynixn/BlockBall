package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.bukkit.event.BallInteractEvent
import com.github.shynixn.blockball.api.bukkit.event.BallKickEvent
import com.github.shynixn.blockball.api.bukkit.event.BallRayTraceEvent
import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toVector
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class BallHitboxEntity(val entityId: Int, var position: Position, private val meta: BallMeta) {
    private val origin = PositionEntity(0.0, 0.0, -1.0).normalize()

    /**
     * Proxy service dependency.
     */
    lateinit var proxyService: ProxyService

    /**
     * Proxy packet dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Concurrency dependency.
     */
    lateinit var concurrencyService: ConcurrencyService

    /**
     * Ball Proxy.
     */
    lateinit var ball: BallCrossPlatformProxy

    /**
     * Motion of the ball.
     * Apply gravity at spawn to start with physic calculations.
     */
    var motion: Position = PositionEntity(0.0, -0.7, 0.0)

    /**
     * Request a teleport at next tick.
     */
    var requestTeleport: Boolean = false

    /**
     * Gets if the ball is on ground or in air.
     */
    var isOnGround: Boolean = false

    /**
     * Remaining time in ticks until players regain the ability to kick this ball.
     */
    private var skipKickCounter: Int = 0

    /**
     * Remaining time until the players can run into the ball again.
     */
    private var skipCounter = 20

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    private var angularVelocity: Double = 0.0

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Player, position: Position) {
        packetService.sendEntitySpawnPacket(player, entityId, "SLIME", position)
    }

    /**
     * Destroys the ball for the given player.
     */
    fun destroy(player: Player) {
        packetService.sendEntityDestroyPacket(player, entityId)
    }

    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun <P> tick(players: List<P>) {
        if (skipKickCounter > 0) {
            skipKickCounter--
        }

        if (skipCounter > 0) {
            skipCounter--
        }

        if (requestTeleport) {
            requestTeleport = false

            for (player in players) {
                packetService.sendEntityTeleportPacket(player, entityId, position)
            }

            motion = PositionEntity(0.0, -0.7, 0.0)
            return
        }

        checkMovementInteractions(players as List<Player>)

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        val rayTraceResult = proxyService.rayTraceMotion(position, motion)

        val rayTraceEvent = BallRayTraceEvent(
            ball,
            rayTraceResult.hitBlock,
            rayTraceResult.targetPosition,
            rayTraceResult.blockdirection
        )

        Bukkit.getPluginManager().callEvent(rayTraceEvent)

        if (rayTraceEvent.hitBlock) {
            if (rayTraceEvent.blockDirection == BlockDirection.UP) {
                calculateBallOnGround(players, rayTraceEvent.targetPosition)
                return
            } else {
                this.motion = calculateWallBounce(this.motion, rayTraceEvent.blockDirection)
                return
            }
        }

        calculateBallOnAir(players, rayTraceEvent.targetPosition)
    }

    /**
     * Handles movement of the ball on ground.
     */
    private fun calculateBallOnGround(players: List<Player>, targetPosition: Position) {
        targetPosition.y = this.position.y
        motion.y = 0.0

        for (player in players) {
            packetService.sendEntityVelocityPacket(player, entityId, motion)
            packetService.sendEntityMovePacket(player, entityId, this.position, targetPosition)
        }

        this.position = targetPosition
        for (player in players) {
            packetService.sendEntityTeleportPacket(player, entityId, this.position)
        }

        val rollingResistance = 1.0 - this.meta.movementModifier.rollingResistance
        this.motion = this.motion.multiply(rollingResistance)
        this.isOnGround = true
    }

    /**
     * Handles movement of the ball in air.
     */
    private fun calculateBallOnAir(players: List<Player>, targetPosition: Position) {
        val airResistance = 1.0 - this.meta.movementModifier.airResistance
        this.motion = this.motion.multiply(airResistance)
        this.motion.y -= this.meta.movementModifier.gravityModifier
        this.position = targetPosition

        for (player in players) {
            packetService.sendEntityTeleportPacket(player, entityId, this.position)
        }

        this.isOnGround = false
    }

    /**
     * Kicks the hitbox for the given player interaction.
     */
    fun kickPlayer(player: Player, delay: Int, baseMultiplier: Double) {
        if (skipKickCounter > 0) {
            return
        }

        val preEvent = BallInteractEvent(player, this.ball)
        Bukkit.getPluginManager().callEvent(preEvent)
        if (preEvent.isCancelled) {
            return
        }

        val prevEyeLoc = player.eyeLocation.clone()
        this.skipCounter = delay + 20
        this.skipKickCounter = delay + 20
        this.motion = player.velocity.toPosition()

        sync(concurrencyService, delay.toLong()) {
            var kickVector = prevEyeLoc.direction.toPosition()
            val eyeLocation = player.eyeLocation
            val spinV = calculateSpinVelocity(eyeLocation.direction.toPosition(), kickVector)

            val spinDrag = 1.0 - abs(spinV) / (3.0 * meta.movementModifier.maximumSpinVelocity)
            val angle = calculatePitchToLaunch(prevEyeLoc, eyeLocation)

            val verticalMod = baseMultiplier * spinDrag * sin(angle)
            val horizontalMod = baseMultiplier * spinDrag * cos(angle)
            kickVector = kickVector.normalize().multiply(horizontalMod)
            kickVector.y = verticalMod

            val event = BallKickEvent(kickVector.toVector(), player, this.ball)
            Bukkit.getPluginManager().callEvent(event)

            if (!event.isCancelled) {
                // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
                this.position.y += 0.25
                this.motion = event.resultVelocity.toPosition()
                this.angularVelocity = spinV

                if (this.meta.rotating) {
                    ball.rotation = PositionEntity(2.0, 0.0, 0.0)
                }
            }
        }
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
    private fun calculatePitchToLaunch(preLoc: Location, postLoc: Location): Double {
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

        return Math.toRadians(result.toDouble())
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
     * Checks movement interactions with the ball.
     */
    private fun checkMovementInteractions(players: List<Player>) {
        if (!meta.enabledInteract) {
            return
        }

        if (skipKickCounter > 0) {
            return
        }

        this.skipCounter = 20
        val ballLocation = position.toLocation()

        // Reduce hitbox size in order to stay compatible to old arena files.
        val hitboxSize = (meta.hitBoxSize - 2)

        for (player in players) {
            if (player.location.distance(ballLocation) < hitboxSize) {
                val event = BallInteractEvent(player, ball)
                Bukkit.getPluginManager().callEvent(event)

                if (event.isCancelled) {
                    continue
                }

                val vector = ballLocation
                    .toVector()
                    .subtract(player.location.toVector())
                    .normalize().multiply(meta.movementModifier.horizontalTouchModifier)
                vector.y = 0.1 * meta.movementModifier.verticalTouchModifier

                this.position.yaw = getYawFromVector(origin, vector.clone().toPosition().normalize()) * -1
                this.angularVelocity = 0.0
                // Move the ball a little bit up otherwise wallcollision of ground immidately cancel movement.
                this.position.y += 0.25
                this.motion = vector.toPosition()

                if (this.meta.rotating) {
                    ball.rotation = PositionEntity(2.0, 0.0, 0.0)
                }
            }
        }
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
                PositionEntity(-1.0, 0.0, 0.0)
            }
            BlockDirection.EAST -> {
                PositionEntity(1.0, 0.0, 0.0)
            }
            BlockDirection.NORTH -> {
                PositionEntity(0.0, 0.0, -1.0)
            }
            BlockDirection.SOUTH -> {
                PositionEntity(0.0, 0.0, 1.0)
            }
            else -> if (blockDirection == BlockDirection.DOWN) {
                PositionEntity(0.0, -1.0, 0.0)
            } else {
                PositionEntity(0.0, 1.0, 1.0)
            }.normalize()
        }

        val radianAngle = 2 * incomingVector.dot(normalVector)
        val outgoingVector =
            incomingVector.clone().subtract(normalVector.multiply(radianAngle))

        this.position.yaw = getYawFromVector(origin, outgoingVector.clone().normalize()) * -1

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
