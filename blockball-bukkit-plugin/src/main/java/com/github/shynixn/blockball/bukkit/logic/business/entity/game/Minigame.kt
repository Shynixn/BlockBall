package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.ball.bukkit.core.logic.persistence.entity.SoundBuilder
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.entity.container.PlayerStorage
import com.github.shynixn.blockball.bukkit.logic.business.helper.sendScreenMessage
import com.github.shynixn.blockball.bukkit.logic.business.helper.toBukkitLocation
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
open class Minigame(arena: BukkitArena) : SoccerGame(arena) {

    private val blingsound = SoundBuilder("NOTE_PLING", 1.0, 2.0)
    protected var isLobbyCountdownRunning: Boolean = false
    private var lobbyCountdown: Int = 0
    protected var isGameRunning: Boolean = false
    private var gameCountdown: Int = 0

    /**
     * Gets called when a player scores a point for the given team.
     */
    override fun onScore(teamMeta: TeamMeta<Location, ItemStack>) {
        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle
        this.getPlayers().forEach { p -> p.sendScreenMessage(scoreMessageTitle, scoreMessageSubTitle, this) }
    }

    /**
     * Gets called when a team wins the game.
     */
    override fun onWin(teamMeta: TeamMeta<Location, ItemStack>) {
        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle
        this.getPlayers().forEach { p -> p.sendScreenMessage(winMessageTitle, winMessageSubTitle, this) }
        plugin.server.scheduler.runTaskLater(plugin, {
            this.close()
        }, 5 * 20L)
    }

    /** Leave the game. */
    override fun leave(player: Player) {
        super.leave(player)
        val stats = this.ingameStats[player] ?: return
        ingameStats.remove(player)
        stats.resetState()
    }

    /** Join the game. */
    override fun join(player: Player, team: Team?): Boolean {
        this.leave(player)
        if (isLobbyFull()) {
            return false
        }
        this.prepareLobbyStatsForPlayer(player)
        return false
    }


    /**
     * Thread save method to listen on the second tick cycle of the game.
     */
    override fun onTwentyTicks() {
        if (isLobbyCountdownRunning) {
            this.ingameStats.keys.forEach { p ->
                p.exp = (lobbyCountdown.toFloat()) / 100.0F
            }
            lobbyCountdown--
            if (lobbyCountdown < 5) {
                this.playBlingSound()
            }
            if (lobbyCountdown <= 0) {
                gameCountdown = this.arena.meta.minigameMeta.matchDuration
                isLobbyCountdownRunning = false
                isGameRunning = true
                startGame()
            }
        }
        if (!isLobbyCountdownRunning && canStartLobbyCountdown()) {
            isLobbyCountdownRunning = true
            lobbyCountdown = 10
        }
        if (isGameRunning) {
            gameCountdown--
            if (gameCountdown <= 0) {
                isGameRunning = false
                timesUpGame()
            }
        }
    }

    private fun timesUpGame() {
        if (this.redGoals == this.blueGoals) {
            //:TODO MAKE IN A DRAW MESSAGE
        } else if (this.redGoals > this.blueGoals) {
            this.onMatchEnd(this.redTeam, this.blueTeam)
            this.onWin(this.arena.meta.redTeamMeta)
        } else {
            this.onMatchEnd(this.blueTeam, this.redTeam)
            this.onWin(this.arena.meta.blueTeamMeta)
        }
    }

    private fun startGame() {
        ingameStats.keys.forEach { p ->
            val stats = ingameStats[p]
            if (stats!!.team == null) {
                if (redTeam.size < blueTeam.size) {
                    joinTeam(p, arena.meta.redTeamMeta, redTeam)
                } else {
                    joinTeam(p, arena.meta.blueTeamMeta, blueTeam)
                }
            }

            if (stats.team == Team.RED) {
                val teamMeta = arena.meta.redTeamMeta
                if (teamMeta.spawnpoint == null) {
                    p.teleport(arena.meta.ballMeta.spawnpoint!!.toBukkitLocation())
                } else {
                    p.teleport(teamMeta.spawnpoint!!.toBukkitLocation())
                }
            } else {
                val teamMeta = arena.meta.blueTeamMeta
                if (teamMeta.spawnpoint == null) {
                    p.teleport(arena.meta.ballMeta.spawnpoint!!.toBukkitLocation())
                } else {
                    p.teleport(teamMeta.spawnpoint!!.toBukkitLocation())
                }
            }
        }
    }

    private fun joinTeam(player: Player, teamMeta: TeamMeta<Location, ItemStack>, teamPlayers: MutableList<Player>) {
        teamPlayers.add(player)
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()
        player.inventory.armorContents = teamMeta.armorContents.clone()
        player.updateInventory()
        player.sendMessage(Config.prefix + teamMeta.joinMessage)
    }

    private fun isLobbyFull(): Boolean {
        val amount = arena.meta.redTeamMeta.maxAmount + arena.meta.blueTeamMeta.maxAmount
        if (this.ingameStats.size >= amount) {
            return true
        }
        return false
    }

    private fun canStartLobbyCountdown(): Boolean {
        val amount = arena.meta.redTeamMeta.minAmount + arena.meta.blueTeamMeta.minAmount
        if (!isGameRunning && this.ingameStats.size >= amount) {
            return true
        }
        return false
    }

    private fun playBlingSound() {
        try {
            this.blingsound.apply(this.ingameStats.keys)
        } catch (e: Exception) {
            Bukkit.getServer().consoleSender.sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [BlingSound]")
        }
    }

    private fun prepareLobbyStatsForPlayer(player: Player) {
        val stats = PlayerStorage(player)
        stats.storeForType(GameType.MINIGAME)

        player.allowFlight = false
        player.isFlying = false
        player.maxHealth = 20.0
        player.health = 20.0
        player.foodLevel = 20
        player.level = 0
        player.exp = 0.0F

        player.inventory.armorContents = arrayOfNulls(4)
        player.inventory.clear()

        player.updateInventory()
        player.teleport(arena.meta.minigameMeta.lobbySpawnpoint!!.toBukkitLocation())

        this.ingameStats[player] = stats
    }
}