package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.impl.serializer.ItemStackSerializer

class TeamMeta(
    /** DisplayName of the team which gets used in the placeholder <red> or <blue>. */
    @YamlSerialize(orderNumber = 1, value = "displayname")
    var displayName: String = "",
    /** Prefix of the team which gets used in the placeholder <redcolor> or <bluecolor>. */
    @YamlSerialize(orderNumber = 2, value = "prefix")
    var prefix: String = "",
    /** Title of the message getting played when a player scores a goal. */
    @YamlSerialize(orderNumber = 11, value = "score-message-title")
    var scoreMessageTitle: String = "",
    /** Subtitle of the message getting played when a player scores a goal. */
    @YamlSerialize(orderNumber = 12, value = "score-message-subtitle")
    var scoreMessageSubTitle: String = "",
    /** Title of the message getting played when this team wins a match. */
    @YamlSerialize(orderNumber = 13, value = "win-message-title")
    var winMessageTitle: String = "",
    /** Subtitle of the message getting played when this team wins a match. */
    @YamlSerialize(orderNumber = 14, value = "win-message-subtitle")
    var winMessageSubTitle: String = "",
    /** Title of the message getting played when the match ends in a draw.*/
    @YamlSerialize(orderNumber = 15, value = "draw-message-title")
    var drawMessageTitle: String = "",
    /** Subtitle of the message getting played when the match ends in a draw. */
    @YamlSerialize(orderNumber = 16, value = "draw-message-subtitle")
    var drawMessageSubTitle: String = ""
) {
    /** Amount of points this team receives when a goal gets scored. */
    @YamlSerialize(orderNumber = 17, value = "points-per-goal")
    var pointsPerGoal: Int = 1
    /** Amount of points this team receives when a player of the opposite team dies. */
    @YamlSerialize(orderNumber = 18, value = "points-per-opponent-death")
    var pointsPerEnemyDeath: Int = 0
    /** List of signs which can be clicked to join the team.*/
    val signs: MutableList<Position>
        get() = this.internalSigns as MutableList<Position>
    /** Min amount of players in this team to start the match for this team. */
    @YamlSerialize(orderNumber = 3, value = "min-amount")
    var minAmount: Int = 0
    /** Max amount of players in this team to start the match for this team. */
    @YamlSerialize(orderNumber = 4, value = "max-amount")
    var maxAmount: Int = 10
    /** Minimum amount of players in this team to keep the game running. **/
    @YamlSerialize(orderNumber = 3, value = "min-amount-playing")
    var minPlayingPlayers: Int = 0
    /** Goal properties of the team. */
    @YamlSerialize(orderNumber = 7, value = "goal")
    val goal: Selection = Selection()
    /** WalkingSpeed of the players in this team. */
    @YamlSerialize(orderNumber = 5, value = "walking-speed")
    var walkingSpeed: Double = 0.2
    /** Message getting played when a player joins a match.*/
    @YamlSerialize(orderNumber = 10, value = "join-message")
    var joinMessage: String = "%blockball_lang_joinSuccessMessage%"
    /** Message getting played when a player leave a match.*/
    @YamlSerialize(orderNumber = 12, value = "leave-message")
    var leaveMessage: String = "%blockball_lang_leaveMessage%"
    /** Lines displayed on the sign for joining the team. */
    @YamlSerialize(orderNumber = 13, value = "lines")
    var signLines: List<String> = arrayListOf(
        "%blockball_lang_teamSignLine1%",
        "%blockball_lang_teamSignLine2%",
        "%blockball_lang_teamSignLine3%",
        "%blockball_lang_teamSignLine4%",
    )
    /** Armor wearing this team. */
    @YamlSerialize(orderNumber = 8, value = "armor", customserializer = ItemStackSerializer::class)
    var armorContents: Array<Any?> = arrayOfNulls(4)
    /** Inventory this team is getting when playing. */
    @YamlSerialize(orderNumber = 9, value = "inventory", customserializer = ItemStackSerializer::class)
    var inventoryContents: Array<Any?> = arrayOfNulls(36)

    @YamlSerialize(orderNumber = 12, value = "score-message-fadein")
    var scoreMessageFadeIn: Int = 20
    @YamlSerialize(orderNumber = 12, value = "score-message-stay")
    var scoreMessageStay: Int = 60
    @YamlSerialize(orderNumber = 12, value = "score-message-fadeout")
    var scoreMessageFadeOut: Int = 20

    @YamlSerialize(orderNumber = 14, value = "win-message-fadein")
    var winMessageFadeIn: Int = 20
    @YamlSerialize(orderNumber = 14, value = "win-message-stay")
    var winMessageStay: Int = 60
    @YamlSerialize(orderNumber = 14, value = "win-message-fadeout")
    var winMessageFadeOut: Int = 20

    @YamlSerialize(orderNumber = 16, value = "draw-message-fadein")
    var drawMessageFadeIn: Int = 20
    @YamlSerialize(orderNumber = 16, value = "draw-message-stay")
    var drawMessageStay: Int = 60
    @YamlSerialize(orderNumber = 16, value = "draw-message-fadeout")
    var drawMessageFadeOut: Int = 20

    /** Spawnpoint of the team inside of the arena. */
    @YamlSerialize(orderNumber = 6, value = "spawnpoint", implementation = Position::class)
    var spawnpoint: Position? = null

    /** Optional lobby spawnpoint */
    @YamlSerialize(orderNumber = 6, value = "lobby-spawnpoint", implementation = Position::class)
    var lobbySpawnpoint: Position? = null

    /** List of signs for this team */
    @YamlSerialize(orderNumber = 11, value = "signs")
    private var internalSigns: MutableList<Position> = ArrayList()
}
