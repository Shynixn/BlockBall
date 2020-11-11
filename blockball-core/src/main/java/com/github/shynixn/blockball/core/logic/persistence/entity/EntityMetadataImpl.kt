package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position

class EntityMetadataImpl : EntityMetaData {
    /**
     * Makes the custom name visible or not.
     */
    override var customNameVisible: Boolean? = null

    /**
     * Sets the custom name.
     */
    override var customname: String? = null

    /**
     * Sets the visibility flag
     */
    override var isInvisible: Boolean? = null

    /**
     * Slime size.
     */
    override var slimeSize: Int? = null

    /**
     * Armorstand head rotation.
     */
    override var armorstandHeadRotation: Position? = null

    constructor(f: EntityMetaData.() -> Unit) {
        f.invoke(this)
    }
}
