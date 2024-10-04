package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.MatchTimeCloseType

class MatchTimeMeta {
    /**
     * TimeSpan of the match in seconds.
     */
    var duration: Int = 300

    /**
     * Close time when the match is over.
     */
    var closeType: MatchTimeCloseType = MatchTimeCloseType.TIME_OVER

    /**
     * Should the goals of the teams be switched when this match time gets enabled?
     */
    var isSwitchGoalsEnabled: Boolean = false

    /**
     * Is the ball playable?
     */
    var playAbleBall: Boolean = true

    /**
     * Should the players respawn when this match time starts.
     */
    var respawnEnabled: Boolean = true

    /**
     * Title of the message getting played when this match time starts.
     */
    var startMessageTitle: String = ""

    /**
     * SubTitle of the message getting played when this match time starts.
     */
    var startMessageSubTitle: String = ""

    var startMessageFadeIn: Int = 20

    var startMessageStay: Int = 60

    var startMessageFadeOut: Int = 20
}
