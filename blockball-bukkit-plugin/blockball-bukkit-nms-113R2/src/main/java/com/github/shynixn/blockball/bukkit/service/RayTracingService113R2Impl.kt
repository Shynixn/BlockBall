package com.github.shynixn.blockball.bukkit.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.RayTracingService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.RayTraceResultEntity
import net.minecraft.server.v1_13_R2.*
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld

class RayTracingService113R2Impl : RayTracingService {
    private val vector3dClazz by lazy { findClazz("net.minecraft.server.VERSION.Vec3D") }
    private val movingObjectClazz by lazy { findClazz("net.minecraft.server.VERSION.MovingObjectPosition") }

    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RaytraceResult {
        val bukkitWorld = Bukkit.getWorld(position.worldName!!)

        val nmsWorld = (bukkitWorld as CraftWorld).handle
        val startVector = Vec3D(position.x, position.y, position.z)
        val endPosition =
            PositionEntity(position.worldName!!, position.x + motion.x, position.y + motion.y, position.z + motion.z)
        val endVector = Vec3D(endPosition.x, endPosition.y, endPosition.z)

        val movingObjectPosition = nmsWorld.rayTrace(startVector, endVector, FluidCollisionOption.NEVER, false, false)

        if (movingObjectPosition == null) {
            endPosition.yaw = position.yaw
            endPosition.pitch = position.pitch
            return RayTraceResultEntity(false, endPosition, BlockDirection.DOWN)
        }

        val resultVector = movingObjectPosition.pos

        val resultPosition = PositionEntity(
            position.worldName!!,
            vector3dClazz.getDeclaredField("x").get(resultVector) as Double,
            vector3dClazz.getDeclaredField("y").get(resultVector) as Double,
            vector3dClazz.getDeclaredField("z").get(resultVector) as Double
        )

        val direction = BlockDirection.valueOf(
            movingObjectClazz.getDeclaredField("direction").get(movingObjectPosition).toString().toUpperCase()
        )

        val movingObjectType = movingObjectClazz.getDeclaredField("type").get(movingObjectPosition)

        resultPosition.yaw = position.yaw
        resultPosition.pitch = position.pitch

        return RayTraceResultEntity(movingObjectType.toString() == "BLOCK", resultPosition, direction)
    }

    /**
     * Finds the version compatible NMS class.
     */
    private fun findClazz(name: String): Class<*> {
        return Class.forName(
            name.replace(
                "VERSION",
                BlockBallApi.resolve(PluginProxy::class.java).getServerVersion().bukkitId
            )
        )
    }
}
