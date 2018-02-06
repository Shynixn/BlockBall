package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitGame
import com.github.shynixn.blockball.api.business.entity.InGameStats
import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.GameScoreboard
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.SimpleBossBar
import com.github.shynixn.blockball.bukkit.logic.business.helper.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.helper.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toBukkitLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.ArmorStand
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

    protected var redGoals = 0
    protected var blueGoals = 0
    var doubleJumpCooldownPlayers: MutableMap<Player, Int> = HashMap()

    private var bumperTimer = 20L

    private var scoreboard: GameScoreboard? = null

    private var bossbar: SimpleBossBar? = null

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
        if (!this.arena.enabled) {
            if (!this.ingameStats.isEmpty()) {
                close()
            }
        }
        if (this.haveTwentyTicksPassed()) {
            this.kickUnwantedEntitiesOutOfForcefield()
            this.onUpdateSigns()
            this.updateScoreboard()
            this.updateBossBar()
            this.updateDoubleJumpCooldown()
        }
    }

    private fun updateBossBar() {
        val meta = arena.meta.bossBarMeta
        if (bossbar == null && arena.meta.bossBarMeta.enabled) {
            bossbar = SimpleBossBar.from(meta.message, meta.color.name, meta.style.name, meta.flags[0].name)
        }
        if (bossbar != null) {
            bossbar!!.addPlayer(getPlayers())
            bossbar!!.message = meta.message.replaceGamePlaceholder(this)
        }
    }

    private fun updateDoubleJumpCooldown() {
        doubleJumpCooldownPlayers.keys.forEach { p ->
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

    protected abstract fun onUpdateSigns()

    protected fun replaceTextOnSign(signPosition: IPosition, lines: Array<String>, teamMeta: TeamMeta<Location, ItemStack>?): Boolean {
        val maxPlayers = teamMeta?.maxAmount
        var players = redTeam.size
        if (arena.meta.blueTeamMeta == teamMeta) {
            players = blueTeam.size
        }
        val location = signPosition.toBukkitLocation()
        if (location.block.type != Material.SIGN_POST && location.block.type != Material.WALL_SIGN)
            return false
        val sign = location.block.state as Sign
        for (i in lines.indices) {
            var text = lines[i]
            text = text.replace(PlaceHolder.ARENA_DISPLAYNAME.placeHolder, this.arena.displayName)
            text = when {
                this.status == GameStatus.RUNNING -> text.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignRunning!!)
                this.status == GameStatus.ENABLED -> text.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignEnabled!!)
                else -> text.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignDisabled!!)
            }
            if (teamMeta != null) {
                text = text.replace(PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder, teamMeta.displayName)
                        .replace(PlaceHolder.ARENA_TEAMCOLOR.placeHolder, teamMeta.prefix)
            }
            text = text.replace(PlaceHolder.ARENA_CURRENTPLAYERS.placeHolder, players.toString())
                    .replace(PlaceHolder.ARENA_MAXPLAYERS.placeHolder, maxPlayers.toString())
            sign.setLine(i, text.convertChatColors())
        }
        sign.update(true)
        return true
    }

    private fun kickUnwantedEntitiesOutOfForcefield() {
        if (arena.meta.protectionMeta.entityProtectionEnabled) {
            this.arena.meta.ballMeta.spawnpoint.toBukkitLocation().world.entities.forEach { p ->
                if (p !is Player && p !is ArmorStand) {
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
        scoreboard?.close()
        ingameStats.keys.forEach { p -> leave(p); }
        ingameStats.clear()
        ball?.remove()
        redTeam.clear()
        blueTeam.clear()
    }
}