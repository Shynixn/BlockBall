package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class CustomizationMeta{
    /** Can players damage other players during a game?*/
    @YamlSerialize(value = "damage-enabled", orderNumber = 1)
    var damageEnabled: Boolean = false
    /** Should players be teleported back to their spawnpoint if someone scores?*/
    @YamlSerialize(value = "score-back-teleport", orderNumber = 2)
    var backTeleport: Boolean = false
    /** After how many seconds should players be teleported back to their spawnpoint if [backTeleport] is enabled?*/
    @YamlSerialize(value = "score-back-teleport-delay", orderNumber = 3)
    var backTeleportDelay: Int = 2
    /**
     * Should the ball be restricted by a forcefield to stay in the arena?
     */
    @YamlSerialize(value = "ball-forcefield", orderNumber = 4)
    var ballForceField: Boolean = true

    /**
     * Should the players keep their inventory when they join a game?
     */
    @YamlSerialize(value = "keep-inventory", orderNumber = 4)
    var keepInventoryEnabled: Boolean = false
    /**
     * Should the players keep their health when they join a game?
     */
    @YamlSerialize(value = "keep-health", orderNumber = 5)
    var keepHealthEnabled: Boolean = false
}
