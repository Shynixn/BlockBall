package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.MatchTimeCloseType

class MatchTimeMeta{
    /**
     * TimeSpan of the match in seconds.
     */
    @YamlSerialize(orderNumber = 1, value = "duration")
    var duration: Int = 300
    /**
     * Close time when the match is over.
     */
    @YamlSerialize(orderNumber = 2, value = "close-type", implementation = MatchTimeCloseType::class)
    var closeType: MatchTimeCloseType = MatchTimeCloseType.TIME_OVER
    /**
     * Should the goals of the teams be switched when this match time gets enabled?
     */
    @YamlSerialize(orderNumber = 3, value = "switch-goals")
    var isSwitchGoalsEnabled: Boolean = false
    /**
     * Is the ball playable?
     */
    @YamlSerialize(orderNumber = 4, value = "ball-playable")
    var playAbleBall: Boolean = true
    /**
     * Should the players respawn when this match time starts.
     */
    @YamlSerialize(orderNumber = 5, value = "respawn")
    var respawnEnabled: Boolean = true
    /**
     * Title of the message getting played when this match time starts.
     */
    @YamlSerialize(orderNumber = 6, value = "start-message-title")
    var startMessageTitle: String = ""
    /**
     * SubTitle of the message getting played when this match time starts.
     */
    @YamlSerialize(orderNumber = 7, value = "start-message-subtitle")
    var startMessageSubTitle: String = ""

    @YamlSerialize(orderNumber = 8, value = "start-message-fadein")
    var startMessageFadeIn: Int = 20

    @YamlSerialize(orderNumber = 9, value = "start-message-stay")
    var startMessageStay: Int = 60

    @YamlSerialize(orderNumber = 10, value = "start-message-fadeout")
    var startMessageFadeOut: Int = 20
}
