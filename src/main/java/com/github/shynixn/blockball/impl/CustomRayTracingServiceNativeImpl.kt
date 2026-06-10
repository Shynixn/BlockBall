package com.github.shynixn.blockball.impl

import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class CustomRayTracingServiceNativeImpl {
    /**
     * Ray traces in the world.
     */
    fun rayTrace(
        startLocation: Location,
        motion: Vector,
        maxDistance: Double,
        collideWithWater: Boolean,
        collideWithPassableBlocks: Boolean
    ): RayTraceResult2 {
        val fluidCollision = if (collideWithWater) {
            FluidCollisionMode.ALWAYS
        } else {
            FluidCollisionMode.NEVER
        }

        val ignorePassableBlocks = !collideWithPassableBlocks

        val world = startLocation.world!!
        val sourceLocation = Location(
            startLocation.world,
            applyCoordinateFilter(startLocation.x),
            applyCoordinateFilter(startLocation.y),
            applyCoordinateFilter(startLocation.z),
            applyRotationFilter(startLocation.yaw),
            applyRotationFilter(startLocation.pitch)
        )

        val resultPosition =
            world.rayTraceBlocks(sourceLocation, motion, maxDistance, fluidCollision, ignorePassableBlocks)

        if (resultPosition == null) {
            val endPosition = Location(
                startLocation.world,
                startLocation.x + motion.x,
                startLocation.y + motion.y,
                startLocation.z + motion.z,
                startLocation.yaw,
                startLocation.pitch
            )
            return RayTraceResult2(endPosition, false, null, null)
        }

        val endPosition = Location(
            startLocation.world,
            resultPosition.hitPosition.x,
            resultPosition.hitPosition.y,
            resultPosition.hitPosition.z,
            startLocation.yaw,
            startLocation.pitch
        )
        return RayTraceResult2(endPosition, true, resultPosition.hitBlockFace!!, resultPosition.hitBlock)
    }

    private fun applyCoordinateFilter(value: Double): Double {
        if (!NumberConversions.isFinite(value)) {
            return (value * 100.0).roundToInt() / 100.0
        }

        return value
    }

    private fun applyRotationFilter(value: Float): Float {
        if (!NumberConversions.isFinite(value)) {
            return ((value % 360.0F) * 100.0).roundToInt() / 100.0F
        }

        return value
    }

    class RayTraceResult2(
        val targetLocation: Location, val hasHitBlock: Boolean = false,
        val blockFace: BlockFace? = null,
        val block: Block? = null
    )
}
