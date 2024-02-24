package com.github.shynixn.blockball.entity

class InteractionCache {
    /**
     * Player in toggled flight mode for anticheat plugins to handle correctly.
     */
   var toggled: Boolean = false

    /**
     * Last Position of the player.
     */
    var lastPosition: Position? = null

    /**
     * Internal movement counter for interactions.
     */
    var movementCounter: Int = 0
}
