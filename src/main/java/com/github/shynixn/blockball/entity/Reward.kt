package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.RewardType


class Reward {
    /** Money which gets added via Vault when a player does a rewarded action. */
    var moneyReward: MutableMap<RewardType, Int> = HashMap()
    /** Commands which get executed when a player does a rewarded action. */
    var commandReward: MutableMap<RewardType, CommandMeta>
        get() = internalCommandReward as MutableMap<RewardType, CommandMeta>
        set(value) {
            internalCommandReward = value as (MutableMap<RewardType, CommandMeta>)
        }

    private var internalCommandReward: MutableMap<RewardType, CommandMeta> = HashMap()
}
