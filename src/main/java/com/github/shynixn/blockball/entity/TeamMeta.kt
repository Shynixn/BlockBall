package com.github.shynixn.blockball.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.command.CommandMeta

class TeamMeta(
    /** DisplayName of the team which gets used in the placeholder <red> or <blue>. */
    var displayName: String = "",
    /** Prefix of the team which gets used in the placeholder <redcolor> or <bluecolor>. */
    var prefix: String = "",
    /** Title of the message getting played when a player scores a goal. */
    var scoreMessageTitle: String = "",
    /** Subtitle of the message getting played when a player scores a goal. */
    var scoreMessageSubTitle: String = "",
    /** Title of the message getting played when this team wins a match. */
    var winMessageTitle: String = "",
    /** Subtitle of the message getting played when this team wins a match. */
    var winMessageSubTitle: String = "",
    /** Title of the message getting played when the match ends in a draw.*/
    var drawMessageTitle: String = "",
    /** Subtitle of the message getting played when the match ends in a draw. */
    var drawMessageSubTitle: String = ""
) {
    /** Amount of points this team receives when a goal gets scored. */
    var pointsPerGoal: Int = 1

    /** Amount of points this team receives when a player of the opposite team dies. */
    var pointsPerEnemyDeath: Int = 0

    /** List of signs which can be clicked to join the team.*/
    val signs: MutableList<Vector3d>
        get() = this.internalSigns

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

    /** Message getting played when a player joins a match.*/
    var joinMessage: String = "%blockball_lang_joinSuccessMessage%"

    /** Message getting played when a player leave a match.*/
    var leaveMessage: String = "%blockball_lang_leaveMessage%"

    /** Lines displayed on the sign for joining the team. */
    var signLines: List<String> = arrayListOf(
        "%blockball_lang_teamSignLine1%",
        "%blockball_lang_teamSignLine2%",
        "%blockball_lang_teamSignLine3%",
        "%blockball_lang_teamSignLine4%",
    )

    @JsonProperty("armor")
    var armor: Array<String?> = arrayOfNulls(4)

    @JsonProperty("inventory")
    var inventory: Array<String?> = arrayOfNulls(36)

    var scoreMessageFadeIn: Int = 20
    var scoreMessageStay: Int = 60
    var scoreMessageFadeOut: Int = 20

    var winMessageFadeIn: Int = 20
    var winMessageStay: Int = 60
    var winMessageFadeOut: Int = 20

    var drawMessageFadeIn: Int = 20
    var drawMessageStay: Int = 60
    var drawMessageFadeOut: Int = 20

    /** Spawnpoint of the team inside the arena. */
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

    /** List of signs for this team */
    private var internalSigns: MutableList<Vector3d> = ArrayList()
}
