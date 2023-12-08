@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toEulerAngle
import com.github.shynixn.mcutils.packet.api.ArmorSlotType
import com.github.shynixn.mcutils.packet.api.EntityType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.packet.*
import org.bukkit.entity.Player
import java.nio.charset.Charset
import java.util.*

class BallDesignEntity(val entityId: Int) {
    /**
     * Rotation of the design in euler angles.
     */
    var rotation: Position = PositionEntity(0.0, 0.0, 0.0)

    /**
     * Proxy service dependency.
     */
    lateinit var proxyService: ProxyService

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
    lateinit var ball: BallProxy

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
                it.typeName = "PLAYER_HEAD,397"
                it.durability = 3
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
    fun <P> tick(players: List<P>) {
        val position = proxyService.toPosition(ball.getLocation<Any>())

        position.y = if (ball.meta.size == BallSize.NORMAL) {
            position.y + ball.meta.hitBoxRelocation - 1.2
        } else {
            position.y + ball.meta.hitBoxRelocation - 0.4
        }

        for (player in players) {
            require(player is Player)
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
        val velocity = proxyService.toPosition(ball.getVelocity<Any>())

        val length = if (ball.isOnGround) {
            PositionEntity(velocity.x, 0.0, velocity.z).length()
        } else {
            PositionEntity(velocity.x, velocity.y, velocity.z).length()
        }

        val angle = when {
            length > 1.0 -> Vector3d(rotation.x - 30, 0.0, 0.0)
            length > 0.1 -> Vector3d(rotation.x - 10, 0.0, 0.0)
            length > 0.08 -> Vector3d(rotation.x - 5, 0.0, 0.0)
            else -> null
        }

        if (angle != null) {
            rotation = PositionEntity(angle.x, angle.y, angle.z)

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
