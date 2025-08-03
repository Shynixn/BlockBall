package com.github.shynixn.blockball.impl

import checkForPluginMainThread
import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerHubGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class SoccerHubGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    plugin: Plugin,
    placeHolderService: PlaceHolderService,
    language: BlockBallLanguage,
    packetService: PacketService,
    soccerBallFactory: SoccerBallFactory,
    commandService: CommandService,
    itemService: ItemService,
    chatMessageService: ChatMessageService
) : SoccerGameImpl(
    arena,
    placeHolderService,
    plugin,
    soccerBallFactory,
    commandService,
    language,
    playerDataRepository,
    itemService,
    chatMessageService
),
    SoccerHubGame {
    /**
     * Handles the game actions per tick.
     */
    override fun handle(hasSecondPassed: Boolean) {
        checkForPluginMainThread()

        // Handle HubGame ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            close()
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.ballSpawnPoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            setGameClosing()
        }

        if (ball == null) {
            if (redTeam.size >= arena.meta.redTeamMeta.minAmount && blueTeam.size >= arena.meta.blueTeamMeta.minAmount && ingamePlayersStorage.isNotEmpty()) {
                respawnBall(arena.meta.customizingMeta.gameStartBallSpawnDelayTicks)
            }
        }

        if (ball != null && ingamePlayersStorage.isEmpty()) {
            destroyBall()
        }

        // Handle SoccerBall.
        this.handleBallSpawning()
        // TODO: Minigame essentials. Update signs and protections.
        super.handleMiniGameEssentials(hasSecondPassed)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        checkForPluginMainThread()

        if (closed) {
            return
        }

        status = GameState.DISABLED
        closed = true
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
    }

    override fun setPlayerToArena(player: Player, team: Team) {
        checkForPluginMainThread()

        if (arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.respawn(player)
        } else {
            val velocityIntoArena = player.location.direction.normalize().multiply(0.5)
            player.velocity = velocityIntoArena
        }
    }
}
