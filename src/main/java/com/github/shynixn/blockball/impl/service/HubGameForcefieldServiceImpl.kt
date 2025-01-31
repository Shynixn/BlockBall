package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallPlugin.Companion.gameKey
import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.HubGameForcefieldService
import com.github.shynixn.blockball.entity.InteractionCache
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.chat.ClickEvent
import com.github.shynixn.mcutils.common.chat.ClickEventType
import com.github.shynixn.mcutils.common.chat.TextComponent
import com.github.shynixn.mcutils.common.language.sendPluginMessage
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player

class HubGameForcefieldServiceImpl(
    private val gameService: GameService,
    private val language: BlockBallLanguage,
    private val chatMessageService: ChatMessageService,
    private val placeholderService: PlaceHolderService
) : HubGameForcefieldService {
    private val cache = HashMap<Player, InteractionCache>()

    /**
     * Checks and executes the forcefield actions if the given [player]
     * is going to the given [location].
     */
    override fun checkForForcefieldInteractions(player: Player, location: Location) {
        val interactionCache = getInteractionCache(player)
        val gameInternal = gameService.getByPlayer(player)

        if (gameInternal != null) {
            if (gameInternal.arena.gameType == GameType.HUBGAME && !gameInternal.arena.isLocationIn2dSelection(
                    location.toVector3d()
                )
            ) {
                gameInternal.leave(player)
                player.sendPluginMessage(language.leftGameMessage)
            }
            return
        }

        var inArea = false

        for (game in gameService.getAll()) {
            if (game.arena.enabled && game.arena.gameType == GameType.HUBGAME && game.arena.isLocationInSelection(
                    location.toVector3d()
                )
            ) {
                inArea = true
                if (game.arena.meta.hubLobbyMeta.instantForcefieldJoin) {
                    game.join(player)
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
                }

                interactionCache.toggled = false
            }
        }

        interactionCache.lastPosition = player.location.toVector3d()
    }

    /**
     * Returns the interaction cache of the given [player].
     */
    override fun getInteractionCache(player: Player): InteractionCache {
        if (!cache.containsKey(player)) {
            cache[player] = InteractionCache()
        }

        return cache[player]!!
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    override fun cleanResources(player: Player) {
        if (cache.containsKey(player)) {
            cache.remove(player)
        }
    }
}
