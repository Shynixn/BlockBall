package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.blockball.impl.service.GameServiceImpl
import com.github.shynixn.blockball.impl.service.HubGameForcefieldServiceImpl
import com.github.shynixn.blockball.impl.service.SoccerBallFactoryImpl
import com.github.shynixn.blockball.impl.service.StatsServiceImpl
import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutinePlugin
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.sound.SoundServiceImpl
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.database.impl.AutoSavePlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.CachedPlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.ConfigSelectedRepositoryImpl
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.impl.service.*
import org.bukkit.plugin.Plugin

class BlockBallDependencyInjectionModule(
    private val plugin: BlockBallPlugin,
    private val language: BlockBallLanguage,
    private val placeHolderService: PlaceHolderService,
) {
    companion object {
        val areLegacyVersionsIncluded: Boolean by lazy {
            try {
                Class.forName("com.github.shynixn.blockball.lib.com.github.shynixn.mcutils.packet.nms.v1_8_R3.PacketSendServiceImpl")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    fun build(): DependencyInjectionModule {
        val module = DependencyInjectionModule()

        // Params
        module.addService<Plugin>(plugin)
        module.addService<CoroutinePlugin>(plugin)
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
            val configSelectedPlayerDataRepository = ConfigSelectedRepositoryImpl<PlayerInformation>(
                plugin,
                "BlockBall",
                plugin.dataFolder.toPath().resolve("BlockBall.sqlite"),
                object : TypeReference<PlayerInformation>() {}
            )
            AutoSavePlayerDataRepositoryImpl(
                1000 * 60L * plugin.config.getInt("database.autoSaveIntervalMinutes"),
                CachedPlayerDataRepositoryImpl(configSelectedPlayerDataRepository),
                plugin
            )
        }

        // Services
        module.addService<CommandService> {
            CommandServiceImpl(module.getService())
        }
        module.addService<PacketService> {
            PacketServiceImpl(module.getService())
        }
        module.addService<ConfigurationService> {
            ConfigurationServiceImpl(module.getService())
        }
        module.addService<SoundService> {
            SoundServiceImpl(module.getService())
        }
        module.addService<ItemService> {
            ItemServiceImpl()
        }
        module.addService<ChatMessageService> {
            ChatMessageServiceImpl(module.getService(), module.getService())
        }
        module.addService<RayTracingService> {
            RayTracingServiceImpl()
        }
        module.addService<AreaSelectionService> {
            AreaSelectionServiceImpl(
                Permission.EDIT_GAME.permission,
                module.getService(),
                module.getService(),
                module.getService(),
            )
        }
        module.addService<StatsService> {
            StatsServiceImpl(module.getService(), module.getService())
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
            )
        }
        module.addService<HubGameForcefieldService> {
            HubGameForcefieldServiceImpl(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<SoccerBallFactory> {
            SoccerBallFactoryImpl(module.getService(), module.getService(), module.getService(), module.getService())
        }
        module.addService<BallListener> { BallListener(module.getService(), module.getService()) }
        module.addService<DoubleJumpListener> { DoubleJumpListener(module.getService(), module.getService()) }
        module.addService<GameListener> {
            GameListener(module.getService(), module.getService(), module.getService(), module.getService())
        }
        module.addService<HubgameListener> { HubgameListener(module.getService()) }
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
            )
        }
        return module
    }
}
