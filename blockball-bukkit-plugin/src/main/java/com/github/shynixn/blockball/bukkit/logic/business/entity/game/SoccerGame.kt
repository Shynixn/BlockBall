package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.ball.api.BallApi
import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.RewardMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.dependencies.RegisterHelper
import com.github.shynixn.blockball.bukkit.logic.business.helper.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toBukkitLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
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
abstract class SoccerGame(arena: BukkitArena) : LowLevelGame(arena) {

    /** Ball of the game. */
    override var ball: BukkitBall? = null

    private var ballSpawning = false
    private var ballSpawnCounter = 0
    private var bumperCounter = 0
    private var bumper = 20
    protected var blockBallSpawning = false
    private var lastBallLocation: Location? = null
    var lastInteractedEntity: Entity? = null

    /**
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        super.run()
        this.fixBallPositionSpawn()
        this.checkBallInGoal()
        this.handleBallSpawning()
    }

    /**
     * Gets called when a player scores a point for the given team.
     */
    protected abstract fun onScore(team: Team, teamMeta: TeamMeta<Location, ItemStack>)

    /**
     * Gets called when a team wins the game.
     */
    protected abstract fun onWin(team: Team, teamMeta: TeamMeta<Location, ItemStack>)

    protected fun onMatchEnd(winningPlayers: List<Player>?, loosingPlayers: List<Player>?) {
        if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.WIN_MATCH) && winningPlayers != null) {
            RegisterHelper.addMoney(arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.WIN_MATCH]!!.toDouble(), winningPlayers)
        }
        if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.LOOSING_MATCH) && loosingPlayers != null) {
            RegisterHelper.addMoney(arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.LOOSING_MATCH]!!.toDouble(), loosingPlayers)
        }
        if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.PARTICIPATE_MATCH)) {
            RegisterHelper.addMoney(arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.PARTICIPATE_MATCH]!!.toDouble(), getPlayers())
        }
        if (arena.meta.rewardMeta.commandReward.containsKey(RewardMeta.RewardedAction.WIN_MATCH) && winningPlayers != null) {
            this.executeCommand(arena.meta.rewardMeta.commandReward[RewardMeta.RewardedAction.WIN_MATCH]!!, winningPlayers)
        }
        if (arena.meta.rewardMeta.commandReward.containsKey(RewardMeta.RewardedAction.LOOSING_MATCH) && loosingPlayers != null) {
            this.executeCommand(arena.meta.rewardMeta.commandReward[RewardMeta.RewardedAction.LOOSING_MATCH]!!, loosingPlayers)
        }
        if (arena.meta.rewardMeta.commandReward.containsKey(RewardMeta.RewardedAction.PARTICIPATE_MATCH)) {
            this.executeCommand(arena.meta.rewardMeta.commandReward[RewardMeta.RewardedAction.PARTICIPATE_MATCH]!!, getPlayers())
        }
    }

    private fun onScoreReward(players: List<Player>) {
        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            if (players.contains(lastInteractedEntity!!)) {
                val list = ArrayList<Player>()
                list.add(lastInteractedEntity as Player)
                if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.SHOOT_GOAL)) {
                    RegisterHelper.addMoney(arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.SHOOT_GOAL]!!.toDouble(), list)
                }
                if (arena.meta.rewardMeta.commandReward.containsKey(RewardMeta.RewardedAction.SHOOT_GOAL)) {
                    this.executeCommand(arena.meta.rewardMeta.commandReward[RewardMeta.RewardedAction.SHOOT_GOAL]!!, list)
                }
            }
        }
    }

    private fun executeCommand(meta: CommandMeta, players: List<Player>) {
        var command = meta.command
        if (command!!.startsWith("/")) {
            command = command.substring(1, command.length)
        }
        if (command.equals("none", true)) {
            return
        }
        when {
            meta.mode == CommandMeta.CommandMode.PER_PLAYER -> players.forEach { p ->
                p.performCommand(command.replaceGamePlaceholder(this))
            }
            meta.mode == CommandMeta.CommandMode.CONSOLE_PER_PLAYER -> players.forEach { p ->
                lastInteractedEntity = p
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceGamePlaceholder(this))
            }
            meta.mode == CommandMeta.CommandMode.CONSOLE_SINGLE -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceGamePlaceholder(this))
        }
    }

    private fun fixBallPositionSpawn() {
        if (ball == null || ball!!.isDead)
            return
        if (!this.arena.isLocationInSelection(this.ball!!.location)) {
            if (this.bumper == 0) {
                this.rescueBall()
            }
        } else {
            this.bumperCounter = 0
            this.lastBallLocation = this.ball!!.location.clone()
        }
        if (this.ingameStats.isEmpty()) {
            this.ball!!.remove()
        }
        if (this.bumper > 0) {
            this.bumper--
        }
    }

    private fun rescueBall() {
        if (this.lastBallLocation != null) {
            val ballLocation = this.ball!!.location
            val knockback = this.lastBallLocation!!.toVector().subtract(ballLocation.toVector())
            ballLocation.direction = knockback
            this.ball!!.hitBox.velocity = knockback
            val direction = this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation().toVector().subtract(ballLocation.toVector())
            this.ball!!.hitBox.velocity = direction.multiply(0.1)
            this.bumper = 40
            this.bumperCounter++
            if (this.bumperCounter == 5) {
                this.ball!!.teleport(this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation())
            }
        }
    }

    private fun checkBallInGoal() {
        if (ball == null || ball!!.isDead)
            return
        if (this.arena.meta.redTeamMeta.goal.isLocationInSelection(this.ball!!.location)) {
            this.blueGoals++
            this.ball!!.remove()
            this.onScore(Team.BLUE, this.arena.meta.blueTeamMeta)
            this.onScoreReward(blueTeam)
            this.teleportBackToSpawnpoint()
            if (this.blueGoals >= this.arena.meta.lobbyMeta.maxScore) {
                this.onMatchEnd(blueTeam, redTeam)
                this.onWin(Team.BLUE, this.arena.meta.blueTeamMeta)
            }
        } else if (this.arena.meta.blueTeamMeta.goal.isLocationInSelection(this.ball!!.location)) {
            this.redGoals++
            this.ball!!.remove()
            this.onScore(Team.RED, this.arena.meta.redTeamMeta)
            this.onScoreReward(redTeam)
            this.teleportBackToSpawnpoint()
            if (this.redGoals >= this.arena.meta.lobbyMeta.maxScore) {
                this.onMatchEnd(redTeam, blueTeam)
                this.onWin(Team.RED, this.arena.meta.redTeamMeta)
            }
        }
    }

    /**
     * Teleports all players back to their spawnpoint if [arena] has got back teleport enabled.
     */
    private fun teleportBackToSpawnpoint() {
        if (!this.arena.meta.customizingMeta.backTeleport) {
            return
        }

        Bukkit.getScheduler().runTaskLater(plugin, {
            var redTeamSpawnpoint = this.arena.meta.redTeamMeta.spawnpoint?.toBukkitLocation()
            if (redTeamSpawnpoint == null) {
                redTeamSpawnpoint = this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation()
            }

            var blueTeamSpawnpoint = this.arena.meta.blueTeamMeta.spawnpoint?.toBukkitLocation()
            if (blueTeamSpawnpoint == null) {
                blueTeamSpawnpoint = this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation()
            }

            this.ingameStats.forEach { i ->
                if (i.value.team == Team.RED) {
                    i.key.teleport(redTeamSpawnpoint)
                } else if (i.value.team == Team.BLUE) {
                    i.key.teleport(blueTeamSpawnpoint)
                }
            }
        }, 20L * this.arena.meta.customizingMeta.backTeleportDelay)
    }

    private fun handleBallSpawning() {
        if (blockBallSpawning) {
            return
        }
        if (this.ballSpawning) {
            this.ballSpawnCounter--
            if (this.ballSpawnCounter <= 0) {
                this.ball = BallApi.spawnTemporaryBall(this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation(), this.arena.meta.ballMeta)
                this.ballSpawning = false
                this.ballSpawnCounter = 0
            }
        } else if ((this.ball == null || this.ball!!.isDead)
                && (!this.redTeam.isEmpty() || !this.blueTeam.isEmpty())
                && (this.redTeam.size >= this.arena.meta.redTeamMeta.minAmount && this.blueTeam.size >= this.arena.meta.blueTeamMeta.minAmount)) {
            this.ballSpawning = true
            this.ballSpawnCounter = this.arena.meta.ballMeta.delayInTicks
        }
    }
}