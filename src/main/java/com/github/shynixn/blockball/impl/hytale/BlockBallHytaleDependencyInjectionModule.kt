package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.blockball.impl.service.CloudServiceImpl
import com.github.shynixn.blockball.impl.service.GameServiceImpl
import com.github.shynixn.blockball.impl.service.StatsServiceImpl
import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.htutils.plugin.HytalePluginProxy
import com.github.shynixn.htutils.service.EmptyParticleServiceImpl
import com.github.shynixn.htutils.service.EmptySoundServiceImpl
import com.github.shynixn.htutils.service.HytaleAreaSelectionServiceImpl
import com.github.shynixn.htutils.service.HytaleChatServiceImpl
import com.github.shynixn.htutils.service.HytaleCommandServiceImpl
import com.github.shynixn.htutils.service.HytaleItemServiceImpl
import com.github.shynixn.htutils.service.HytalePacketServiceImpl
import com.github.shynixn.htutils.service.HytaleRayTracingServiceImpl
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.database.impl.AutoSavePlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.CachedPlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.CommonSqlConnectionServiceImpl
import com.github.shynixn.mcutils.database.impl.PlayerDataSqlRepositoryImpl
import com.github.shynixn.mcutils.http.HttpClientFactory
import com.github.shynixn.mcutils.http.HttpClientFactoryImpl
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.shyparticles.contract.ParticleEffectService
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager

class BlockBallHytaleDependencyInjectionModule(
    private val coroutineHandler: CoroutineHandler,
    private val plugin: HytalePluginProxy,
    private val javaPlugin: JavaPlugin,
    private val language: BlockBallLanguage,
    private val placeHolderService: PlaceHolderService
) {
    fun build(): DependencyInjectionModule {
        val module = DependencyInjectionModule()

        // Params
        module.addService<Plugin>(plugin)
        module.addService<Version> { Version.HYTALE_LATEST }
        module.addService<CoroutineHandler>(coroutineHandler)
        module.addService<PluginManager> { plugin.server.pluginManager }
        module.addService<Server>(plugin.server)
        module.addService<BlockBallLanguage>(language)
        module.addService<PlaceHolderService>(placeHolderService)

        // Repositories
        module.addService<Repository<SoccerArena>> {
            module.getService<CacheRepository<SoccerArena>>()
        }
        module.addService<CacheRepository<SoccerArena>> {
            CachedRepositoryImpl(
                YamlFileRepositoryImpl<SoccerArena>(
                    plugin,
                    "arena",
                    plugin.dataFolder.toPath().resolve("arena"),
                    listOf(),
                    listOf("arena_sample.yml"),
                    object : TypeReference<SoccerArena>() {}
                ))
        }
        module.addService<PlayerDataRepository<PlayerInformation>> {
            module.getService<CachePlayerRepository<PlayerInformation>>()
        }
        module.addService<CachePlayerRepository<PlayerInformation>> {
            val sqlConnectionService =
                CommonSqlConnectionServiceImpl(plugin, plugin.dataFolder.toPath().resolve("BlockBall.sqlite"))
            val configSelectedPlayerDataRepository = PlayerDataSqlRepositoryImpl<PlayerInformation>(
                "BlockBall",
                0L,
                object : TypeReference<PlayerInformation>() {},
                sqlConnectionService
            )
            AutoSavePlayerDataRepositoryImpl(
                1000 * 60L * plugin.config.getInt("database.autoSaveIntervalMinutes"),
                CachedPlayerDataRepositoryImpl(configSelectedPlayerDataRepository),
                coroutineHandler
            )
        }

        // Services
        module.addService<CommandService> {
            HytaleCommandServiceImpl(module.getService(), module.getService())
        }
        module.addService<PacketService> {
            HytalePacketServiceImpl(plugin, coroutineHandler)
        }
        module.addService<ConfigurationService> {
            ConfigurationServiceImpl(module.getService())
        }
        module.addService<SoundService> {
            EmptySoundServiceImpl()
        }
        module.addService<ItemService> {
            HytaleItemServiceImpl()
        }
        module.addService<ParticleEffectService> {
            EmptyParticleServiceImpl()
        }
        module.addService<ChatMessageService> {
            HytaleChatServiceImpl(module.getService())
        }
        module.addService<RayTracingService> {
            HytaleRayTracingServiceImpl()
        }
        module.addService<HttpClientFactory> { HttpClientFactoryImpl() }
        module.addService<CloudService> {
            CloudServiceImpl(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<AreaSelectionService> {
            HytaleAreaSelectionServiceImpl(
                plugin,
                javaPlugin,
                coroutineHandler,
                module.getService(),
                Permission.EDIT_GAME.permission
            )
        }
        module.addService<StatsService> {
            StatsServiceImpl(module.getService(), module.getService(), module.getService())
        }
        module.addService<GameService> {
            GameServiceImpl(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<SoccerBallFactory> {
            HytaleSoccerBallFactoryImpl(
                module.getService(),
                module.getService(),
                module.getService(),
            )
        }
        module.addService<BallListener> { BallListener(module.getService(), module.getService()) }
        module.addService<DoubleJumpListener> {
            DoubleJumpListener(
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<GameListener> {
            GameListener(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<HubgameListener> {
            HubgameListener(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<MinigameListener> { MinigameListener(module.getService(), module.getService()) }
        module.addService<BlockBallCommandExecutor> {
            BlockBallCommandExecutor(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        return module
    }
}
