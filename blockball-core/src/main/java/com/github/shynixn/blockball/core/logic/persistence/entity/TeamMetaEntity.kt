@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.business.serializer.ItemStackSerializer

/**
 * Entity to hold information about the team.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class TeamMetaEntity(
    /** DisplayName of the team which gets used in the placeholder <red> or <blue>. */
    @YamlSerialize(orderNumber = 1, value = "displayname")
    override var displayName: String = "",
    /** Prefix of the team which gets used in the placeholder <redcolor> or <bluecolor>. */
    @YamlSerialize(orderNumber = 2, value = "prefix")
    override var prefix: String = "",
    /** Title of the message getting played when a player scores a goal. */
    @YamlSerialize(orderNumber = 11, value = "score-message-title")
    override var scoreMessageTitle: String = "",
    /** Subtitle of the message getting played when a player scores a goal. */
    @YamlSerialize(orderNumber = 12, value = "score-message-subtitle")
    override var scoreMessageSubTitle: String = "",
    /** Title of the message getting played when this team wins a match. */
    @YamlSerialize(orderNumber = 13, value = "win-message-title")
    override var winMessageTitle: String = "",
    /** Subtitle of the message getting played when this team wins a match. */
    @YamlSerialize(orderNumber = 14, value = "win-message-subtitle")
    override var winMessageSubTitle: String = "",
    /** Title of the message getting played when the match ends in a draw.*/
    @YamlSerialize(orderNumber = 15, value = "draw-message-title")
    override var drawMessageTitle: String = "",
    /** Subtitle of the message getting played when the match ends in a draw. */
    @YamlSerialize(orderNumber = 16, value = "draw-message-subtitle")
    override var drawMessageSubTitle: String = ""
) : TeamMeta {
    /** Amount of points this team receives when a goal gets scored. */
    @YamlSerialize(orderNumber = 17, value = "points-per-goal")
    override var pointsPerGoal: Int = 1
    /** Amount of points this team receives when a player of the opposite team dies. */
    @YamlSerialize(orderNumber = 18, value = "points-per-opponent-death")
    override var pointsPerEnemyDeath: Int = 0
    /** List of signs which can be clicked to join the team.*/
    override val signs: MutableList<Position>
        get() = this.internalSigns as MutableList<Position>
    /** Min amount of players in this team to start the match for this team. */
    @YamlSerialize(orderNumber = 3, value = "min-amount")
    override var minAmount: Int = 0
    /** Max amount of players in this team to start the match for this team. */
    @YamlSerialize(orderNumber = 4, value = "max-amount")
    override var maxAmount: Int = 10
    /** Minimum amount of players in this team to keep the game running. **/
    @YamlSerialize(orderNumber = 3, value = "min-amount-playing")
    override var minPlayingPlayers: Int = 0
    /** Goal properties of the team. */
    @YamlSerialize(orderNumber = 7, value = "goal")
    override val goal: SelectionEntity = SelectionEntity()
    /** WalkingSpeed of the players in this team. */
    @YamlSerialize(orderNumber = 5, value = "walking-speed")
    override var walkingSpeed: Double = 0.2
    /** Message getting played when a player joins a match.*/
    @YamlSerialize(orderNumber = 10, value = "join-message")
    override var joinMessage: String =
        "You have joined the game for " + PlaceHolder.ARENA_TEAMCOLOR.placeHolder + PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder + "."
    /** Message getting played when a player leave a match.*/
    @YamlSerialize(orderNumber = 12, value = "leave-message")
    override var leaveMessage: String = "You have left the game."
    /** Lines displayed on the sign for joining the team. */
    @YamlSerialize(orderNumber = 13, value = "lines")
    override var signLines: List<String> = arrayListOf(
        "&lBlockBall",
        PlaceHolder.ARENA_DISPLAYNAME.placeHolder,
        PlaceHolder.ARENA_TEAMCOLOR.placeHolder + PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder,
        PlaceHolder.ARENA_PLAYERS_ON_TEAM.placeHolder + '/' + PlaceHolder.ARENA_MAX_PLAYERS_ON_TEAM.placeHolder
    )
    /** Armor wearing this team. */
    @YamlSerialize(orderNumber = 8, value = "armor", customserializer = ItemStackSerializer::class)
    override var armorContents: Array<Any?> = arrayOfNulls(4)
    /** Inventory this team is getting when playing. */
    @YamlSerialize(orderNumber = 9, value = "inventory", customserializer = ItemStackSerializer::class)
    override var inventoryContents: Array<Any?> = arrayOfNulls(36)
    /** Spawnpoint of the team inside of the arena. */
    @YamlSerialize(orderNumber = 6, value = "spawnpoint", implementation = PositionEntity::class)
    override var spawnpoint: Position? = null
    /** List of signs for this team */
    @YamlSerialize(orderNumber = 11, value = "signs")
    private var internalSigns: MutableList<PositionEntity> = ArrayList()
}
