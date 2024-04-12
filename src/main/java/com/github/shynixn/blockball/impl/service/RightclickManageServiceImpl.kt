package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.RightclickManageService
import com.github.shynixn.blockball.impl.extension.setSignLines
import com.github.shynixn.mcutils.common.ChatColor
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Player

class RightclickManageServiceImpl @Inject constructor() :
    RightclickManageService {
    private val rightClickListener = HashMap<Player, (Location) -> Unit>()

    /**
     * Gets called one time when a location gets rightlicked by [player].
     */
    override fun watchForNextRightClickSign(player: Player, f: (Location) -> Unit) {
        rightClickListener[player] = f
    }

    /**
     * Executes the watcher for the given [player] if he has registered one.
     * Returns if watchers has been executed.
     */
    override fun executeWatchers(player: Player, location: Location): Boolean {
        if (!rightClickListener.containsKey(player)) {
            return false
        }

        if (location.setSignLines(
                listOf(
                    ChatColor.BOLD.toString() + "BlockBall",
                    ChatColor.GREEN.toString() + "Loading..."
                )
            )
        ) {
            rightClickListener[player]!!.invoke(location)
            rightClickListener.remove(player)
        }

        return true
    }

    /**
     * Clears all resources this [player] has allocated from this service.
     */
    override fun cleanResources(player: Player) {
        if (rightClickListener.containsKey(player)) {
            rightClickListener.remove(player)
        }
    }
}
