package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.bukkit.event.BallInteractEvent
import com.github.shynixn.blockball.api.bukkit.event.BallKickEvent
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
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class BallHitboxEntity(val entityId: Int, var position: Position, private val meta: BallMeta) {
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
     * Ball is grabbed.
     */
    var isGrabbed: Boolean = false

    /**
     * Gravity modifier.
     */
    private val gravity: Double = 0.07

    /**
     * Remaining time in ticks until players regain the ability to kick this ball.
     */
    private var skipKickCounter: Int = 0

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    private var angularVelocity: Double = 0.0

    /**
     * Skips next interaction.
     */
    private var skipCounter = 20

    /**
     * Runnable Value yaw change which reprents internal yaw change calculation.
     * Returns below 0 if yaw did not change.
     */
    private var yawChange: Float = -1.0F
    private var times: Int = 0
    private var reduceVector: Vector? = null
    private var originVector: Vector? = null

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

        if (yawChange > 0) {
            position.yaw = yawChange.toDouble()
            yawChange = -1.0F
        }

        if (requestTeleport) {
            requestTeleport = false
            packetService.sendEntityTeleportPacket(players, entityId, position)
            motion = PositionEntity(0.0, -0.7, 0.0)
            return
        }

        checkMovementInteractions(players as List<Player>)

        if (motion.x == 0.0 && motion.y == 0.0 && motion.z == 0.0) {
            return
        }

        val rayTraceResult = proxyService.rayTraceMotion(position, motion)

        if (rayTraceResult.hitBlock) {
            this.motion = PositionEntity(position.worldName!!, 0.0, 0.0, 0.0)
            return
        }

        packetService.sendEntityVelocityPacket(players, entityId, motion)
        packetService.sendEntityMovePacket(players, entityId, this.position, rayTraceResult.targetPosition)

        this.motion = this.motion.multiply(0.90)
        this.motion.y -= gravity
        this.position = rayTraceResult.targetPosition
    }

    /**
     * Kicks the hitbox for the given player interaction.
     */
    fun kickPlayer(player: Player, delay: Int, baseMultiplier: Double) {
        if (isGrabbed || skipKickCounter > 0) {
            return
        }

        val preEvent = BallInteractEvent(player, this.ball!!)
        Bukkit.getPluginManager().callEvent(preEvent)
        if (preEvent.isCancelled) {
            return
        }

        val prevEyeLoc = player.eyeLocation.clone()
        this.yawChange = player.location.yaw
        this.skipCounter = delay + 4
        this.skipKickCounter = delay + 4
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
                this.motion = event.resultVelocity.toPosition()
                this.angularVelocity = spinV
            }
        }
    }

    /**
     * Sets the velocity of the ball.
     */
    fun setVelocity(vector: Vector) {
        this.ball.ballDesignEntity.backAnimation = false
        this.angularVelocity = 0.0

        if (this.meta.rotating) {
            ball.rotation = PositionEntity(2.0, 0.0, 0.0)
        }

        try {
            this.times = (50 * this.meta.movementModifier.rollingDistanceModifier).toInt()
            this.motion = vector.toPosition()
            val normalized = vector.clone().normalize()
            this.originVector = vector.clone()
            this.reduceVector = Vector(
                normalized.x / this.times,
                0.0784 * meta.movementModifier.gravityModifier,
                normalized.z / this.times
            )
        } catch (ignored: IllegalArgumentException) {
            // Ignore calculated velocity if it's out of range.
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

        this.skipCounter = 2
        val ballLocation = position.toLocation()

        for (player in players) {
            if (player.location.distance(ballLocation) < meta.hitBoxSize) {
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

                this.yawChange = player.location.yaw
                this.setVelocity(vector)
            }
        }
    }
}
