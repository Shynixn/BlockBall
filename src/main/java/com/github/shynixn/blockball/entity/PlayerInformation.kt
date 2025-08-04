package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.database.api.PlayerData

class PlayerInformation : PlayerData {
    /**
     *  Marker if this player data has been stored before.
     */
    override var isPersisted: Boolean = false

    /**
     * Name of the player.
     */
    override var playerName: String = ""

    /**
     * UUID of the player.
     */
    override var playerUUID: String = ""

    /**
     * Collected stats meta.
     */
    var statsMeta: StatsMeta = StatsMeta()

    /**
     * Cached game storage for crashes.
     */
    var cachedStorage: GameStorage? = null
}
