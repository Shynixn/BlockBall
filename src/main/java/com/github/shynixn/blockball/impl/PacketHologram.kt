package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.HologramProxy
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.EntityType
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityDestroy
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntityMetadata
import com.github.shynixn.mcutils.packet.api.packet.PacketOutEntitySpawn
import org.bukkit.Location
import org.bukkit.entity.Player

class PacketHologram : HologramProxy {
    private var position: Vector3d? = null
    private var entityIds = ArrayList<Int>()
    private var backedLines: List<String> = emptyList()
    private var linesChanged = false
    private val playerTracker = AllPlayerTracker(
        {
            position!!
        }, { player ->
            sendSpawn(player)
            sendUpdateMetaData(player)
        },
        { player ->
            sendDestroy(player)

            if (players.contains(player)) {
                players.remove(player)
            }
        }, { player ->
            players.contains(player)
        })

    /**
     * Gets if this hologram was removed.
     */
    override var isDead: Boolean = false

    /**
     * List of players being able to see this hologram.
     */
    override val players: MutableSet<Player> = HashSet()

    /**
     * List of lines being displayed on the hologram.
     */
    override var lines: List<String>
        get() {
            return backedLines
        }
        set(value) {
            if (this.backedLines.size != value.size) {
                entityIds.clear()
                for (i in value.indices) {
                    entityIds.add(packetService.getNextEntityId())
                }

                this.playerTracker.dispose()
            }

            this.backedLines = value
            linesChanged = true
        }


    /**
     * Location of the hologram.
     */
    override var location: Location
        get() {
            return position!!.toLocation()
        }
        set(value) {
            this.position = value.toVector3d()
        }

    /**
     * Packet service dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Updates changes of the hologram.
     */
    override fun update() {
        val players = playerTracker.checkAndGet()

        for (player in players) {
            sendUpdateMetaData(player)
        }
    }

    /**
     * Removes this hologram permanently.
     */
    override fun remove() {
        if (isDead) {
            return
        }

        isDead = true
        playerTracker.dispose()
        players.clear()
        entityIds.clear()
    }

    /**
     * Sends a spawn packet.
     */
    private fun sendSpawn(player: Player) {
        for (i in 0 until entityIds.size) {
            val upSet = i * 0.24
            packetService.sendPacketOutEntitySpawn(player, PacketOutEntitySpawn().also {
                it.entityId = entityIds[i]
                it.entityType = EntityType.ARMOR_STAND
                it.target = Vector3d(this.position!!.world!!, this.position!!.x, this.position!!.y - upSet, this.position!!.z).toLocation()
            })
        }
    }

    /**
     * Updates the metadata.
     */
    private fun sendUpdateMetaData(player: Player) {
        for (i in 0 until entityIds.size) {
            packetService.sendPacketOutEntityMetadata(player, PacketOutEntityMetadata().also {
                it.entityId = entityIds[i]
                it.customname = lines[i]
                it.customNameVisible = true
                it.isInvisible = true
            })
        }
    }

    /**
     * Sends destroy.
     */
    private fun sendDestroy(player: Player) {
        packetService.sendPacketOutEntityDestroy(player, PacketOutEntityDestroy().also {
            it.entityIds = entityIds
        })
    }
}
