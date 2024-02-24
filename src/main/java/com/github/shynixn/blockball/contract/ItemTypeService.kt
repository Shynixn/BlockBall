package com.github.shynixn.blockball.contract

interface ItemTypeService {
    /**
     * Tries to find a matching itemType matching the given hint.
     */
    fun <I> findItemType(sourceHint: Any): I

    /**
     * Tries to find the data value of the given hint.
     */
    fun findItemDataValue(sourceHint: Any): Int
}
