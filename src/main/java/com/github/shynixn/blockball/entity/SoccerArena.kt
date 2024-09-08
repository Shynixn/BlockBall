package com.github.shynixn.blockball.entity

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mcutils.common.repository.Element

@JsonPropertyOrder("name", "displayName", "enabled", "gameType", "corner1", "corner2", "meta")
class SoccerArena : Selection(), Element{
    /** Unique [name] of the soccerArena. */
    override var name: String = ""

    /** [displayName] of the soccerArena on signs or messages. */
    var displayName: String = ""

    /** Is the soccerArena ready to be placed. */
    var enabled: Boolean = false

    /** [gameType] of the soccerArena */
    var gameType: GameType = GameType.HUBGAME

    /** Collection of the soccerArena metadata. */
    val meta: ArenaMeta = ArenaMeta()
}
