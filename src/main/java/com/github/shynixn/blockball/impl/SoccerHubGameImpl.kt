package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.BlockBallPlugin.Companion.gameKey
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.ForceField
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.chat.ClickEvent
import com.github.shynixn.mcutils.common.chat.ClickEventType
import com.github.shynixn.mcutils.common.chat.TextComponent
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

class SoccerHubGameImpl(
    arena: SoccerArena,
    playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    placeHolderService: PlaceHolderService,
    language: BlockBallLanguage,
    soccerBallFactory: SoccerBallFactory,
    commandService: CommandService,
    itemService: ItemService,
    chatMessageService: ChatMessageService,
    cloudService: CloudService,
    private val server: Server,
    private val coroutineHandler: CoroutineHandler,
    private val forceFieldService: ForceFieldService
) : SoccerGameImpl(
    arena,
    placeHolderService,
    plugin,
    soccerBallFactory,
    commandService,
    language,
    playerDataRepository,
    itemService,
    chatMessageService, cloudService,
    coroutineHandler,
    server
),
    SoccerHubGame {
    private val lastJoinPrompt = ConcurrentHashMap<Player, Long>()
    private val forceField: ForceField = ForceField(arena.outerField.corner1!!, arena.outerField.corner2!!).also {
        it.on3dInside = { player ->
            if (!ingamePlayersStorage.containsKey(player)) {
                onPlayerEnterHubGameForceField(player)
            }
        }
        it.on2dOutSide = { player ->
            if (ingamePlayersStorage.containsKey(player)) {
                onPlayerLeaveHubGameForceField(player)
            }
        }
    }

    init {
        forceFieldService.addForceField(forceField)
    }

    /**
     * Handles the game actions per tick.
     */
    override fun handle(hasSecondPassed: Boolean) {
        if (server.getWorld(arena.ballSpawnPoint!!.world!!) == null) {
            return
        }

        if (!arena.enabled || closing) {
            close()
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
        super.handleMiniGameEssentials(hasSecondPassed)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (status == GameState.DISABLED) {
            return
        }

        status = GameState.DISABLED
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
        interactedWithBall.clear()
        forceFieldService.removeForceField(forceField)
        lastJoinPrompt.clear()
        coroutineHandler.execute {
            delay(3000)
            closed = true
        }
    }

    override fun setPlayerToArena(player: Player, team: Team) {
        if (arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.respawn(player, team)
        } else {
            val velocityIntoArena = player.location.direction.normalize().multiply(0.5)
            player.velocity = velocityIntoArena
        }
    }

    private fun onPlayerLeaveHubGameForceField(player: Player) {
        forceFieldService.knockBackOutside(forceField, player)
        val lastJoinPromptTimeStamp = lastJoinPrompt[player]
        val timeStamp = System.currentTimeMillis()
        if (lastJoinPromptTimeStamp != null && timeStamp - lastJoinPromptTimeStamp < 100) {
            return
        }

        lastJoinPrompt[player] = timeStamp
        Bukkit.getServer().dispatchCommand(player, "blockball leave")
        return
    }

    private fun onPlayerEnterHubGameForceField(player: Player) {
        forceFieldService.knockBackOutside(forceField, player)
        val lastJoinPromptTimeStamp = lastJoinPrompt[player]
        val timeStamp = System.currentTimeMillis()
        if (lastJoinPromptTimeStamp != null && timeStamp - lastJoinPromptTimeStamp < 5000) {
            return
        }

        // Makes sure to not execute this too often.
        lastJoinPrompt[player] = timeStamp
        if (arena.meta.hubLobbyMeta.instantForcefieldJoin) {
            coroutineHandler.execute {
                join(player)
            }
            return
        }
        chatMessageService.sendChatMessage(player, TextComponent().also {
            it.components = mutableListOf(
                TextComponent().also {
                    it.text = placeHolderService.resolvePlaceHolder(
                        language.hubGameJoinHeader.text,
                        player,
                        mapOf(gameKey to arena.name)
                    ) + "\n"
                },
                TextComponent().also {
                    it.text = placeHolderService.resolvePlaceHolder(
                        language.hubGameJoinRed.text,
                        player,
                        mapOf(gameKey to arena.name)
                    ) + "\n"
                    it.clickEvent = ClickEvent(
                        ClickEventType.RUN_COMMAND,
                        "/blockball join ${arena.name} red"
                    )
                },
                TextComponent().also {
                    it.text = placeHolderService.resolvePlaceHolder(
                        language.hubGameJoinBlue.text,
                        player,
                        mapOf(gameKey to this.arena.name)
                    )
                    it.clickEvent = ClickEvent(
                        ClickEventType.RUN_COMMAND,
                        "/blockball join ${arena.name} blue"
                    )
                }
            )
        })
    }
}

