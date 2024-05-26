package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d

class SpectatorMeta  {
    /**
     * Should nearby players inside of the [notificationRadius] be messaged by title messages, scoreboard, holograms and bossbar.
     */
    var notifyNearbyPlayers: Boolean = false
    /**
     * The radius from the center of the arena a player has to be in order to get notified when [notifyNearbyPlayers] is enabled.
     */
    var notificationRadius: Int = 50

    /**
     * Should the spectator mode be enabled for this arena?
     */
    var spectatorModeEnabled: Boolean = true

    /**
     * Spectate asking message.
     */
    var spectateStartMessage: MutableList<String> = arrayListOf("%blockball_lang_spectateJoinHeader%", "%blockball_lang_spectateJoinClick%")

    /**
     *  Spawnpoint of the spectatorPlayers.
     */
    var spectateSpawnpoint: Vector3d? = null
}
