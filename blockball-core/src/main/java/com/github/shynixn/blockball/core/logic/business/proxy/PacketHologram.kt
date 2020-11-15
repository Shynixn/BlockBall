package com.github.shynixn.blockball.core.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.persistence.entity.EntityMetadataImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity

class PacketHologram : HologramProxy {
    private var position: Position? = null
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
    override val players: MutableSet<Any> = HashSet()

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
                    entityIds.add(proxyService.createNewEntityId())
                }

                this.playerTracker.dispose()
            }

            this.backedLines = value
            linesChanged = true
        }


    /**
     * Location of the hologram.
     */
    override var location: Any
        get() {
            return proxyService.toLocation(position!!)
        }
        set(value) {
            this.position = proxyService.toPosition(value)
        }

    /**
     * Packet service dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Proxy service dependency.
     */
    var proxyService: ProxyService
        get() {
            return playerTracker.proxyService
        }
        set(value) {
            playerTracker.proxyService = value
        }

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
    private fun sendSpawn(player: Any) {
        for (i in 0 until entityIds.size) {
            val upSet = i * 0.24

            packetService.sendEntitySpawnPacket(
                player,
                entityIds[i],
                "ARMOR_STAND",
                PositionEntity(this.position!!.x, this.position!!.y - upSet, this.position!!.z)
            )
        }
    }

    /**
     * Updates the metadata.
     */
    private fun sendUpdateMetaData(player: Any) {
        for (i in 0 until entityIds.size) {
            packetService.sendEntityMetaDataPacket(player, entityIds[i], EntityMetadataImpl {
                this.customNameVisible = true
                this.customname = lines[i]
                this.isInvisible = true
            })
        }
    }

    /**
     * Sends destroy.
     */
    private fun sendDestroy(player: Any) {
        for (i in 0 until entityIds.size) {
            packetService.sendEntityDestroyPacket(player, entityIds[i])
        }
    }
}
