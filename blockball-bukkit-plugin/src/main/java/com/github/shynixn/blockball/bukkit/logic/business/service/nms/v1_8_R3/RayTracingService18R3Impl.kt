package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_8_R3

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.RayTracingService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.RayTraceResultEntity
import com.google.inject.Inject
import org.bukkit.Bukkit

class RayTracingService18R3Impl @Inject constructor(private val pluginProxy: PluginProxy) : RayTracingService {
    private val craftWorldClazz by lazy { findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld") }
    private val craftWorldClazzHandleMethod by lazy { craftWorldClazz.getDeclaredMethod("getHandle") }
    private val nmsWorldClazz by lazy { findClazz("net.minecraft.server.VERSION.World") }
    private val vector3dClazz by lazy { findClazz("net.minecraft.server.VERSION.Vec3D") }
    private val movingObjectClazz by lazy { findClazz("net.minecraft.server.VERSION.MovingObjectPosition") }

    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RaytraceResult {
        val bukkitWorld = Bukkit.getWorld(position.worldName!!)
        val nmsWorld = craftWorldClazzHandleMethod.invoke(bukkitWorld)
        val startVector = vector3dClazz
            .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
            .newInstance(position.x, position.y, position.z)
        val endPosition =
            PositionEntity(position.worldName!!, position.x + motion.x, position.y + motion.y, position.z + motion.z)
        val endVector = vector3dClazz
            .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
            .newInstance(endPosition.x, endPosition.y, endPosition.z)

        val movingObjectPosition = nmsWorldClazz
            .getDeclaredMethod(
                "rayTrace",
                vector3dClazz,
                vector3dClazz,
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java
            )
            .invoke(
                nmsWorld,
                startVector,
                endVector,
                false,
                false,
                false
            )

        if (movingObjectPosition == null) {
            endPosition.yaw = position.yaw
            endPosition.pitch = position.pitch
            return RayTraceResultEntity(false, endPosition, BlockDirection.DOWN)
        }

        val resultVector = movingObjectClazz.getDeclaredField("pos").accessible(true).get(movingObjectPosition)

        val resultPosition = if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            PositionEntity(
                position.worldName!!,
                vector3dClazz.getDeclaredField("x").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("y").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("z").get(resultVector) as Double
            )
        } else {
            PositionEntity(
                position.worldName!!,
                vector3dClazz.getDeclaredField("a").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("b").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("c").get(resultVector) as Double
            )
        }

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
