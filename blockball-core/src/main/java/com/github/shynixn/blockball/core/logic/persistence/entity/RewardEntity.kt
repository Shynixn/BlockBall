@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.RewardType
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.RewardMeta

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
class RewardEntity : RewardMeta {
    /** Money which gets added via Vault when a player does a rewarded action. */
    @YamlSerialize(orderNumber = 1, value = "money-reward")
    override var moneyReward: MutableMap<RewardType, Int> = HashMap()
    /** Commands which get executed when a player does a rewarded action. */
    override var commandReward: MutableMap<RewardType, CommandMeta>
        get() = internalCommandReward as MutableMap<RewardType, CommandMeta>
        set(value) {
            internalCommandReward = value as (MutableMap<RewardType, CommandMetaEntity>)
        }

    @YamlSerialize(orderNumber = 2, value = "command-reward")
    private var internalCommandReward: MutableMap<RewardType, CommandMetaEntity> = HashMap()
}