@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.enumeration.BallSize
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toEulerAngle
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.*
import org.bukkit.entity.Player

class BallDesignEntity(val entityId: Int) {
    /**
     * Rotation of the design in euler angles.
     */
    var rotation: Vector3d = Vector3d(0.0, 0.0, 0.0)

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
    fun spawn(player: Any, position: Vector3d) {
        require(player is Player)
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.target = position.toLocation()
            it.entityId = entityId
            it.entityType = EntityType.ARMOR_STAND
        })

        if (!ball.meta.isSlimeVisible) {
            val stack = itemService.toItemStack(ball.meta.item)
            packetService.sendPacketOutEntityEquipment(player, PacketOutEntityEquipment().also {
                it.entityId = entityId
                it.items = listOf(Pair(ArmorSlotType.HELMET, stack))
            })
        }

        packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
            it.entityId = entityId
            it.isInvisible = true
            it.isArmorstandSmall = ball.meta.size == BallSize.SMALL
        })
    }

    /**
     * Destroys the ball for the given player.
     */
    fun destroy(player: Any) {
        require(player is Player)
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = listOf(entityId)
        })
    }

    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun tick(players: List<Player>) {
        val position = ball.getLocation().toVector3d()

        position.y = if (ball.meta.size == BallSize.NORMAL) {
            position.y + ball.meta.hitBoxRelocation - 1.2
        } else {
            position.y + ball.meta.hitBoxRelocation - 0.4
        }

        for (player in players) {
            packetService.sendPacketOutEntityTeleport(player, PacketOutEntityTeleport().also {
                it.entityId = entityId
                it.target = position.toLocation()
            })
        }

        if (ball.meta.rotating) {
            playRotationAnimation(players as List<Any>)
        }
    }

    /**
     * Plays the rotation animation.
     */
    private fun playRotationAnimation(players: List<Any>) {
        // 360 0 0 is a full forward rotation.
        // Length of the velocity is the speed of the ball.
        val velocity = ball.getVelocity().toVector3d()

        val length = if (ball.isOnGround) {
            Vector3d(velocity.x, 0.0, velocity.z).length()
        } else {
            Vector3d(velocity.x, velocity.y, velocity.z).length()
        }

        val angle = when {
            length > 1.0 -> Vector3d(rotation.x - 30, 0.0, 0.0)
            length > 0.1 -> Vector3d(rotation.x - 10, 0.0, 0.0)
            length > 0.08 -> Vector3d(rotation.x - 5, 0.0, 0.0)
            else -> null
        }

        if (angle != null) {
            rotation = Vector3d(angle.x, angle.y, angle.z)

            if (ball.meta.isSlimeVisible) {
                return
            }

            for (player in players) {
                require(player is Player)
                packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                    it.armorStandHeadRotation = angle.toEulerAngle()
                    it.entityId = entityId
                })
            }
        }
    }
}
