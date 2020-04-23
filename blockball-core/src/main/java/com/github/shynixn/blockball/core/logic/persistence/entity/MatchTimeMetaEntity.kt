package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.api.persistence.entity.MatchTimeMeta

/**
 * Created by Shynixn 2020.
 * <p>
 * Version 1.5
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2020 by Shynixn
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
}