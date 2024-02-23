package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.contract.RightclickManageService
import com.github.shynixn.mcutils.common.ChatColor
import com.google.inject.Inject

class RightclickManageServiceImpl @Inject constructor(private val proxyService: ProxyService) :
    RightclickManageService {
    private val rightClickListener = HashMap<Any, (Any) -> Unit>()

    /**
     * Gets called one time when a location gets rightlicked by [player].
     */
    override fun <P, L> watchForNextRightClickSign(player: P, f: (L) -> Unit) {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val func = f as (Any) -> Unit
        rightClickListener[player] = func
    }

    /**
     * Executes the watcher for the given [player] if he has registered one.
     */
    override fun <P, L> executeWatchers(player: P, location: L): Boolean {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (!rightClickListener.containsKey(player)) {
            return false
        }

        if (proxyService.setSignLines(location, listOf(ChatColor.BOLD.toString() + "BlockBall", ChatColor.GREEN.toString() + "Loading..."))) {
            rightClickListener[player]!!.invoke(location as Any)
            rightClickListener.remove(player)
        }

        return true
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    override fun <P> cleanResources(player: P) {
        require (player is Any) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (rightClickListener.containsKey(player)) {
            rightClickListener.remove(player)
        }
    }
}
