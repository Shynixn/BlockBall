package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.api.bukkit.business.event.GameJoinEvent
import com.github.shynixn.blockball.api.bukkit.business.event.GameLeaveEvent
import com.github.shynixn.blockball.api.bukkit.business.event.GameWinEvent
import com.github.shynixn.blockball.api.bukkit.business.event.GoalShootEvent
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.container.PlayerStorage
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toBukkitLocation
import org.bukkit.Bukkit
import org.bukkit.GameMode
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
    override fun onScore(team: Team, teamMeta: TeamMeta<Location, ItemStack>) {
        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle

        val players = ArrayList(getPlayers())
        val additionalPlayers = getAdditionalNotificationPlayers()
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p -> screenMessageService.setTitle(p, scoreMessageTitle.replaceGamePlaceholder(this), scoreMessageSubTitle.replaceGamePlaceholder(this)) }

        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            val event = GoalShootEvent(this, lastInteractedEntity as Player, team)
            Bukkit.getServer().pluginManager.callEvent(event)
        }
    }

    /**
     * Gets called when a team wins the game.
     */
    override fun onWin(team: Team, teamMeta: TeamMeta<Location, ItemStack>) {
        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle

        val players = ArrayList(getPlayers())
        val additionalPlayers = getAdditionalNotificationPlayers()
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p -> screenMessageService.setTitle(p, winMessageTitle.replaceGamePlaceholder(this), winMessageSubTitle.replaceGamePlaceholder(this)) }

        val event = GameWinEvent(this, team)
        Bukkit.getServer().pluginManager.callEvent(event)

        this.close()
    }

    /** Join the game. */
    override fun join(player: Player, team: Team?): Boolean {
        if (!this.arena.enabled || closed) {
            return false
        }

        if (!this.isAllowedToJoinWithPermissions(player)) {
            return false
        }

        this.leave(player)

        var joiningTeam = team
        if (arena.meta.lobbyMeta.onlyAllowEventTeams) {
            if (joiningTeam == Team.RED && this.redTeam.size > this.blueTeam.size) {
                joiningTeam = null
            } else if (joiningTeam == Team.BLUE && this.blueTeam.size > this.redTeam.size) {
                joiningTeam = null
            }
        }

        if (joiningTeam == null) {
            joiningTeam = Team.BLUE
            if (this.redTeam.size < this.blueTeam.size) {
                joiningTeam = Team.RED
            }
        }

        if (joiningTeam == Team.RED && this.redTeam.size < this.arena.meta.redTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, joiningTeam, this.arena.meta.redTeamMeta)
            this.redTeam.add(player)

            val event = GameJoinEvent(this, player)
            Bukkit.getServer().pluginManager.callEvent(event)

            return true
        } else if (joiningTeam == Team.BLUE && this.blueTeam.size < this.arena.meta.blueTeamMeta.maxAmount) {
            this.prepareStatsForPlayer(player, joiningTeam, this.arena.meta.blueTeamMeta)
            this.blueTeam.add(player)

            val event = GameJoinEvent(this, player)
            Bukkit.getServer().pluginManager.callEvent(event)

            return true
        }

        return false
    }

    /** Leave the game. */
    override fun leave(player: Player) {
        super.leave(player)
        if (!ingameStats.containsKey(player))
            return
        val event = GameLeaveEvent(this, player)
        Bukkit.getServer().pluginManager.callEvent(event)
    }

    /**
     * Thread save method to listen on the second tick cycle of the game.
     */
    override fun onTwentyTicks() {
        super.onTwentyTicks()
        if (this.arena.gameType != GameType.HUBGAME) {
            this.close()
            return
        }

        if (this.arena.meta.hubLobbyMeta.resetArenaOnEmpty
                && this.ingameStats.isEmpty() && (redPoints > 0 || bluePoints > 0)) {
            this.close()
        }
    }

    private fun prepareStatsForPlayer(player: Player, team: Team, teamMeta: TeamMeta<Location, ItemStack>) {
        val stats = PlayerStorage(player)
        stats.team = team
        stats.storeForType(GameType.HUBGAME)
        this.ingameStats[player] = stats

        player.gameMode = arena.meta.lobbyMeta.gamemode as GameMode
        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()
        player.inventory.contents = teamMeta.inventoryContents.clone()
        player.inventory.armorContents = teamMeta.armorContents.clone()
        player.updateInventory()

        if (teamMeta.spawnpoint == null) {
            player.teleport(arena.meta.ballMeta.spawnpoint!!.toBukkitLocation())
        } else {
            player.teleport(teamMeta.spawnpoint!!.toBukkitLocation())
        }

        player.sendMessage(Config.prefix + teamMeta.joinMessage.replaceGamePlaceholder(this, teamMeta))
    }
}