@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.Ball
import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.enumeration.BallSize
import com.github.shynixn.blockball.impl.extension.toLocation
import com.github.shynixn.blockball.impl.extension.toPosition
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toEulerAngle
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.*
import org.bukkit.entity.Player
import java.nio.charset.Charset
import java.util.*

class BallDesignEntity(val entityId: Int) {
    /**
     * Rotation of the design in euler angles.
     */
    var rotation: Position = Position(0.0, 0.0, 0.0)

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
    lateinit var ball: Ball

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Any, position: Position) {
        require(player is Player)
        packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
            it.target = position.toLocation()
            it.entityId = entityId
            it.entityType = EntityType.ARMOR_STAND
        })

        if (!ball.meta.isSlimeVisible) {
            val encodingSkinUrl = Base64.getEncoder().encodeToString(
                "{\"textures\":{\"SKIN\":{\"url\":\"${ball.meta.skin}\"}}}".toByteArray(
                    Charset.forName("UTF-8")
                )
            )
            val item = Item().also {
                it.typeName = ball.meta.itemType
                it.durability = ball.meta.itemDamage
            }

            if (ball.meta.itemNbt != null && !ball.meta.itemNbt.isNullOrEmpty()) {
                item.nbt = ball.meta.itemNbt
            } else {
                item.nbt =
                    "{SkullOwner:{Id:[I;1,1,1,1],Name:\"FootBall\",Properties:{textures:[{Value:\"${encodingSkinUrl}\"}]}}}"
            }

            val stack = itemService.toItemStack(item)

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
        val position = ball.getLocation().toPosition()

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
        val velocity = ball.getVelocity().toPosition()

        val length = if (ball.isOnGround) {
            Position(velocity.x, 0.0, velocity.z).length()
        } else {
            Position(velocity.x, velocity.y, velocity.z).length()
        }

        val angle = when {
            length > 1.0 -> Vector3d(rotation.x - 30, 0.0, 0.0)
            length > 0.1 -> Vector3d(rotation.x - 10, 0.0, 0.0)
            length > 0.08 -> Vector3d(rotation.x - 5, 0.0, 0.0)
            else -> null
        }

        if (angle != null) {
            rotation = Position(angle.x, angle.y, angle.z)

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
