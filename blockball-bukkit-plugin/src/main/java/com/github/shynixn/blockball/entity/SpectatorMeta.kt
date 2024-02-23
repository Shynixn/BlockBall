package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class SpectatorMeta  {
    /**
     * Should nearby players inside of the [notificationRadius] be messaged by title messages, scoreboard, holograms and bossbar.
     */
    @YamlSerialize(orderNumber = 1, value = "notify-nearby-players-enabled")
    var notifyNearbyPlayers: Boolean = false
    /**
     * The radius from the center of the arena a player has to be in order to get notified when [notifyNearbyPlayers] is enabled.
     */
    @YamlSerialize(orderNumber = 2, value = "notify-nearby-players-radius")
    var notificationRadius: Int = 50

    /**
     * Should the spectator mode be enabled for this arena?
     */
    @YamlSerialize(orderNumber = 3, value = "spectatormode-enabled")
    var spectatorModeEnabled: Boolean = true

    /**
     * Spectate asking message.
     */
    @YamlSerialize(orderNumber = 4, value = "spectatormode-start-message")
    var spectateStartMessage: MutableList<String> = arrayListOf("%blockball_lang_spectateJoinHeader%", "%blockball_lang_spectateJoinClick%")

    /**
     *  Spawnpoint of the spectatorPlayers.
     */
    @YamlSerialize(orderNumber = 5, value = "spectatormode-spawnpoint", implementation = Position::class)
    var spectateSpawnpoint: Position? = null
}
