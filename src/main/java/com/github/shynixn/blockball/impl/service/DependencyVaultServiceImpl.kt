package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.DependencyVaultService
import com.google.inject.Inject
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class DependencyVaultServiceImpl @Inject constructor() : DependencyVaultService {
    private var economy: Economy? = null

    /**
     * Adds the given [amount] of money to the [player].
     */
    override fun addMoney(player: Player, amount: Double) {
        if (economy == null) {
            setupEconomy()
        }

        economy!!.depositPlayer(player, amount)
    }

    /**
     * Returns the name of the currency.
     */
    override fun getPluralCurrencyName(): String {
        if (economy == null) {
            setupEconomy()
        }

        return economy!!.currencyNamePlural()
    }

    /**
     * Setups the eoconomy.
     */
    private fun setupEconomy(): Boolean {
        if (economy == null) {
            if (Bukkit.getServer().pluginManager.getPlugin("Vault") == null) {
                return false
            }

            val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java) ?: return false
            economy = rsp.provider

            return economy != null
        }
        return true
    }
}
