package com.github.shynixn.blockball.contract

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface BlockSelectionService {
    /**
     * Selects the left location internally.
     */
    fun  selectLeftLocation(player: Player, location: Location) : Boolean

    /**
     * Selects the right location internally.
     */
    fun selectRightLocation(player: Player, location: Location) : Boolean

    /**
     * Returns the leftclick internal or worledit selection of the given [player].
     */
    fun getLeftClickLocation(player: Player): Optional<Location>

    /**
     * Returns the rightclick internal or worledit selection of the given [player].
     */
    fun getRightClickLocation(player: Player): Optional<Location>

    /**
     * Gives the given [player] the selection tool if he does not
     * already have it.
     */
    fun setSelectionToolForPlayer(player : Player)

    /**
     * Cleans open resources.
     */
    fun cleanResources(player: Player)
}
