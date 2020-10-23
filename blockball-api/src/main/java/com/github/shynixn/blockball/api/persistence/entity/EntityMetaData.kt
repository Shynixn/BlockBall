package com.github.shynixn.blockball.api.persistence.entity

interface EntityMetaData {
    /**
     * Makes the custom name visible or not.
     */
    var customNameVisible: Boolean?

    /**
     * Sets the custom name.
     */
    var customname: String?

    /**
     * Slime size.
     */
    var slimeSize: Int?
}
