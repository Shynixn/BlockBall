package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.blockball.impl.service.*
import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mccoroutine.bukkit.CoroutineTimings
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.globalChatMessageService
import com.github.shynixn.mcutils.common.language.globalPlaceHolderService
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
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.ItemServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.RayTracingServiceImpl
import com.github.shynixn.mcutils.packet.nms.v1_21_R5.AreaSelectionServiceImpl
import kotlinx.coroutines.CoroutineDispatcher
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
        module.addService<BlockBallLanguage>(language)

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
                object : TypeReference<PlayerInformation>() {},
                plugin.minecraftDispatcher
            )
            AutoSavePlayerDataRepositoryImpl(
                1000 * 60L * plugin.config.getInt("database.autoSaveIntervalMinutes"),
                CachedPlayerDataRepositoryImpl(configSelectedPlayerDataRepository, plugin.minecraftDispatcher),
                plugin.scope, plugin.minecraftDispatcher
            )
        }
        // Library Services
        module.addService<com.github.shynixn.mcutils.common.command.CommandService>(
            com.github.shynixn.mcutils.common.command.CommandServiceImpl(
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch(object : CoroutineTimings() {}) {
                            f.invoke()
                        }
                    }
                })
        )
        module.addService<PacketService>(PacketServiceImpl(plugin))
        module.addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        module.addService<SoundService>(SoundServiceImpl(plugin))
        module.addService<ItemService>(ItemServiceImpl())
        module.addService<ChatMessageService>(ChatMessageServiceImpl(plugin))
        module.addService<RayTracingService>(RayTracingServiceImpl())
        module.addService<AreaSelectionService> {
            AreaSelectionServiceImpl(
                Permission.EDIT_GAME.permission,
                plugin,
                module.getService<ItemService>(),
                module.getService<PacketService>(),
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch(object : CoroutineTimings() {}) {
                            f.invoke()
                        }
                    }
                },
                plugin.minecraftDispatcher as CoroutineDispatcher,
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch(object : CoroutineTimings() {}) {
                            f.invoke()
                        }
                    }
                }
            )
        }
        module.addService<PlaceHolderService> { placeHolderService }

        // Services
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
            SoccerBallFactoryImpl(module.getService(), module.getService(), module.getService(), plugin)
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
        plugin.globalChatMessageService = module.getService()
        plugin.globalPlaceHolderService = module.getService()
        return module
    }
}
