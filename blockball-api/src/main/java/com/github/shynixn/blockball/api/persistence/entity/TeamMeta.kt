package com.github.shynixn.blockball.api.persistence.entity

/**
 * Created by Shynixn 2018.
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
interface TeamMeta {

    /** Goal properties of the team. */
    val goal: Selection

    /** Spawnpoint of the team inside of the arena. */
    var spawnpoint: Position?

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
