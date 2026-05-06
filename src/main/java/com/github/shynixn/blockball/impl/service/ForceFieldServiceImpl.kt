package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.ForceFieldService
import com.github.shynixn.blockball.entity.ForceField
import com.github.shynixn.blockball.entity.ForceFieldPlayerData
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ForceFieldServiceImpl : ForceFieldService {
    private val playerForceFieldData = ConcurrentHashMap<Player, ForceFieldPlayerData>()
    private val forceFields = HashSet<ForceField>()

    init {
        for (player in Bukkit.getOnlinePlayers()) {
            checkInteractionsWithForceField(player, player.location)
        }
    }

    /**
     * Adds a new tracked forceField.
     */
    override fun addForceField(forceField: ForceField) {
        forceFields.add(forceField)
    }

    /**
     * Removes a tracked forcefield.
     */
    override fun removeForceField(forceField: ForceField) {
        forceFields.remove(forceField)
    }

    /**
     * Checks if the player is in flight status.
     */
    override fun isInFlightStatus(player: Player): Boolean {
        val playerData = playerForceFieldData[player] ?: return false
        return playerData.hasReceivedFlight
    }

    /**
     * Resets.
     */
    override fun resetFlightStatus(player: Player) {
        val playerData = playerForceFieldData[player] ?: return
        playerData.hasReceivedFlight = false
        player.allowFlight = false
        player.isFlying = false
    }

    /**
     * Checks if the player is currently interacting with a forceField and applies the according effects.
     */
    override fun checkInteractionsWithForceField(player: Player, location: Location) {
        val now = System.currentTimeMillis()
        var playerData = playerForceFieldData[player]

        if (playerData == null) {
            playerData = ForceFieldPlayerData().also {
                it.joinTimeStamp = now
                it.hasReceivedFlight = false
            }
            playerForceFieldData[player] = playerData
        }

        if (now - playerData.joinTimeStamp > 2000) { // It is important to not immediately perform join checks otherwise it could collision with other loading.
            for (forceField in forceFields) {
                checkInsideOutSide(player, location.toVector3d(), forceField)
            }
        }
    }

    /**
     * Clears the resources from the given player.
     */
    override fun clear(player: Player) {
        val playerData = playerForceFieldData.remove(player) ?: return

        if (playerData.hasReceivedFlight && player.gameMode != GameMode.CREATIVE && player.gameMode != GameMode.SPECTATOR) {
            player.isFlying = false
            player.allowFlight = false
        }
    }

    /**
     * Knocks a player into the center of the forceField.
     */
    override fun knockBackInside(forceField: ForceField, player: Player, strength: Float) {
        knockBackPlayer(forceField, player, strength)
    }

    /**
     * Knocks a player out the center of the forceField.
     */
    override fun knockBackOutside(forceField: ForceField, player: Player, strength: Float) {
        knockBackPlayer(forceField, player, strength * -1)
    }

    private fun knockBackPlayer(forceField: ForceField, player: Player, modifier: Float) {
        val playerData = playerForceFieldData[player] ?: return
        val knockBack = forceField.center.toVector().subtract(player.location.toVector()).normalize().multiply(modifier)
        player.velocity = knockBack
        player.allowFlight = true
        playerData.hasReceivedFlight = true
    }

    private fun checkInsideOutSide(player: Player, location: Vector3d, forceField: ForceField) {
        if (location.world != null && location.world == forceField.lowerCorner.world) {
            if (forceField.lowerCorner.x <= location.x && forceField.upperCorner.x >= location.x) {
                if (forceField.lowerCorner.z <= location.z && forceField.upperCorner.z >= location.z) {
                    forceField.on2dInside.invoke(player)
                    if (forceField.lowerCorner.y <= location.y + 1 && forceField.upperCorner.y >= location.y + 1) {
                        forceField.on3dInside.invoke(player)
                    } else {
                        forceField.on3dOutSide.invoke(player)
                    }
                } else {
                    forceField.on2dOutSide.invoke(player)
                    forceField.on3dOutSide.invoke(player)
                }
            } else {
                forceField.on2dOutSide.invoke(player)
                forceField.on3dOutSide.invoke(player)
            }
        }
    }
}