package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.api.business.executor.CommandExecutor
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.reloadTranslation
import com.google.inject.Inject
import kotlinx.coroutines.runBlocking
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
    override fun <S> onExecuteCommand(source: S, args: Array<out String>): Boolean {
        configurationService.reload()
        val language = configurationService.findValue<String>("language")
        runBlocking {
            plugin.reloadTranslation(language, BlockBallLanguage::class.java, "en_us")
        }
        this.gameService.restartGames()
        proxyService.sendMessage(source, BlockBallLanguage.reloadMessage)
        return true
    }
}
