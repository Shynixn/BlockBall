package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.BlockDirection

interface RaytraceResult {
    /**
     * Gets if the raytrace has ended in a block hit.
     */
    val hitBlock: Boolean

    /**
     * Gets the resulting position if the object actually
     * performs the given velocity.
     */
    val targetPosition: Position

    /**
     * Hit direction.
     */
    var blockdirection: BlockDirection
}
