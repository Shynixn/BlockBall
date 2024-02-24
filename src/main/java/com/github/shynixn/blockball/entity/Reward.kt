package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.RewardType


class Reward {
    /** Money which gets added via Vault when a player does a rewarded action. */
    @YamlSerialize(orderNumber = 1, value = "money-reward")
    var moneyReward: MutableMap<RewardType, Int> = HashMap()
    /** Commands which get executed when a player does a rewarded action. */
    var commandReward: MutableMap<RewardType, CommandMeta>
        get() = internalCommandReward as MutableMap<RewardType, CommandMeta>
        set(value) {
            internalCommandReward = value as (MutableMap<RewardType, CommandMeta>)
        }

    @YamlSerialize(orderNumber = 2, value = "command-reward")
    private var internalCommandReward: MutableMap<RewardType, CommandMeta> = HashMap()
}
