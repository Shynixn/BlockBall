package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.entity.InteractionCache
import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.blockball.impl.extension.toLocation
import com.github.shynixn.blockball.impl.extension.toPosition
import com.github.shynixn.blockball.impl.extension.toVector
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player

class HubGameForcefieldServiceImpl @Inject constructor(
    private val gameService: GameService,
    private val gameActionService: GameActionService,
    private val placeholderService: PlaceHolderService,
    private val configurationService: ConfigurationService,
    private val chatMessageService: ChatMessageService
) : HubGameForcefieldService {
    private val cache = HashMap<Player, InteractionCache>()
    /**
     * Checks and executes the forcefield actions if the given [player]
     * is going to the given [location].
     */
    override fun checkForForcefieldInteractions(player: Player, location: Location) {
        val interactionCache = getInteractionCache(player)
        val gameInternal = gameService.getGameFromPlayer(player)

        if (gameInternal.isPresent) {
            if (gameInternal.get().arena.gameType == GameType.HUBGAME && !gameInternal.get().arena.isLocationInSelection(
                    location.toPosition()
                )
            ) {
                gameActionService.leaveGame(gameInternal.get(), player)
            }
            return
        }

        var inArea = false

        gameService.getAllGames().forEach { game ->
            if (game.arena.enabled && game.arena.gameType == GameType.HUBGAME && game.arena.isLocationInSelection(
                    location.toPosition()
                )
            ) {
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
                        val knockback =
                            interactionCache.lastPosition!!.toLocation().toVector().subtract(player.location.toVector())
                        player.location.direction = knockback
                        player.velocity = knockback
                        player.allowFlight = true

                        if (!interactionCache.toggled) {
                            val joinCommand = configurationService.findValue<String>("global-join.command")

                            val b =
                                ChatBuilder().text(placeholderService.replacePlaceHolders(game.arena.meta.hubLobbyMeta.joinMessage[0], player, game, null, null))
                                    .nextLine()
                                    .component(
                                        placeholderService.replacePlaceHolders(
                                            game.arena.meta.hubLobbyMeta.joinMessage[1],
                                            player,
                                            game,
                                            game.arena.meta.redTeamMeta
                                        )
                                    )
                                    .setClickAction(
                                        ChatClickAction.RUN_COMMAND
                                        ,
                                        "/" + joinCommand + " " + game.arena.name + "|" + game.arena.meta.redTeamMeta.displayName.stripChatColors()
                                    )
                                    .setHoverText(" ")
                                    .builder().text(" ")
                                    .component(
                                        placeholderService.replacePlaceHolders(
                                            game.arena.meta.hubLobbyMeta.joinMessage[2],
                                            player,
                                            game,
                                            game.arena.meta.blueTeamMeta
                                        )
                                    )
                                    .setClickAction(
                                        ChatClickAction.RUN_COMMAND
                                        ,
                                        "/" + joinCommand + " " + game.arena.name + "|" + game.arena.meta.blueTeamMeta.displayName.stripChatColors()
                                    )
                                    .setHoverText(" ")
                                    .builder()
                            chatMessageService.sendChatMessage(player, b.convertToTextComponent())
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
