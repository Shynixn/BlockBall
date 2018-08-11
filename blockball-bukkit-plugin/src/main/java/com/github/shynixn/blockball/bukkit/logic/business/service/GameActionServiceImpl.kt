package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.bukkit.event.GameLeaveEvent
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.extension.isLocationInSelection
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Item
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.util.Vector
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
/*class GameActionServiceImpl<in G : Game> @Inject constructor(private val plugin: Plugin, private val gameHubGameActionService: GameHubGameActionService, private val bossBarService: BossBarService, private val configurationService: ConfigurationService, private val hubGameActionService: GameHubGameActionService, private val minigameActionService: GameMiniGameActionService<MiniGame>, private val bungeeCordGameActionService: GameBungeeCordGameActionService, private val scoreboardService: ScoreboardService) : GameActionService<G> {
 /*   private val prefix = configurationService.findValue<String>("messages.prefix")
    private val version = VersionSupport.getServerVersion()

    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: G, player: P, team: Team?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets called when a goal gets scored on the given [game] by the given [team].
     */
    override fun onScore(game: G, team: Team, teamMeta: TeamMeta) {
        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = game.notifiedPlayers
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p -> screenMessageService.setTitle(p, scoreMessageTitle.replaceGamePlaceholder(this), scoreMessageSubTitle.replaceGamePlaceholder(this)) }

        if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
            val event = GoalShootEvent(this, lastInteractedEntity as Player, team)
            Bukkit.getServer().pluginManager.callEvent(event)
        }
    }


    /**
     * Returns if the given [player] is allowed to join the match.
     */
    protected fun isAllowedToJoinWithPermissions(player: Player): Boolean {
        if (player.hasPermission(Permission.JOIN.permission + ".all")
                || player.hasPermission(Permission.JOIN.permission + "." + this.arena.name)) {
            return true
        }

        player.sendMessage(Config.prefix + Config.joinGamePermissionmessage)

        return false
    }

    /**
     * Gets called when the given [game] gets win by the given [team].
     */
    override fun onWin(game: G, team: Team, teamMeta: TeamMeta) {
        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = game.notifiedPlayers
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p -> screenMessageService.setTitle(p, winMessageTitle.replaceGamePlaceholder(this), winMessageSubTitle.replaceGamePlaceholder(this)) }


        val event = GameWinEvent(this, team)
        Bukkit.getServer().pluginManager.callEvent(event)

        this.close()
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: G) {
        if (!game.closed) {
            return
        }

        game.status = GameStatus.DISABLED
        game.closed = true
        game.ingamePlayersStorage.keys.toTypedArray().map { p -> p is Player }.forEach { p -> leaveGame(game, p) }



        if (!closed) {
            status =
                    closed = true
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

    /**
     * Lets the given [player] leave join the given [game].
     * Does nothing if the player is already in a Game.
     */
    override fun <P> joinGame(game: G, player: P) {
        /**
         * Returns if the given [player] is allowed to join the match.
         */
        protected fun isAllowedToJoinWithPermissions(player: Player): Boolean {
            if (player.hasPermission(Permission.JOIN.permission + ".all")
                    || player.hasPermission(Permission.JOIN.permission + "." + this.arena.name)) {
                return true
            }

            player.sendMessage(Config.prefix + Config.joinGamePermissionmessage)

            return false
        }
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun <P> leaveGame(game: G, player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val event = GameLeaveEvent(player, game)
        Bukkit.getServer().pluginManager.callEvent(event)

        if (event.Cancelled) {
            return
        }

        if (game.scoreboard != null && player.scoreboard == game.scoreboard) {
            player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        }

        if (game.bossBar != null) {
            bossBarService.removePlayer(game.bossBar, player)
        }

        game.holograms.forEach { h ->
            h.removeWatcher(player)
        }

        if (game is HubGame) {
            hubGameActionService.leaveGame(game, player)
        }
        if (game is MiniGame) {
            minigameActionService.leaveGame(game, player)
        }
        if (game is BungeeCordGame) {
            bungeeCordGameActionService.leaveGame(game, player)
        }

        if (game.ingamePlayersStorage.containsKey(player)) {
            val storage = game.ingamePlayersStorage[player]!!

            if (storage.team == Team.RED) {
                player.sendMessage(prefix + game.arena.meta.redTeamMeta.leaveMessage)
            } else if (storage.team == Team.BLUE) {
                player.sendMessage(prefix + game.arena.meta.blueTeamMeta.leaveMessage)
            }

            game.ingamePlayersStorage.remove(player)
        }

        if (game.arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(game.arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
        }
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: G, ticks: Int) {
        if (!game.arena.enabled || game.closing) {
            game.status = GameStatus.DISABLED
            onUpdateSigns(game)
            if (!game.ingamePlayersStorage.isEmpty() && game.arena.gameType != GameType.BUNGEE) {
                game.closing = true
            }
            return
        }

        if (game.status == GameStatus.DISABLED) {
            game.status = GameStatus.ENABLED
        }

        if (Bukkit.getWorld(game.arena.meta.ballMeta.spawnpoint!!.worldName) == null) {
            return
        }

        if (game is HubGame) {
            hubGameActionService.handle(game, ticks)
        }
        if (game is MiniGame) {
            minigameActionService.handle(game, ticks)
        }
        if (game is BungeeCordGame) {
            bungeeCordGameActionService.handle(game, ticks)
        }

        if (ticks % 20 != 0) {
            if (game.closing) {
                return
            }

            this.kickUnwantedEntitiesOutOfForcefield(game)
            this.onUpdateSigns(game)
        }




        this.onTick()
        if (this.haveTwentyTicksPassed()) {

            this.updateScoreboard()
            this.updateBossBar()
            this.updateDoubleJumpCooldown()
            this.updateHolograms()
            this.updateDoubleJump()
        }
    }

    private fun onUpdateSigns(game: Game) {
        try {
            for (i in game.arena.meta.redTeamMeta.signs.indices) {
                val position = game.arena.meta.redTeamMeta.signs[i]
                if (!replaceTextOnSign(game, position, game.arena.meta.redTeamMeta.signLines, game.arena.meta.redTeamMeta)) {
                    game.arena.meta.redTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.blueTeamMeta.signs.indices) {
                val position = game.arena.meta.blueTeamMeta.signs[i]
                if (!replaceTextOnSign(game, position, game.arena.meta.blueTeamMeta.signLines, game.arena.meta.blueTeamMeta)) {
                    game.arena.meta.blueTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.lobbyMeta.joinSigns.indices) {
                val position = game.arena.meta.lobbyMeta.joinSigns[i]
                if (!replaceTextOnSign(game, position, game.arena.meta.lobbyMeta.joinSignLines, null)) {
                    game.arena.meta.lobbyMeta.joinSigns.removeAt(i)
                    return
                }
            }
            for (i in game.arena.meta.lobbyMeta.leaveSigns.indices) {
                val position = game.arena.meta.lobbyMeta.leaveSigns[i]
                if (!replaceTextOnSign(game, position, game.arena.meta.lobbyMeta.leaveSignLines, null)) {
                    game.arena.meta.lobbyMeta.leaveSigns.removeAt(i)
                    return
                }
            }
        } catch (e: Exception) { // Removing sign task could clash with updating signs.
            plugin.logger.log(Level.INFO, "Sign update was cached.", e)
        }
    }

    private fun replaceTextOnSign(game: Game, signPosition: Position, lines: List<String>, teamMeta: TeamMeta?): Boolean {
        var players = game.redTeam
        if (game.arena.meta.blueTeamMeta == teamMeta) {
            players = game.blueTeam
        }
        val location = signPosition.toLocation()
        if (location.block.type != Material.SIGN_POST && location.block.type != Material.WALL_SIGN)
            return false
        val sign = location.block.state as Sign
        for (i in lines.indices) {
            val text = lines[i]
            sign.setLine(i, text.replaceGamePlaceholder(this, teamMeta, players as List<Player>))
        }
        sign.update(true)
        return true
    }

    private fun kickUnwantedEntitiesOutOfForcefield(game: G) {
        if (game.arena.meta.protectionMeta.entityProtectionEnabled) {
            game.arena.meta.ballMeta.spawnpoint!!.toLocation().world.entities.forEach { p ->
                if (p !is Player && p !is ArmorStand && p !is Item && p !is ItemFrame) {
                    if (game.arena.isLocationInSelection(p.location)) {
                        val vector = game.arena.meta.protectionMeta.entityProtection
                        p.location.direction = vector as Vector
                        p.velocity = vector
                    }
                }
            }
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
            val players = ArrayList(getPlayers())
            val additionalPlayers = getAdditionalNotificationPlayers()
            players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

            players.forEach { p ->
                if (!holo.containsPlayer(p)) {
                    holo.addPlayer(p)
                }
            }

            additionalPlayers.filter { p -> !p.second }.forEach { p ->
                if (holo.containsPlayer(p.first)) {
                    holo.removePlayer(p.first)
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
                val players = ArrayList(getPlayers())
                val additionalPlayers = getAdditionalNotificationPlayers()
                players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

                val bossbarPlayers = bossbar!!.players

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    if (bossbarPlayers.contains(p.first)) {
                        bossbar!!.removePlayer(p.first)
                    }
                }

                bossbar!!.addPlayer(players)
                bossbar!!.message = meta.message.replaceGamePlaceholder(this)
            }
        } else if (dependencyService.isInstalled(PluginDependency.BOSSBARAPI)) {
            if (arena.meta.bossBarMeta.enabled) {
                val percentage = meta.percentage

                val players = ArrayList(getPlayers())
                val additionalPlayers = getAdditionalNotificationPlayers()
                players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    bossbarApiService.removeBossbarMessage(p.first)
                }

                players.forEach { p ->
                    bossbarApiService.setBossbarMessage(p, meta.message.replaceGamePlaceholder(this), percentage)
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

    /**
     * Updates the scoreboard for all players when enabled.
     */
    private fun updateScoreboard(game : Game) {
        if (!game.arena.meta.scoreboardMeta.enabled) {
            return
        }

        if (game.scoreboard == null) {
            game.scoreboard = Bukkit.getScoreboardManager().newScoreboard

            scoreboardService.setConfiguration(game.scoreboard, DisplaySlot.SIDEBAR, game.arena.meta.scoreboardMeta.title)
        }

        val players = ArrayList(game.inGamePlayers)
        val additionalPlayers = getAdditionalNotificationPlayers()
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        additionalPlayers.filter { p -> !p.second }.forEach { p ->
            if (p.first.scoreboard == scoreboard) {
                p.first.scoreboard = Bukkit.getScoreboardManager().newScoreboard
            }
        }

        players.forEach { p ->
            if (scoreboard != null) {
                if (p.scoreboard != scoreboard) {
                    p.scoreboard = this.scoreboard
                }

                val lines = arena.meta.scoreboardMeta.lines

                var j = lines.size
                for (i in 0 until lines.size) {
                    val line = lines[i].replaceGamePlaceholder(this)
                    scoreboardService.setLine(scoreboard, j, line)
                    j--
                }
            }
        }
    }

    protected fun onMatchEnd(winningPlayers: List<Player>?, loosingPlayers: List<Player>?) {
        if (dependencyService.isInstalled(PluginDependency.VAULT)) {
            if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.WIN_MATCH) && winningPlayers != null) {
                winningPlayers.forEach { p ->
                    vaultDependencyVaultService.addMoney(p, arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.WIN_MATCH]!!.toDouble())
                }
            }

            if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.LOOSING_MATCH) && loosingPlayers != null) {
                loosingPlayers.forEach { p ->
                    vaultDependencyVaultService.addMoney(p, arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.LOOSING_MATCH]!!.toDouble())
                }
            }
            if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.PARTICIPATE_MATCH)) {
                getPlayers().forEach { p ->
                    vaultDependencyVaultService.addMoney(p, arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.PARTICIPATE_MATCH]!!.toDouble())
                }
            }
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
                if (arena.meta.rewardMeta.moneyReward.containsKey(RewardMeta.RewardedAction.SHOOT_GOAL)) {
                    vaultDependencyVaultService.addMoney(lastInteractedEntity, arena.meta.rewardMeta.moneyReward[RewardMeta.RewardedAction.SHOOT_GOAL]!!.toDouble())
                }
                if (arena.meta.rewardMeta.commandReward.containsKey(RewardMeta.RewardedAction.SHOOT_GOAL)) {
                    this.executeCommand(arena.meta.rewardMeta.commandReward[RewardMeta.RewardedAction.SHOOT_GOAL]!!, kotlin.collections.arrayListOf(lastInteractedEntity as Player))
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


    /**
     * Returns a list of players which can be also notified
     */
    private fun getAdditionalNotificationPlayers(game : Game): MutableList<Pair<Player, Boolean>> {
        if (!game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            return ArrayList()
        }

        val players = ArrayList<Pair<Player, Boolean>>()
        val center = game.arena.center as Location

        center.world.players.forEach { p ->
            if (!hasJoined(p)) {
                if (p.location.distance(center) <= arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(p, true))
                } else {
                    players.add(Pair(p, false))
                }
            }
        }

        return players
    }*/
}*/