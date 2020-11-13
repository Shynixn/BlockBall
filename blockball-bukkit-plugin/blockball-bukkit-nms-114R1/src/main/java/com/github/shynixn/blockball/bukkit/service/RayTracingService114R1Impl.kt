package com.github.shynixn.blockball.bukkit.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.RayTracingService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.RayTraceResultEntity
import org.bukkit.Bukkit

class RayTracingService114R1Impl : RayTracingService {
    private val craftWorldClazz by lazy { findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld") }
    private val craftWorldClazzHandleMethod by lazy { craftWorldClazz.getDeclaredMethod("getHandle") }
    private val vector3dClazz by lazy { findClazz("net.minecraft.server.VERSION.Vec3D") }
    private val iBlockAccessClazz by lazy { findClazz("net.minecraft.server.VERSION.IBlockAccess") }
    private val blockCollisionOptionClazz by lazy { findClazz("net.minecraft.server.VERSION.RayTrace\$BlockCollisionOption") }
    private val fluidCollisionOptionClazz by lazy { findClazz("net.minecraft.server.VERSION.RayTrace\$FluidCollisionOption") }
    private val rayTraceClazz by lazy { findClazz("net.minecraft.server.VERSION.RayTrace") }
    private val entityClazz by lazy { findClazz("net.minecraft.server.VERSION.Entity") }
    private val movingObjectClazz by lazy { findClazz("net.minecraft.server.VERSION.MovingObjectPosition") }
    private val movingObjectBlockClazz by lazy { findClazz("net.minecraft.server.VERSION.MovingObjectPositionBlock") }

    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RaytraceResult {
        val bukkitWorld = Bukkit.getWorld(position.worldName!!)
        val nmsWorld = craftWorldClazzHandleMethod.invoke(bukkitWorld)
        val startVector = vector3dClazz
            .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
            .newInstance(position.x, position.y, position.z)
        val endVector = vector3dClazz
            .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
            .newInstance(position.x + motion.x, position.y + motion.y, position.z + motion.z)
        val rayTrace = rayTraceClazz.getDeclaredConstructor(
            vector3dClazz,
            vector3dClazz,
            blockCollisionOptionClazz,
            fluidCollisionOptionClazz,
            entityClazz
        ).newInstance(
            startVector,
            endVector,
            blockCollisionOptionClazz.enumConstants.first { e -> e.toString() == "COLLIDER" },
            fluidCollisionOptionClazz.enumConstants.first { e -> e.toString() == "NONE" },
            null
        )
        val movingObjectPosition = iBlockAccessClazz.getDeclaredMethod("rayTrace", rayTraceClazz)
            .invoke(nmsWorld, rayTrace)
        val resultVector = movingObjectClazz.getDeclaredMethod("getPos").invoke(movingObjectPosition)

        val resultPosition = PositionEntity(
            position.worldName!!,
            vector3dClazz.getDeclaredMethod("getX").invoke(resultVector) as Double,
            vector3dClazz.getDeclaredMethod("getY").invoke(resultVector) as Double,
            vector3dClazz.getDeclaredMethod("getZ").invoke(resultVector) as Double
        )

        val direction = BlockDirection.valueOf(
            movingObjectBlockClazz.getDeclaredMethod("getDirection").invoke(movingObjectPosition).toString()
                .toUpperCase()
        )

        val movingObjectType = movingObjectClazz.getDeclaredMethod("getType")
            .invoke(movingObjectPosition)

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
