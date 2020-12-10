package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_13_R2

import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.service.RayTracingService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toVector
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.RayTraceResultEntity
import org.bukkit.FluidCollisionMode
import org.bukkit.util.NumberConversions

class RayTracingService113R2Impl : RayTracingService {
    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RaytraceResult {
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
            PositionEntity(position.worldName!!, position.x + motion.x, position.y + motion.y, position.z + motion.z)
        val sourceLocation = position.toLocation()

        val directionVector = motion.toVector().normalize()
        val distance = motion.length()
        val world = sourceLocation.world!!

        val movingObjectPosition =
            world.rayTraceBlocks(sourceLocation, directionVector, distance, FluidCollisionMode.NEVER, false)

        if (movingObjectPosition == null) {
            endPosition.yaw = position.yaw
            endPosition.pitch = position.pitch
            return RayTraceResultEntity(false, endPosition, BlockDirection.DOWN)
        }

        val targetPosition = movingObjectPosition.hitPosition.toLocation(world).toPosition()
        val direction = BlockDirection.valueOf(
            movingObjectPosition.hitBlockFace!!.toString().toUpperCase()
        )

        targetPosition.yaw = position.yaw
        targetPosition.pitch = position.pitch

        return RayTraceResultEntity(true, targetPosition, direction)
    }

    private fun fixFiniteDomain(value: Double): Double {
        if (!NumberConversions.isFinite(value)) {
            return Math.round(value * 100.0) / 100.0
        }

        return value
    }
}
