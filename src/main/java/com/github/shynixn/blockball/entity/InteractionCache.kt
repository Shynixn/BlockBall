package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d

class InteractionCache {
    /**
     * Player in toggled flight mode for anticheat plugins to handle correctly.
     */
   var toggled: Boolean = false

    /**
     * Last Position of the player.
     */
    var lastPosition: Vector3d? = null

    /**
     * Internal movement counter for interactions.
     */
    var movementCounter: Int = 0

    var joinTimeStamp = 0L
}
