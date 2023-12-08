package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.MatchTimeCloseType

interface MatchTimeMeta {
    /**
     * Should the goals of the teams be switched when this match time gets enabled?
     */
    var isSwitchGoalsEnabled: Boolean
    /**
     * TimeSpan of the match in seconds.
     */
    var duration: Int
    /**
     * Close time when the match is over.
     */
    var closeType: MatchTimeCloseType

    /**
     * Is the ball playable?
     */
    var playAbleBall: Boolean

    /**
     * Should the players respawn when this match time starts.
     */
    var respawnEnabled: Boolean

    /**
     * Title of the message getting played when this match time starts.
     */
    var startMessageTitle: String

    /**
     * SubTitle of the message getting played when this match time starts.
     */
    var startMessageSubTitle: String

    var startMessageFadeIn : Int

    var startMessageStay : Int

    var startMessageFadeOut : Int
}
