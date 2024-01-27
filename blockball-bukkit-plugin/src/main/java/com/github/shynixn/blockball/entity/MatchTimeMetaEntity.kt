package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.api.persistence.entity.MatchTimeMeta

class MatchTimeMetaEntity : MatchTimeMeta {
    /**
     * TimeSpan of the match in seconds.
     */
    @YamlSerialize(orderNumber = 1, value = "duration")
    override var duration: Int = 300
    /**
     * Close time when the match is over.
     */
    @YamlSerialize(orderNumber = 2, value = "close-type", implementation = MatchTimeCloseType::class)
    override var closeType: MatchTimeCloseType = MatchTimeCloseType.TIME_OVER
    /**
     * Should the goals of the teams be switched when this match time gets enabled?
     */
    @YamlSerialize(orderNumber = 3, value = "switch-goals")
    override var isSwitchGoalsEnabled: Boolean = false
    /**
     * Is the ball playable?
     */
    @YamlSerialize(orderNumber = 4, value = "ball-playable")
    override var playAbleBall: Boolean = true
    /**
     * Should the players respawn when this match time starts.
     */
    @YamlSerialize(orderNumber = 5, value = "respawn")
    override var respawnEnabled: Boolean = true
    /**
     * Title of the message getting played when this match time starts.
     */
    @YamlSerialize(orderNumber = 6, value = "start-message-title")
    override var startMessageTitle: String = ""
    /**
     * SubTitle of the message getting played when this match time starts.
     */
    @YamlSerialize(orderNumber = 7, value = "start-message-subtitle")
    override var startMessageSubTitle: String = ""

    @YamlSerialize(orderNumber = 8, value = "start-message-fadein")
    override var startMessageFadeIn: Int = 20

    @YamlSerialize(orderNumber = 9, value = "start-message-stay")
    override var startMessageStay: Int = 60

    @YamlSerialize(orderNumber = 10, value = "start-message-fadeout")
    override var startMessageFadeOut: Int = 20
}
