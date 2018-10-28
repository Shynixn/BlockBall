package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.BossBarFlag
import com.github.shynixn.blockball.api.business.enumeration.BossbarColor
import com.github.shynixn.blockball.api.business.enumeration.BossbarStyle
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.BossBarMeta

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
class BossBarMetaEntity : BossBarMeta {
    /** Color of the bossbar. */
    @YamlSerialize("color", orderNumber = 4)
    override var color: BossbarColor = BossbarColor.WHITE
    /** Is bossbar visible. */
    @YamlSerialize("enabled", orderNumber = 1)
    override var enabled: Boolean = false
    /** Displaying message. */
    @YamlSerialize("text", orderNumber = 2)
    override var message: String = PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.TEAM_RED.placeHolder + ' ' + PlaceHolder.RED_GOALS.placeHolder + " : " + PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.BLUE_GOALS.placeHolder + ' ' + PlaceHolder.TEAM_BLUE.placeHolder
    /** Percentage filled in the bossbar. */
    @YamlSerialize("percentage", orderNumber = 3)
    override var percentage: Double = 100.0
    /** Style of the bossbar. */
    @YamlSerialize("style", orderNumber = 5)
    override var style: BossbarStyle = BossbarStyle.SOLID
    /** Flags of the bossbar. */
    @YamlSerialize("flags", orderNumber = 6)
    override val flags: MutableList<BossBarFlag> = ArrayList()
}