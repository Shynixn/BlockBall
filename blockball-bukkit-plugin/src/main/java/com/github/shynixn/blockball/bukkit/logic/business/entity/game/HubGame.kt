package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.container.PlayerStorage
import com.github.shynixn.blockball.bukkit.logic.business.helper.toBukkitLocation
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class HubGame(arena: BukkitArena) : LowLevelGame(arena) {


    /** Join the game. */
    override fun join(player: Player, team: Team): Boolean {
        this.leave(player)
        if (team == Team.RED && this.redTeam.size < this.arena.meta.redTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, team, this.arena.meta.redTeamMeta)
            return true
        } else if (team == Team.BLUE && this.blueTeam.size < this.arena.meta.blueTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, team, this.arena.meta.blueTeamMeta)
            return true
        }
        return false
    }

    /** Leave the game. */
    override fun leave(player: Player) {
        if (!this.ingameStats.containsKey(player))
            return
        val stats = ingameStats[player] ?: return
        if (stats.team == Team.RED) {
            player.sendMessage(arena.meta.redTeamMeta.leaveMessage)
            this.redTeam.remove(player)
        } else if (stats.team == Team.BLUE) {
            player.sendMessage(arena.meta.blueTeamMeta.leaveMessage)
            this.blueTeam.remove(player)
        }
        ingameStats.remove(player)
        stats.resetState()

        player.teleport(arena.meta.hubLobbyMeta.leaveSpawnpoint?.toBukkitLocation())
    }

    override fun onUpdateSigns() {
        for (i in this.arena.meta.hubLobbyMeta.redTeamSigns.indices) {
            val position = this.arena.meta.hubLobbyMeta.redTeamSigns[i]
            if (!replaceTextOnSign(position, arena.meta.redTeamMeta.signLines, arena.meta.redTeamMeta)) {
                this.arena.meta.hubLobbyMeta.redTeamSigns.removeAt(i)
            }
        }
        for (i in this.arena.meta.hubLobbyMeta.blueTeamSigns.indices) {
            val position = this.arena.meta.hubLobbyMeta.blueTeamSigns[i]
            if (!replaceTextOnSign(position, arena.meta.blueTeamMeta.signLines, arena.meta.blueTeamMeta)) {
                this.arena.meta.hubLobbyMeta.blueTeamSigns.removeAt(i)
            }
        }
        for (i in this.arena.meta.hubLobbyMeta.leaveSigns.indices) {
            val position = this.arena.meta.hubLobbyMeta.leaveSigns[i]
            if (!replaceTextOnSign(position, arena.meta.hubLobbyMeta.leaveSignLines, null)) {
                this.arena.meta.hubLobbyMeta.leaveSigns.removeAt(i)
            }
        }
    }

    private fun prepareStatsForPlayer(player: Player, team: Team, teamMeta: TeamMeta<Location, ItemStack>) {
        val stats = PlayerStorage(player, team)
        stats.storeForType(GameType.HUBGAME)
        this.ingameStats.put(player, stats)

        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()
        player.inventory.armorContents = teamMeta.armorContents.clone()
        player.updateInventory()

        if (teamMeta.spawnpoint == null) {
            player.teleport(arena.center)
        } else {
            player.teleport(teamMeta.spawnpoint!!.toBukkitLocation())
        }

        player.sendMessage(teamMeta.joinMessage)
    }
}