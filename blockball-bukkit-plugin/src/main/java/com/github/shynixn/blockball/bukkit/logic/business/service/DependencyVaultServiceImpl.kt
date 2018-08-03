package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.DependencyVaultService
import com.google.inject.Inject
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
class DependencyVaultServiceImpl @Inject constructor() : DependencyVaultService {
    private var economy: Economy? = null

    /**
     * Adds the given [amount] of money to the [player].
     */
    override fun <P> addMoney(player: P, amount: Double) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

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