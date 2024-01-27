package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.SpectatorMeta

class SpectatorMetaEntity : SpectatorMeta {
    /**
     * Should nearby players inside of the [notificationRadius] be messaged by title messages, scoreboard, holograms and bossbar.
     */
    @YamlSerialize(orderNumber = 1, value = "notify-nearby-players-enabled")
    override var notifyNearbyPlayers: Boolean = false
    /**
     * The radius from the center of the arena a player has to be in order to get notified when [notifyNearbyPlayers] is enabled.
     */
    @YamlSerialize(orderNumber = 2, value = "notify-nearby-players-radius")
    override var notificationRadius: Int = 50

    /**
     * Should the spectator mode be enabled for this arena?
     */
    @YamlSerialize(orderNumber = 3, value = "spectatormode-enabled")
    override var spectatorModeEnabled: Boolean = true

    /**
     * Spectate asking message.
     */
    @YamlSerialize(orderNumber = 4, value = "spectatormode-start-message")
    override var spectateStartMessage: MutableList<String> = arrayListOf("%blockball_lang_spectateJoinHeader%", "%blockball_lang_spectateJoinClick%")

    /**
     *  Spawnpoint of the spectatorPlayers.
     */
    @YamlSerialize(orderNumber = 5, value = "spectatormode-spawnpoint", implementation = PositionEntity::class)
    override var spectateSpawnpoint: Position? = null
}
