package com.github.shynixn.blockball.business.bukkit.dependencies.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created by Shynixn
 */
public class VaultConnection {
    private static Economy economy;

    public static void remove(double amount, Player... players) {
        for (final Player player : players) {
            economy.withdrawPlayer(player, amount);
        }
    }

    public static void add(double amount, Player... players) {
        for (final Player player : players) {
            economy.depositPlayer(player, amount);
        }
    }

    public static String getCurrencyName() {
        return economy.currencyNamePlural();
    }

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
}
