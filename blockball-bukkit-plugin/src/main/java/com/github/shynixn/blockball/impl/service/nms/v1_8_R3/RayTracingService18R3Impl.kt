package com.github.shynixn.blockball.impl.service.nms.v1_8_R3

import com.github.shynixn.blockball.contract.RayTracingService
import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.entity.RayTraceResult
import com.github.shynixn.blockball.enumeration.BlockDirection
import com.github.shynixn.blockball.impl.extension.findClazz
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.Version.Companion.findClass
import com.github.shynixn.mcutils.common.accessible
import com.google.inject.Inject
import org.bukkit.Bukkit

class RayTracingService18R3Impl : RayTracingService {
    private val craftWorldClazz by lazy { findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld") }
    private val craftWorldClazzHandleMethod by lazy { craftWorldClazz.getDeclaredMethod("getHandle") }
    private val nmsWorldClazz by lazy { findClass("net.minecraft.server.VERSION.World") }
    private val vector3dClazz by lazy { findClazz("net.minecraft.server.VERSION.Vec3D") }
    private val movingObjectClazz by lazy { findClazz("net.minecraft.server.VERSION.MovingObjectPosition") }

    /**
     * Ray traces in the world for the given motion.
     */
    override fun rayTraceMotion(position: Position, motion: Position): RayTraceResult {
        val bukkitWorld = Bukkit.getWorld(position.worldName!!)
        val nmsWorld = craftWorldClazzHandleMethod.invoke(bukkitWorld)
        val startVector = vector3dClazz
            .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
            .newInstance(position.x, position.y, position.z)
        val endPosition =
            Position(position.worldName!!, position.x + motion.x, position.y + motion.y, position.z + motion.z)
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
            return RayTraceResult(false, endPosition, BlockDirection.DOWN)
        }

        val resultVector = movingObjectClazz.getDeclaredField("pos").accessible().get(movingObjectPosition)

        val resultPosition = if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            Position(
                position.worldName!!,
                vector3dClazz.getDeclaredField("x").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("y").get(resultVector) as Double,
                vector3dClazz.getDeclaredField("z").get(resultVector) as Double
            )
        } else {
            Position(
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

        return RayTraceResult(movingObjectType.toString() == "BLOCK", resultPosition, direction)
    }
}
