package com.github.shynixn.blockball.entity

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mcutils.common.repository.Element

@JsonPropertyOrder("name", "displayName", "enabled", "gameType", "corner1", "corner2", "meta")
class Arena : Selection(), Element{
    /** Unique [name] of the arena. */
    override var name: String = ""

    /** [displayName] of the arena on signs or messages. */
    var displayName: String = ""

    /** Is the arena ready to be placed. */
    var enabled: Boolean = true

    /** [gameType] of the arena */
    var gameType: GameType = GameType.HUBGAME

    /** Collection of the arena meta data. */
    val meta: ArenaMeta = ArenaMeta()
}
