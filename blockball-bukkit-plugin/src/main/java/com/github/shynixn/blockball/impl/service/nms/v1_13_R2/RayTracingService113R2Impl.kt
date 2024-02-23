package com.github.shynixn.blockball.impl.service.nms.v1_13_R2

import com.github.shynixn.blockball.contract.RayTracingService
import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.entity.RayTraceResult
import com.github.shynixn.blockball.enumeration.BlockDirection
import com.github.shynixn.blockball.impl.extension.toLocation
import com.github.shynixn.blockball.impl.extension.toPosition
import com.github.shynixn.blockball.impl.extension.toVector
import org.bukkit.FluidCollisionMode
import org.bukkit.util.NumberConversions

class RayTracingService113R2Impl : RayTracingService {
    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RayTraceResult {
        if (!NumberConversions.isFinite(position.yaw.toFloat())) {
            position.yaw = Math.round((position.yaw % 360.0F) * 100.0) / 100.0
        }

        if (!NumberConversions.isFinite(position.pitch.toFloat())) {
            position.pitch = Math.round((position.pitch % 360.0F) * 100.0) / 100.0
        }

        position.x = fixFiniteDomain(position.x)
        position.y = fixFiniteDomain(position.y)
        position.z = fixFiniteDomain(position.z)
        motion.x = fixFiniteDomain(motion.x)
        motion.y = fixFiniteDomain(motion.y)
        motion.z = fixFiniteDomain(motion.z)

        val endPosition =
            Position(position.worldName!!, position.x + motion.x, position.y + motion.y, position.z + motion.z)
        val sourceLocation = position.toLocation()

        val directionVector = motion.toVector().normalize()
        var distance = motion.length()
        val world = sourceLocation.world!!

        sourceLocation.x = fixFiniteDomain(sourceLocation.x)
        sourceLocation.y = fixFiniteDomain(sourceLocation.y)
        sourceLocation.z = fixFiniteDomain(sourceLocation.z)
        sourceLocation.yaw = fixFiniteDomain(sourceLocation.yaw.toDouble()).toFloat()
        sourceLocation.pitch = fixFiniteDomain(sourceLocation.pitch.toDouble()).toFloat()
        directionVector.x = fixFiniteDomain(directionVector.x)
        directionVector.y = fixFiniteDomain(directionVector.y)
        directionVector.z = fixFiniteDomain(directionVector.z)
        distance = fixFiniteDomain(distance)

        val movingObjectPosition =
            world.rayTraceBlocks(sourceLocation, directionVector, distance, FluidCollisionMode.NEVER, false)

        if (movingObjectPosition == null) {
            endPosition.yaw = position.yaw
            endPosition.pitch = position.pitch
            return RayTraceResult(false, endPosition, BlockDirection.DOWN)
        }

        val targetPosition = movingObjectPosition.hitPosition.toLocation(world).toPosition()
        val direction = BlockDirection.valueOf(
            movingObjectPosition.hitBlockFace!!.toString().toUpperCase()
        )

        targetPosition.yaw = position.yaw
        targetPosition.pitch = position.pitch

        return RayTraceResult(true, targetPosition, direction)
    }

    private fun fixFiniteDomain(value: Double): Double {
        if (!NumberConversions.isFinite(value)) {
            return Math.round(value * 100.0) / 100.0
        }

        return value
    }
}
