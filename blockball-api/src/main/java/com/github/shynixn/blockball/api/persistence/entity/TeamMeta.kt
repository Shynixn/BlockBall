package com.github.shynixn.blockball.api.persistence.entity

interface TeamMeta {

    /** Goal properties of the team. */
    val goal: Selection

    /** Spawnpoint of the team inside of the arena. */
    var spawnpoint: Position?

    /** Optional lobby spawnpoint */
    var lobbySpawnpoint: Position?

    /** DisplayName of the team which gets used in the placeholder <red> or <blue>. */
    var displayName: String

    /** Prefix of the team which gets used in the placeholder <redcolor> or <bluecolor>. */
    var prefix: String

    /** Min amount of players in this team to start the match for this team. */
    var minAmount: Int

    /** Max amount of players in this team to start the match for this team. */
    var maxAmount: Int
    /** Minimum amount of players in this team to keep the game running. **/
    var minPlayingPlayers : Int

    /** Amount of points this team receives when a goal gets scored. */
    var pointsPerGoal: Int

    /** Amount of points this team receives when a player of the opposite team dies. */
    var pointsPerEnemyDeath: Int

    /** WalkingSpeed of the players in this team. */
    var walkingSpeed: Double

    /** Armor wearing this team. */
    var armorContents: Array<Any?>

    /** Inventory this team is getting when playing. */
    var inventoryContents: Array<Any?>

    /** Title of the message getting played when a player scores a goal. */
    var scoreMessageTitle: String

    /** Subtitle of the message getting played when a player scores a goal. */
    var scoreMessageSubTitle: String

    /** Title of the message getting played when this team wins a match. */
    var winMessageTitle: String

    /** Subtitle of the message getting played when this team wins a match. */
    var winMessageSubTitle: String

    /** Title of the message getting played when the match ends in a draw.*/
    var drawMessageTitle: String

    /** Subtitle of the message getting played when the match ends in a draw. */
    var drawMessageSubTitle: String

    /** Message getting played when a player joins a match.*/
    var joinMessage: String

    /** Message getting played when a player leave a match.*/
    var leaveMessage: String

    /** Lines displayed on the sign for joining the team. */
    var signLines: List<String>

    /** List of signs which can be clicked to join the red team.*/
    val signs: MutableList<Position>
}
