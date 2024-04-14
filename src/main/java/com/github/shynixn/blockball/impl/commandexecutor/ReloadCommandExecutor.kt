package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.reloadTranslation
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class ReloadCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val configurationService: ConfigurationService,
    private val plugin: Plugin,
    private val arenaRepository : CacheRepository<Arena>
) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        configurationService.reload()
        val language = configurationService.findValue<String>("language")
        plugin.launch {
            arenaRepository.clearCache()
            plugin.reloadTranslation(language, BlockBallLanguage::class.java, "en_us")
            gameService.reloadAll()
            source.sendMessage(BlockBallLanguage.reloadMessage)
        }
        return true
    }
}
