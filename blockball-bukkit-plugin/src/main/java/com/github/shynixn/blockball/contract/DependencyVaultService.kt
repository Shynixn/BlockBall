package com.github.shynixn.blockball.contract

import org.bukkit.entity.Player

interface DependencyVaultService {
    /**
     * Adds the given [amount] of money to the [player].
     */
    fun addMoney(player: Player, amount: Double)

    /**
     * Returns the name of the currency.
     */
    fun getPluralCurrencyName(): String
}
