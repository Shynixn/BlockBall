package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.reloadTranslation
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class ReloadCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val proxyService: ProxyService,
    private val configurationService: ConfigurationService,
    private val plugin: Plugin
) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        configurationService.reload()
        val language = configurationService.findValue<String>("language")
        // TODO: Replace with plugin.launch
        runBlocking {
            plugin.reloadTranslation(language, BlockBallLanguage::class.java, "en_us")
            gameService.reloadAll()
        }
        proxyService.sendMessage(source, BlockBallLanguage.reloadMessage)
        return true
    }
}
