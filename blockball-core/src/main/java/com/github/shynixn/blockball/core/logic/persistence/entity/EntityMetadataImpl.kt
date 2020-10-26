package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData

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
     * Slime size.
     */
    override var slimeSize: Int? = null

    constructor(f: EntityMetaData.() -> Unit) {
        f.invoke(this)
    }
}
