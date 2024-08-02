package com.github.shynixn.blockball.entity


class SpectatorMeta  {
    /**
     * Should nearby players inside of the [notificationRadius] be messaged by title messages, scoreboard, holograms and bossbar.
     */
    var notifyNearbyPlayers: Boolean = false
    /**
     * The radius from the center of the soccerArena a player has to be in order to get notified when [notifyNearbyPlayers] is enabled.
     */
    var notificationRadius: Int = 50
}
