package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.container.PlayerStorage
import com.github.shynixn.blockball.bukkit.logic.business.helper.sendScreenMessage
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
class HubGame(arena: BukkitArena) : SoccerGame(arena) {
    /**
     * Gets called when a player scores a point for the given team.
     */
    override fun onScore(teamMeta: TeamMeta<Location, ItemStack>) {
        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle
        this.getPlayers().forEach{ p -> p.sendScreenMessage(scoreMessageTitle, scoreMessageSubTitle, this)}
    }

    /**
     * Gets called when a team wins the game.
     */
    override fun onWin(teamMeta: TeamMeta<Location, ItemStack>) {
        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle
        this.getPlayers().forEach{ p -> p.sendScreenMessage(winMessageTitle, winMessageSubTitle, this)}
    }

    /** Join the game. */
    override fun join(player: Player, team: Team?): Boolean {
        this.leave(player)
        if (team == Team.RED && this.redTeam.size < this.arena.meta.redTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, team, this.arena.meta.redTeamMeta)
            this.redTeam.add(player)
            return true
        } else if (team == Team.BLUE && this.blueTeam.size < this.arena.meta.blueTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, team, this.arena.meta.blueTeamMeta)
            this.blueTeam.add(player)
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
            player.sendMessage(Config.prefix + arena.meta.redTeamMeta.leaveMessage)
            this.redTeam.remove(player)
        } else if (stats.team == Team.BLUE) {
            player.sendMessage(Config.prefix + arena.meta.blueTeamMeta.leaveMessage)
            this.blueTeam.remove(player)
        }
        ingameStats.remove(player)
        stats.resetState()

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint?.toBukkitLocation())
        }
    }

    override fun onUpdateSigns() {
        for (i in this.arena.meta.lobbyMeta.redTeamSigns.indices) {
            val position = this.arena.meta.lobbyMeta.redTeamSigns[i]
            if (!replaceTextOnSign(position, arena.meta.redTeamMeta.signLines, arena.meta.redTeamMeta)) {
                this.arena.meta.lobbyMeta.redTeamSigns.removeAt(i)
            }
        }
        for (i in this.arena.meta.lobbyMeta.blueTeamSigns.indices) {
            val position = this.arena.meta.lobbyMeta.blueTeamSigns[i]
            if (!replaceTextOnSign(position, arena.meta.blueTeamMeta.signLines, arena.meta.blueTeamMeta)) {
                this.arena.meta.lobbyMeta.blueTeamSigns.removeAt(i)
            }
        }
        for (i in this.arena.meta.lobbyMeta.joinSigns.indices) {
            val position = this.arena.meta.lobbyMeta.joinSigns[i]
            if (!replaceTextOnSign(position, arena.meta.lobbyMeta.joinSignLines, null)) {
                this.arena.meta.lobbyMeta.joinSigns.removeAt(i)
            }
        }
        for (i in this.arena.meta.lobbyMeta.leaveSigns.indices) {
            val position = this.arena.meta.lobbyMeta.leaveSigns[i]
            if (!replaceTextOnSign(position, arena.meta.lobbyMeta.leaveSignLines, null)) {
                this.arena.meta.lobbyMeta.leaveSigns.removeAt(i)
            }
        }
    }

    private fun prepareStatsForPlayer(player: Player, team: Team, teamMeta: TeamMeta<Location, ItemStack>) {
        val stats = PlayerStorage(player, team)
        stats.storeForType(GameType.HUBGAME)
        this.ingameStats[player] = stats

        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()
        player.inventory.armorContents = teamMeta.armorContents.clone()
        player.updateInventory()

        if (teamMeta.spawnpoint == null) {
            player.teleport(arena.meta.ballMeta.spawnpoint.toBukkitLocation())
        } else {
            player.teleport(teamMeta.spawnpoint!!.toBukkitLocation())
        }

        player.sendMessage(Config.prefix + teamMeta.joinMessage)
    }
}