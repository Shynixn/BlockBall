package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.bukkit.business.entity.BukkitGame
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.entity.InGameStats
import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.dependencies.RegisterHelper
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.GameScoreboard
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.SimpleBossBar
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.SimpleHologram
import com.github.shynixn.blockball.bukkit.logic.business.helper.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toBukkitLocation
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

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
abstract class LowLevelGame(
        /** Arena of the game. */
        override val arena: BukkitArena) : BukkitGame, Runnable {
    /** Ingame stats of the players. */
    override val ingameStats: MutableMap<Player, InGameStats> = HashMap()
    /** Status of the game. */
    override var status: GameStatus = GameStatus.ENABLED
    /** List of players in the redTeam. */
    override val redTeam: MutableList<Player> = ArrayList()
    /** List of players in the blueTeam. */
    override val blueTeam: MutableList<Player> = ArrayList()

    protected var plugin: Plugin = JavaPlugin.getPlugin(BlockBallPlugin::class.java)
    protected var redGoals = 0
    protected var blueGoals = 0
    var doubleJumpCooldownPlayers: MutableMap<Player, Int> = HashMap()
    var closed: Boolean = false
    private var bumperTimer = 20L

    private var scoreboard: GameScoreboard? = null

    private var bossbar: SimpleBossBar? = null

    private var holograms: MutableList<SimpleHologram> = ArrayList()

    /** Amount of points the blue team has scored. */
    override val bluePoints: Int
        get() = blueGoals
    /** Amount of points the red team has scored.  */
    override val redPoints: Int
        get() = redGoals

    /**
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        if (!this.arena.enabled || closed) {
            status = GameStatus.DISABLED
            onUpdateSigns()
            if (!this.ingameStats.isEmpty() && this.arena.gameType != GameType.BUNGEE) {
                close()
            }
            return
        }

        if (status == GameStatus.DISABLED) {
            status = GameStatus.ENABLED
        }

        if (Bukkit.getWorld(this.arena.meta.ballMeta.spawnpoint!!.worldName) == null) {
            return
        }

        this.onTick()
        if (this.haveTwentyTicksPassed()) {
            this.onTwentyTicks()

            if (closed) {
                return
            }

            this.kickUnwantedEntitiesOutOfForcefield()
            this.onUpdateSigns()
            this.updateScoreboard()
            this.updateBossBar()
            this.updateDoubleJumpCooldown()
            this.updateHolograms()
            this.updateDoubleJump()
        }
    }

    /**
     * Thread save method to listen on the tick cycle of the game.
     */
    protected open fun onTick() {}

    /**
     * Thread save method to listen on the second tick cycle of the game.
     */
    protected open fun onTwentyTicks() {}

    /** Leave the game. */
    override fun leave(player: Player) {
        if (!ingameStats.containsKey(player))
            return
        val stats = this.ingameStats[player]
        if (stats!!.team == Team.RED) {
            this.redTeam.remove(player)
            player.sendMessage(Config.prefix + arena.meta.redTeamMeta.leaveMessage)
        } else if (stats.team == Team.BLUE) {
            this.blueTeam.remove(player)
            player.sendMessage(Config.prefix + arena.meta.blueTeamMeta.leaveMessage)
        }

        if (bossbar != null) {
            bossbar!!.removePlayer(player)
        }

        if (!VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1) && RegisterHelper.isRegistered("BossBarAPI")) {
            RegisterHelper.setBossBar(player, null, 0.0)
        }

        this.holograms.forEachIndexed { i, holo ->
            if (holo.containsPlayer(player)) {
                holo.removePlayer(player)
            }
        }

        ingameStats.remove(player)
        stats.resetState()

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint!!.toBukkitLocation())
        }
    }

    private fun updateDoubleJump() {
        this.doubleJumpCooldownPlayers.keys.toTypedArray().forEach { p ->
            val cooldown = this.doubleJumpCooldownPlayers[p]!! - 1
            if (cooldown <= 0) {
                doubleJumpCooldownPlayers.remove(p)
            } else {
                this.doubleJumpCooldownPlayers[p] = cooldown
            }
        }
    }

    private fun updateHolograms() {
        if (holograms.size != this.arena.meta.hologramMetas.size) {
            val plugin = JavaPlugin.getPlugin(BlockBallPlugin::class.java) as Plugin
            cleanHolograms()
            this.arena.meta.hologramMetas.indices
                    .map { arena.meta.hologramMetas[it] }
                    .forEach { holograms.add(SimpleHologram(plugin, it.position!!.toBukkitLocation(), it.lines)) }
        }
        this.holograms.forEachIndexed { i, holo ->
            this.getPlayers().forEach { p ->
                if (!holo.containsPlayer(p)) {
                    holo.addPlayer(p)
                }
            }
            val lines = ArrayList(this.arena.meta.hologramMetas[i].lines)
            for (i in lines.indices) {
                lines[i] = lines[i].replaceGamePlaceholder(this)
            }
            holo.setLines(lines)
        }
    }

    private fun cleanHolograms() {
        this.holograms.forEach { p -> p.close() }
        this.holograms.clear()
    }

    private fun updateBossBar() {
        val meta = arena.meta.bossBarMeta
        if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
            if (bossbar == null && arena.meta.bossBarMeta.enabled) {

                bossbar = if (meta.flags.isEmpty()) {
                    SimpleBossBar.from(meta.message, meta.color.name, meta.style.name, "NONE")
                } else {
                    SimpleBossBar.from(meta.message, meta.color.name, meta.style.name, meta.flags[0].name)
                }

                bossbar!!.percentage = meta.percentage / 100.0
            }
            if (bossbar != null) {
                bossbar!!.addPlayer(getPlayers())
                bossbar!!.message = meta.message.replaceGamePlaceholder(this)
            }
        } else if (RegisterHelper.isRegistered("BossBarAPI")) {
            if (arena.meta.bossBarMeta.enabled) {
                val percentage = meta.percentage
                getPlayers().forEach { p ->
                    RegisterHelper.setBossBar(p, meta.message.replaceGamePlaceholder(this), percentage)
                }
            }
        }
    }

    private fun updateDoubleJumpCooldown() {
        doubleJumpCooldownPlayers.keys.toTypedArray().forEach { p ->
            var time = doubleJumpCooldownPlayers[p]!!
            time -= 1
            if (time <= 0) {
                doubleJumpCooldownPlayers.remove(p)
            } else {
                doubleJumpCooldownPlayers[p] = time
            }
        }
    }

    private fun updateScoreboard() {
        if (scoreboard == null && arena.meta.scoreboardMeta.enabled) {
            scoreboard = GameScoreboard(this)
        }
        if (scoreboard != null) {
            getPlayers().forEach { p -> scoreboard!!.updateScoreboard(p) }
        }
    }

    /** Checks if the player has joined the game. */
    override fun hasJoined(player: Player): Boolean {
        return ingameStats.containsKey(player)
    }

    /** Returns all players inside of the game. */
    override fun getPlayers(): List<Player> {
        val list = ArrayList<Player>()
        list.addAll(redTeam)
        list.addAll(blueTeam)
        return list
    }

    private fun onUpdateSigns() {
        try {
            for (i in this.arena.meta.redTeamMeta.signs.indices) {
                val position = this.arena.meta.redTeamMeta.signs[i]
                if (!replaceTextOnSign(position, arena.meta.redTeamMeta.signLines, arena.meta.redTeamMeta)) {
                    this.arena.meta.redTeamMeta.signs.removeAt(i)
                }
            }
            for (i in this.arena.meta.blueTeamMeta.signs.indices) {
                val position = this.arena.meta.blueTeamMeta.signs[i]
                if (!replaceTextOnSign(position, arena.meta.blueTeamMeta.signLines, arena.meta.blueTeamMeta)) {
                    this.arena.meta.blueTeamMeta.signs.removeAt(i)
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
        } catch (e: Exception) { // Removing sign task could clash with updating signs.
            plugin.logger.log(Level.INFO, "Sign update was cached.", e)
        }
    }

    private fun replaceTextOnSign(signPosition: StorageLocation, lines: List<String>, teamMeta: TeamMeta<Location, ItemStack>?): Boolean {
        var players = this.redTeam
        if (arena.meta.blueTeamMeta == teamMeta) {
            players = this.blueTeam
        }
        val location = signPosition.toBukkitLocation()
        if (location.block.type != Material.SIGN_POST && location.block.type != Material.WALL_SIGN)
            return false
        val sign = location.block.state as Sign
        for (i in lines.indices) {
            val text = lines[i]
            sign.setLine(i, text.replaceGamePlaceholder(this, teamMeta, players))
        }
        sign.update(true)
        return true
    }

    private fun kickUnwantedEntitiesOutOfForcefield() {
        if (arena.meta.protectionMeta.entityProtectionEnabled) {
            this.arena.meta.ballMeta.spawnpoint!!.toBukkitLocation().world.entities.forEach { p ->
                if (p !is Player && p !is ArmorStand && p !is Item && p !is ItemFrame) {
                    if (this.arena.isLocationInSelection(p.location)) {
                        val vector = arena.meta.protectionMeta.entityProtection
                        p.location.direction = vector
                        p.velocity = vector
                    }
                }
            }
        }
    }

    private fun haveTwentyTicksPassed(): Boolean {
        this.bumperTimer--
        if (this.bumperTimer <= 0) {
            this.bumperTimer = 20
            return true
        }
        return false
    }


    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        if (!closed) {
            status = GameStatus.DISABLED
            closed = true
            scoreboard?.close()
            ingameStats.keys.toTypedArray().forEach { p -> leave(p); }
            ingameStats.clear()
            ball?.remove()
            redTeam.clear()
            blueTeam.clear()
            bossbar?.close()
            holograms.forEach { h -> h.close() }
            holograms.clear()
        }
    }
}