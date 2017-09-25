package com.github.shynixn.blockball.bukkit.dependencies.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;


/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public final class VaultConnection {
    private static Economy economy;

    /**
     * Initialize
     */
    private VaultConnection() {
        super();
    }

    /**
     * Initializes the economy connection
     *
     * @return success
     */
    public static boolean setupEconomy() {
        if (economy == null) {
            if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
                return false;
            }
            final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
            return economy != null;
        }
        return true;
    }

    /**
     * Removes the amount of money from the given players account
     *
     * @param amount  amount
     * @param players players
     */
    public static void remove(double amount, Player... players) {
        if (economy == null)
            throw new IllegalArgumentException("Economy is not initialized.");
        for (final Player player : players) {
            economy.withdrawPlayer(player, amount);
        }
    }

    /**
     * Adds the amount of money to the given player accounts
     *
     * @param amount  amount
     * @param players players
     */
    public static void add(double amount, Player... players) {
        if (economy == null)
            throw new IllegalArgumentException("Economy is not initialized.");
        for (final Player player : players) {
            economy.depositPlayer(player, amount);
        }
    }

    /**
     * Returns the name of the currency
     *
     * @return name
     */
    public static String getCurrencyName() {
        if (economy == null)
            throw new IllegalArgumentException("Economy is not initialized.");
        return economy.currencyNamePlural();
    }
}
