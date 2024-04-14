package com.github.shynixn.blockball.contract

import org.bukkit.Location
import org.bukkit.entity.Player

interface RightclickManageService {
    /**
     * Gets called one time when a location gets rightlicked by [player].
     */
    fun watchForNextRightClickSign(player: Player, f: (Location) -> Unit)

    /**
     * Executes the watcher for the given [player] if he has registered one.
     * Returns if watchers has been executed.
     */
    fun executeWatchers(player: Player, location: Location): Boolean

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    fun cleanResources(player: Player)
}
