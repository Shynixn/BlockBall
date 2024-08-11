package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.sign.SignMeta

class TeamMeta {
    /** Amount of points this team receives when a goal gets scored. */
    var pointsPerGoal: Int = 1

    /** Amount of points this team receives when a player of the opposite team dies. */
    var pointsPerEnemyDeath: Int = 0

    /** List of signs which can be clicked to join the team.*/
    val teamSigns = ArrayList<SignMeta>()

    /** Min amount of players in this team to start the match for this team. */
    var minAmount: Int = 0

    /** Max amount of players in this team to start the match for this team. */
    var maxAmount: Int = 10

    /** Minimum amount of players in this team to keep the game running. **/
    var minPlayingPlayers: Int = 0

    /** Goal properties of the team. */
    val goal: Selection = Selection()

    /** WalkingSpeed of the players in this team. */
    var walkingSpeed: Double = 0.2

    var armor: Array<String?> = arrayOfNulls(4)

    var inventory: Array<String?> = arrayOfNulls(36)

    /** Spawnpoint of the team inside the soccerArena. */
    var spawnpoint: Vector3d? = null

    /** Optional lobby spawnpoint */
    var lobbySpawnpoint: Vector3d? = null

    /**
     * Commands executed on player win.
     */
    var winCommands: List<CommandMeta> = ArrayList()

    /**
     * Commands executed on player loose.
     */
    var looseCommands: List<CommandMeta> = ArrayList()

    /**
     * Commands executed on player draw.
     */
    var drawCommands: List<CommandMeta> = ArrayList()

    /**
     * Commands executed on player goal.
     */
    var goalCommands: List<CommandMeta> = ArrayList()

    /**
     * Commands executed on player join.
     */
    var joinCommands: List<CommandMeta> = ArrayList()

    /**
     * Commands executed on player leave.
     */
    var leaveCommands: List<CommandMeta> = ArrayList()
}
