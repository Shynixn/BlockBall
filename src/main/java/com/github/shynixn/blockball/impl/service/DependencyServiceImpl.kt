package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.DependencyService
import com.github.shynixn.blockball.enumeration.PluginDependency
import com.github.shynixn.mcutils.common.ChatColor
import org.bukkit.Bukkit

class DependencyServiceImpl : DependencyService {
    private val prefix: String = ChatColor.BLUE.toString() + "[BlockBall] "

    /**
     * Checks for installed dependencies and shows console output.
     */
    override fun checkForInstalledDependencies() {
        printInstallment(PluginDependency.PLACEHOLDERAPI)
        printInstallment(PluginDependency.VAULT)
        printInstallment(PluginDependency.BOSSBARAPI)
    }

    /**
     * Returns if the given [pluginDependency] is installed.
     */
    override fun isInstalled(pluginDependency: PluginDependency, version: String?): Boolean {
        val plugin = Bukkit.getPluginManager().getPlugin(pluginDependency.pluginName) ?: return false

        if (version != null) {
            return plugin.description.version.startsWith(version)
        }

        return true
    }

    /**
     * Prints to the console if the plugin is installed.
     */
    private fun printInstallment(pluginDependency: PluginDependency) {
        if (isInstalled(pluginDependency)) {
            val plugin = Bukkit.getPluginManager().getPlugin(pluginDependency.pluginName)!!

            Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.DARK_GREEN + "found dependency [" + plugin.name + "].")
            Bukkit.getServer()
                .consoleSender.sendMessage(prefix + ChatColor.DARK_GREEN + "successfully loaded dependency [" + plugin.name + "] " + plugin.description.version + '.')
        }
    }
}
