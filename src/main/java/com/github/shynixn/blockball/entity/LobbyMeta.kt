package com.github.shynixn.blockball.entity

import com.github.shynixn.fasterxml.jackson.annotation.JsonProperty
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.sign.SignMeta
import org.bukkit.GameMode

class LobbyMeta {
    /** Max score of a team until the match ends and the soccerArena gets reset. */
    var maxScore: Int = 10

    /** Should players automatically join the other team to even out them?*/
    var onlyAllowEventTeams: Boolean = false

    /** List of signs which can be clicked to join the game. */
    @JsonProperty("compatibilityJoinSigns")
    val joinSigns = ArrayList<SignMeta>()

    /** List of signs which can be clicked to leave the game. */
    @JsonProperty("compatibilityLeaveSigns")
    val leaveSigns = ArrayList<SignMeta>()

    /** Spawnpoint when someone leaves the hub game. */
    var leaveSpawnpoint: Vector3d? = null

    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    var gamemode: GameMode = GameMode.ADVENTURE
}
