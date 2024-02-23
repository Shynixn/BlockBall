package com.github.shynixn.blockball.contract

interface RightclickManageService {
    /**
     * Gets called one time when a location gets rightlicked by [player].
     */
    fun <P, L> watchForNextRightClickSign(player: P, f: (L) -> Unit)

    /**
     * Executes the watcher for the given [player] if he has registered one.
     * Returns if watchers has been executed.
     */
    fun <P, L> executeWatchers(player: P, location: L): Boolean

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    fun <P> cleanResources(player: P)
}
