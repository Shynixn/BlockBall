package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.EntityAttribute
import com.github.shynixn.mcutils.packet.api.meta.enumeration.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.atan2

class BallDesignEntity(val entityId: Int) {
    companion object {
        const val SCALE: String = "scale"
    }

    private var rotationDegrees: Int = 0

    /**
     * Packet service dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Item service dependency.
     */
    lateinit var itemService: ItemService

    /**
     * Reference.
     */
    lateinit var ball: SoccerBall

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Player) {
        val position = ball.getLocation().toVector3d()
        position.y += ball.meta.render.offSetY
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.target = position.toLocation()
            it.entityId = entityId
            it.entityType = EntityType.ARMOR_STAND
        })

        if (!ball.meta.hitbox.slimeVisible) {
            val stack = itemService.toItemStack(ball.meta.render.item)
            packetService.sendPacketOutEntityEquipment(player, PacketOutEntityEquipment().also {
                it.entityId = entityId
                it.items = listOf(Pair(ArmorSlotType.HELMET, stack))
            })
        }

        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_20_R4)) {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = entityId
                it.isInvisible = true
            })
            packetService.sendPacketOutEntityAttributes(player, PacketOutEntityAttributes().also {
                it.entityId = entityId
                it.attributes = listOf(EntityAttribute().also { at ->
                    at.id = SCALE
                    at.base = ball.meta.render.scale
                })
            })
        } else {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = entityId
                it.isInvisible = true
                it.isArmorstandSmall = ball.meta.render.scale == 0.5
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
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun tick(players: List<Pair<Player, Location>>) {
        val position = ball.getLocation().toVector3d()
        position.y += ball.meta.render.offSetY
        position.yaw = vectorToYaw(ball.getVelocity())

        for (player in players) {
            packetService.sendPacketOutEntityTeleport(player.first, PacketOutEntityTeleport().also {
                it.entityId = entityId
                it.target = position.toLocation()
            })
        }

        if (ball.meta.render.rotating) {
            playRotationAnimation(players)
        }
    }

    /**
     * Plays the rotation animation.
     */
    private fun playRotationAnimation(players: List<Pair<Player, Location>>) {
        // 360 0 0 is a full forward rotation.
        // Length of the velocity is the speed of the ball.
        val velocity = ball.getVelocity().toVector3d()
        val length = if (ball.isOnGround) {
            Vector3d(velocity.x, 0.0, velocity.z).length()
        } else {
            Vector3d(velocity.x, velocity.y, velocity.z).length()
        }

        val angle = when {
            length > 1.0 -> rotationDegrees - 20
            length > 0.1 -> rotationDegrees - 10
            length > 0.08 -> rotationDegrees - 5
            else -> null
        }

        if (angle != null) {
            rotationDegrees = angle % 360
            if (ball.meta.hitbox.slimeVisible) {
                return
            }

            for (player in players) {
                packetService.sendPacketOutEntityMetadata(player.first, PacketOutEntityMetadata().also {
                    it.armorStandHeadRotation = EulerAngle(-1 * rotationDegrees.toDouble(), 0.0, 0.0)
                    it.entityId = entityId
                })
            }
        }
    }

    private fun vectorToYaw(vector: Vector): Double {
        // Invert X because Minecraft yaw is reversed around the Y-axis
        var yaw = Math.toDegrees(atan2(-vector.x, vector.z))

        // Normalize to the range (-180, 180]
        if (yaw < -180) yaw += 360.0
        if (yaw > 180) yaw -= 360.0

        return yaw
    }
}
