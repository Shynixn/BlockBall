package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.InteractionCache
import org.bukkit.Location
import org.bukkit.entity.Player

interface HubGameForcefieldService {
    /**
     * Checks and executes the forcefield actions if the given [player]
     * is going to the given [location].
     */
    fun checkForForcefieldInteractions(player: Player, location: Location)

    /**
     * Returns the interaction cache of the given [player].
     */
    fun getInteractionCache(player: Player): InteractionCache

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    fun cleanResources(player: Player)
}
