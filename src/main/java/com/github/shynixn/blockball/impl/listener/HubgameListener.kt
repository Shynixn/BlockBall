package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.BlockBallPlugin.Companion.gameKey
import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.InteractionCache
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.chat.ClickEvent
import com.github.shynixn.mcutils.common.chat.ClickEventType
import com.github.shynixn.mcutils.common.chat.TextComponent
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

class HubgameListener(
    private val plugin: Plugin,
    private val gameService: GameService,
    private val chatMessageService: ChatMessageService,
    private val placeholderService: PlaceHolderService,
    private val language: BlockBallLanguage
) : Listener {
    private val cache = ConcurrentHashMap<Player, InteractionCache>()

    /** Handles the forcefield of hubGames. */
    @EventHandler
    fun onPlayerMoveAgainstHubForceField(event: PlayerMoveEvent) {
        if (event.to == null || event.to!!.distance(event.from) <= 0) {
            return
        }

        checkForForcefieldInteractions(event.player, event.to!!)
    }

    /**
     * Gets called when the player teleports into the hubfield directly.
     */
    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        checkForForcefieldInteractions(event.player, event.to!!)
    }

    /**
     * Gets called when a player leaves the server and the game.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        cleanResources(event.player)
    }

    /**
     * The [event] gets called when a player toggles flight with double space pressing and
     * disables flying when it's a player in a game that simply used double jump.
     */
    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (event.player.gameMode == GameMode.CREATIVE) {
            return
        }

        val interactionCache = getInteractionCache(event.player)

        if (!interactionCache.toggled) {
            return
        }

        event.player.allowFlight = false
        event.player.isFlying = false
        event.isCancelled = true
        interactionCache.toggled = false
    }

    /**
     * Checks and executes the forcefield actions if the given [player]
     * is going to the given [location].
     */
    private fun checkForForcefieldInteractions(player: Player, location: Location) {
        if (player.gameMode == GameMode.SPECTATOR) {
            // Explicitly ignore spectators because it would toggle off fly.
            return
        }

        val interactionCache = getInteractionCache(player)

        if (System.currentTimeMillis() - interactionCache.joinTimeStamp < 4000) {
            return
        }

        val gameInternal = gameService.getByPlayer(player)

        if (gameInternal != null) {
            if (gameInternal.arena.gameType == GameType.HUBGAME) {
                if (!gameInternal.arena.outerField.isLocationIn2dSelection(location.toVector3d())) {
                    plugin.launch {
                        gameInternal.leave(player)
                        chatMessageService.sendLanguageMessage(player, language.leftGameMessage)
                    }
                }
            } else if ((gameInternal.arena.gameType == GameType.MINIGAME || gameInternal.arena.gameType == GameType.REFEREEGAME) && gameInternal.arena.meta.minigameMeta.forceFieldEnabled) {
                if (gameInternal.status == GameState.RUNNING && !gameInternal.arena.outerField.isLocationIn2dSelection(location.toVector3d())) {
                    val knockBack =
                        gameInternal.arena.ballSpawnPoint!!.toLocation().toVector().subtract(player.location.toVector())
                            .normalize().multiply(2.0)
                    player.location.direction = knockBack
                    player.velocity = knockBack
                    player.allowFlight = true
                }
            }

            return
        }

        var inArea = false

        for (game in gameService.getAll()) {
            if (game.arena.enabled && !game.closed && !game.closing && game.arena.gameType == GameType.HUBGAME && game.arena.outerField.isLocationInSelection(
                    location.toVector3d()
                )
            ) {
                inArea = true
                if (game.arena.meta.hubLobbyMeta.instantForcefieldJoin) {
                    plugin.launch {
                        game.join(player)
                    }
                    return
                }

                if (interactionCache.lastPosition == null) {
                    if (game.arena.meta.protectionMeta.rejoinProtectionEnabled) {
                        player.velocity = game.arena.meta.protectionMeta.rejoinProtection.toVector()
                    }
                } else {
                    if (interactionCache.movementCounter == 0) {
                        interactionCache.movementCounter = 1
                    } else if (interactionCache.movementCounter < 50)
                        interactionCache.movementCounter = interactionCache.movementCounter + 1
                    if (interactionCache.movementCounter > 20) {
                        player.velocity = game.arena.meta.protectionMeta.rejoinProtection.toVector()
                    } else {
                        val knockback =
                            interactionCache.lastPosition!!.toLocation().toVector().subtract(player.location.toVector())
                        player.location.direction = knockback
                        player.velocity = knockback
                        player.allowFlight = true

                        if (!interactionCache.toggled) {
                            chatMessageService.sendChatMessage(player, TextComponent().also {
                                it.components = mutableListOf(
                                    TextComponent().also {
                                        it.text = placeholderService.resolvePlaceHolder(
                                            language.hubGameJoinHeader.text,
                                            player,
                                            mapOf(gameKey to game.arena.name)
                                        ) + "\n"
                                    },
                                    TextComponent().also {
                                        it.text = placeholderService.resolvePlaceHolder(
                                            language.hubGameJoinRed.text,
                                            player,
                                            mapOf(gameKey to game.arena.name)
                                        ) + "\n"
                                        it.clickEvent = ClickEvent(
                                            ClickEventType.RUN_COMMAND,
                                            "/blockball join ${game.arena.name} red"
                                        )
                                    },
                                    TextComponent().also {
                                        it.text = placeholderService.resolvePlaceHolder(
                                            language.hubGameJoinBlue.text,
                                            player,
                                            mapOf(gameKey to game.arena.name)
                                        )
                                        it.clickEvent = ClickEvent(
                                            ClickEventType.RUN_COMMAND,
                                            "/blockball join ${game.arena.name} blue"
                                        )
                                    }
                                )
                            })
                            interactionCache.toggled = true
                        }
                    }
                }
            }
        }

        if (!inArea) {
            if (interactionCache.movementCounter != 0) {
                interactionCache.movementCounter = 0
            }

            if (interactionCache.toggled) {
                if (player.gameMode != GameMode.CREATIVE) {
                    player.allowFlight = false
                    player.isFlying = false
                }

                interactionCache.toggled = false
            }
        }

        interactionCache.lastPosition = player.location.toVector3d()
    }

    private fun getInteractionCache(player: Player): InteractionCache {
        if (!cache.containsKey(player)) {
            cache[player] = InteractionCache().also {
                it.joinTimeStamp = System.currentTimeMillis()
            }
        }

        return cache[player]!!
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    private fun cleanResources(player: Player) {
        if (cache.containsKey(player)) {
            cache.remove(player)
        }
    }
}
