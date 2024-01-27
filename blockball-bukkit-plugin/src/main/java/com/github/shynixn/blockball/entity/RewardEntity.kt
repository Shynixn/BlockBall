package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.RewardType
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.RewardMeta

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
