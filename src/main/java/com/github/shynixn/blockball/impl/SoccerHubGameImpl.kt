package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.BossBarService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerHubGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.mcplayerstats.contract.DiscordService
import com.github.shynixn.mcplayerstats.contract.TemplateProcessService
import com.github.shynixn.mcplayerstats.entity.Template
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.Repository
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
    private val bossBarService: BossBarService,
    language: BlockBallLanguage,
    packetService: PacketService,
    soccerBallFactory: SoccerBallFactory,
    commandService: CommandService,
    templateProcessService: TemplateProcessService,
    templateRepository: Repository<Template>,
    discordService: DiscordService,
    itemService: ItemService
) : SoccerGameImpl(
    arena,
    placeHolderService,
    packetService,
    plugin,
    bossBarService,
    soccerBallFactory,
    commandService,
    language,
    playerDataRepository,
    templateProcessService,
    templateRepository,
    discordService,
    itemService
),
    SoccerHubGame {
    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(ticks: Int) {
        // Handle HubGame ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            close()
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            setGameClosing()
        }

        // Handle SoccerBall.
        this.fixBallPositionSpawn()
        this.handleBallSpawning()
        // TODO: Minigame essentials. Update signs and protections.
        super.handleMiniGameEssentials(ticks)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (closed) {
            return
        }

        status = GameState.DISABLED
        closing = true
        closed = true
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()

        if (bossBar != null) {
            bossBarService.cleanResources(bossBar)
        }
    }

    override fun setPlayerToArena(player: Player, team: Team) {
        if (arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.respawn(player)
        } else {
            val velocityIntoArena = player.location.direction.normalize().multiply(0.5)
            player.velocity = velocityIntoArena
        }
    }
}
