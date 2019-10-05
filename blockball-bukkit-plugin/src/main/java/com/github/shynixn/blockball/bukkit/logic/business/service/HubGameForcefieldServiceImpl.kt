package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ChatClickAction
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.InteractionCache
import com.github.shynixn.blockball.bukkit.logic.business.extension.*
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.InteractionCacheEntity
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player

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
class HubGameForcefieldServiceImpl @Inject constructor(
    private val gameService: GameService,
    private val configurationService: ConfigurationService,
    private val gameActionService: GameActionService,
    private val proxyService: ProxyService
) : HubGameForcefieldService {
    private val cache = HashMap<Player, InteractionCache>()

    /**
     * Checks and executes the forcefield actions if the given [player]
     * is going to the given [location].
     */
    override fun <P, L> checkForForcefieldInteractions(player: P, location: L) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        val prefix = configurationService.findValue<String>("messages.prefix")
        val interactionCache = getInteractionCache(player)
        val gameInternal = gameService.getGameFromPlayer(player)

        if (gameInternal.isPresent) {
            if (gameInternal.get().arena.gameType == GameType.HUBGAME && !gameInternal.get().arena.isLocationInSelection(player.location)) {
                gameActionService.leaveGame(gameInternal.get(), player)
            }
            return
        }

        var inArea = false

        gameService.getAllGames().forEach { game ->
            if (game.arena.enabled && game.arena.gameType == GameType.HUBGAME && game.arena.isLocationInSelection(location)) {
                inArea = true
                if (game.arena.meta.hubLobbyMeta.instantForcefieldJoin) {
                    gameActionService.joinGame(game, player)
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
                        val knockback = interactionCache.lastPosition!!.toLocation().toVector().subtract(player.location.toVector())
                        player.location.direction = knockback
                        player.velocity = knockback
                        player.allowFlight = true

                        if (!interactionCache.toggled) {
                            val joinCommand = configurationService.findValue<String>("global-join.command")

                            val b = ChatBuilderEntity().text(prefix + game.arena.meta.hubLobbyMeta.joinMessage[0].translateChatColors())
                                .nextLine()
                                .component(game.arena.meta.hubLobbyMeta.joinMessage[1].replaceGamePlaceholder(game, game.arena.meta.redTeamMeta))
                                .setClickAction(
                                    ChatClickAction.RUN_COMMAND
                                    , "/" + joinCommand + " " + game.arena.name + "|" + game.arena.meta.redTeamMeta.displayName.stripChatColors()
                                )
                                .setHoverText(" ")
                                .builder().text(" ")
                                .component(game.arena.meta.hubLobbyMeta.joinMessage[2].replaceGamePlaceholder(game, game.arena.meta.blueTeamMeta))
                                .setClickAction(
                                    ChatClickAction.RUN_COMMAND
                                    , "/" + joinCommand + " " + game.arena.name + "|" + game.arena.meta.blueTeamMeta.displayName.stripChatColors()
                                )
                                .setHoverText(" ")
                                .builder()

                            proxyService.sendMessage(player, b)

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

        interactionCache.lastPosition = player.location.toPosition()
    }

    /**
     * Returns the interaction cache of the given [player].
     */
    override fun <P> getInteractionCache(player: P): InteractionCache {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!cache.containsKey(player)) {
            cache[player] = InteractionCacheEntity()
        }

        return cache[player]!!
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    override fun <P> cleanResources(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (cache.containsKey(player)) {
            cache.remove(player)
        }
    }
}