package com.github.shynixn.blockball.entity


class CustomizationMeta{
    /** Can players damage other players during a game?*/
    var damageEnabled: Boolean = false
    /** Should players be teleported back to their spawnpoint if someone scores?*/
    var backTeleport: Boolean = false
    /** After how many seconds should players be teleported back to their spawnpoint if [backTeleport] is enabled?*/
    var backTeleportDelay: Int = 2
    /**
     * Should the players keep their inventory when they join a game?
     */
    var keepInventoryEnabled: Boolean = false
    /**
     * Should the players keep their health when they join a game?
     */
    var keepHealthEnabled: Boolean = false
}
