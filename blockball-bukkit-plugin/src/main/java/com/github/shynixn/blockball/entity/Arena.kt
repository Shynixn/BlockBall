package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.GameType

class Arena : Selection(){
    /** Unique [name] of the arena. */
    @YamlSerialize(orderNumber = 1, value = "name")
    var name: String = ""

    /** [displayName] of the arena on signs or messages. */
    @YamlSerialize(orderNumber = 2, value = "displayname")
    var displayName: String = ""

    /** Is the arena ready to be placed. */
    @YamlSerialize(orderNumber = 3, value = "enabled")
    var enabled: Boolean = true

    /** [gameType] of the arena */
    @YamlSerialize(orderNumber = 4, value = "gamemode")
    var gameType: GameType = GameType.HUBGAME

    /** Collection of the arena meta data. */
    @YamlSerialize(orderNumber = 7, value = "meta")
    val meta: ArenaMeta = ArenaMeta()
}
