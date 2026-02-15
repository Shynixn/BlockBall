package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.BlockBallLanguageImpl
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.htutils.coroutine.CoroutineSession
import com.github.shynixn.htutils.coroutine.CoroutineSessionImpl
import com.github.shynixn.htutils.entity.HytalePlayerProxy
import com.github.shynixn.htutils.event.HytalePacketEvent
import com.github.shynixn.htutils.plugin.HytaleLoggerProxy
import com.github.shynixn.htutils.plugin.HytalePluginManagerProxy
import com.github.shynixn.htutils.plugin.HytalePluginProxy
import com.github.shynixn.htutils.plugin.HytaleServerProxy
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.commonServer
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.event.PacketAsyncEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.PluginManager
import java.util.logging.Level
import kotlin.coroutines.CoroutineContext

class BlockBallHytalePlugin(pluginInit: JavaPluginInit) : JavaPlugin(pluginInit), CoroutineHandler {
    private val prefix: String = "[BlockBall] "
    private var module: DependencyInjectionModule? = null
    private var coroutineSession: CoroutineSession? = null

    override fun start() {
        // Fake Bukkit api
        val pluginManager = HytalePluginManagerProxy()
        val loggerProxy = HytaleLoggerProxy(this.logger)
        val logger = loggerProxy.logger
        val server = HytaleServerProxy(loggerProxy, pluginManager)
        val plugin = HytalePluginProxy(this, server, loggerProxy)
        coroutineSession = CoroutineSessionImpl(plugin)
        commonServer = server

        plugin.logger.log(Level.INFO, prefix + "Loading BlockBall ...")
        plugin.saveDefaultConfig()

        // Load BlockBallLanguage
        val language = BlockBallLanguageImpl()
        plugin.reloadTranslation(language)
        logger.log(Level.INFO, "Loaded language file.")

        // Module
        val placeHolderService = PlaceHolderServiceImpl(plugin, pluginManager)
        this.module = BlockBallHytaleDependencyInjectionModule(this, plugin, this, language, placeHolderService).build()

        // Connect to database
        try {
            val playerDataRepository = module!!.getService<PlayerDataRepository<PlayerInformation>>()
            playerDataRepository.createIfNotExist()
        } catch (e: Exception) {
            e.printStackTrace()
            pluginManager.disablePlugin(plugin)
            return
        }

        // Register PlaceHolder
        PlaceHolder.registerAll(
            module!!.getService(), module!!.getService(), module!!.getService(), module!!.getService()
        )

        // Register Packet
        module!!.getService<PacketService>().registerPacketListening(PacketInType.INTERACT)
        module!!.getService<PacketService>().registerPacketListening(PacketInType.USEENTITY)

        // Register Listeners
        pluginManager.registerEvents(module!!.getService<GameListener>(), plugin)
        pluginManager.registerEvents(module!!.getService<DoubleJumpListener>(), plugin)
        pluginManager.registerEvents(module!!.getService<HubgameListener>(), plugin)
        pluginManager.registerEvents(module!!.getService<MinigameListener>(), plugin)
        pluginManager.registerEvents(module!!.getService<BallListener>(), plugin)

        // Register CommandExecutor
        module!!.getService<BlockBallCommandExecutor>()

        // Event Pipeline Proxy
        setupEventPipeline(pluginManager)

        // Service dependencies
        this.execute {
            // Load Games
            val gameService = module!!.getService<GameService>()
            try {
                gameService.reloadAll()
            } catch (e: SoccerGameException) {
                plugin.logger.log(Level.WARNING, " Cannot start game of soccerArena ${e.arena.name}.", e)
            }

            // Enable stats
            module!!.getService<StatsService>().register()
            val playerDataRepository = module!!.getService<PlayerDataRepository<PlayerInformation>>()
            for (player in server.getOnlinePlayers()) {
                playerDataRepository.getByPlayer(player)
            }

            server.consoleSender.sendMessage(prefix + ChatColor.GREEN + "Enabled BlockBall " + plugin.description.version + " by Shynixn")
        }
    }

    override fun shutdown() {
    }

    override fun execute(
        coroutineContext: CoroutineContext,
        f: suspend () -> Unit
    ): Job {
        return coroutineSession!!.scope.launch(coroutineContext) {
            f.invoke()
        }
    }

    override fun execute(f: suspend () -> Unit): Job {
        return coroutineSession!!.scope.launch {
            f.invoke()
        }
    }

    override fun fetchEntityDispatcher(entity: Entity): CoroutineContext {
        return coroutineSession!!.getWorldDispatcher(entity.world)
    }

    override fun fetchGlobalRegionDispatcher(): CoroutineContext {
        return coroutineSession!!.dispatcherMain
    }

    override fun fetchLocationDispatcher(location: Location): CoroutineContext {
        return coroutineSession!!.getWorldDispatcher(location.world!!)
    }

    private fun setupEventPipeline(pluginManager: PluginManager) {
        eventRegistry.register(HytalePacketEvent::class.java, { event ->
            val packet = event.packet
            pluginManager.callEvent(PacketAsyncEvent(event.player, packet))
        })
        eventRegistry.register(PlayerDisconnectEvent::class.java, { event ->
            pluginManager.callEvent(PlayerQuitEvent(HytalePlayerProxy(event.playerRef, null)))
        })
        eventRegistry.register(PlayerConnectEvent::class.java, { event ->
            pluginManager.callEvent(PlayerJoinEvent(HytalePlayerProxy(event.playerRef, null)))
        })
    }
}