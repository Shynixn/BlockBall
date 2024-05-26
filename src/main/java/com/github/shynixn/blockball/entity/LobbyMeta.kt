package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import org.bukkit.GameMode

class LobbyMeta {
    /** Max score of a team until the match ends and the arena gets reset. */
    var maxScore: Int = 10

    /** Should players automatically join the other team to even out them?*/
    var onlyAllowEventTeams: Boolean = false

    /** List of signs which can be clicked to join the game. */
    val joinSigns: MutableList<Vector3d>
        get() = sign.joinSigns

    /** Lines displayed on the sign for leaving the match. */
    var joinSignLines: List<String> = arrayListOf(
        "%blockball_lang_joinSignLine1%",
        "%blockball_lang_joinSignLine2%",
        "%blockball_lang_joinSignLine3%",
        "%blockball_lang_joinSignLine4%"
    )

    var leaveSignLines: List<String> = arrayListOf(
        "%blockball_lang_leaveSignLine1%",
        "%blockball_lang_leaveSignLine2%",
        "%blockball_lang_leaveSignLine3%",
        "%blockball_lang_leaveSignLine4%"
    )

    /** List of signs which can be clicked to leave the game. */
    val leaveSigns: MutableList<Vector3d>
        get() = sign.leaveSigns

    /** Spawnpoint when someone leaves the hub game. */
    var leaveSpawnpoint: Vector3d? = null

    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    var gamemode: GameMode = GameMode.ADVENTURE

    private val sign: SignCollection = SignCollection()
}
