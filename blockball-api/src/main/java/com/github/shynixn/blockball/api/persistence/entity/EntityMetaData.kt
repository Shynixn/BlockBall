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
     * Sets the visibility flag
     */
    var isInvisible: Boolean?

    /**
     * Slime size.
     */
    var slimeSize: Int?

    /**
     * Armorstand head rotation.
     */
    var armorstandHeadRotation: Position?

    /**
     * Is the armorstand small?
     */
    var isSmall: Boolean?
}
