package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.sign.SignMeta
import org.bukkit.GameMode

class LobbyMeta {
    @Comment("The max amount of goals a team can score before the match ends and the game is reset.")
    var maxScore: Int = 10

    @Comment("If set to true, the team choice when joining may get ignored if the other team has not got enough players.")
    var onlyAllowEventTeams: Boolean = false

    @Comment("The list of join signs for this game. Use the /blockball sign command to create them in-ame instead of manually here.")
    val joinSigns = ArrayList<SignMeta>()

    @Comment("The list of leave signs for this game. Use the /blockball sign command to create them in-ame instead of manually here.")
    val leaveSigns = ArrayList<SignMeta>()

    @Comment("If gameType=HubGame, this is the spawnpoint when they leave the game.")
    var leaveSpawnpoint: Vector3d? = null

    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    @Comment("Sets players to this gamemode, when they join a game. ")
    var gamemode: GameMode = GameMode.ADVENTURE
}
