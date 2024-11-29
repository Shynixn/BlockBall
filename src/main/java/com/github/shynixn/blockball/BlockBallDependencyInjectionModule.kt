package com.github.shynixn.blockball

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.impl.service.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.item.ItemService
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
import com.github.shynixn.mcutils.database.impl.CachePlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.ConfigSelectedRepositoryImpl
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.ItemServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.RayTracingServiceImpl
import com.github.shynixn.mcutils.packet.nms.v1_21_R2.AreaSelectionServiceImpl
import com.github.shynixn.mcutils.sign.SignService
import com.github.shynixn.mcutils.sign.SignServiceImpl
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class BlockBallDependencyInjectionModule(
    private val plugin: BlockBallPlugin,
    private val language: Language
) : DependencyInjectionModule() {
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

    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        addService<Plugin>(plugin)
        addService<Language>(language)

        // Repositories
        val arenaRepository = YamlFileRepositoryImpl<SoccerArena>(plugin, "arena",
            listOf(Pair("arena_sample.yml", "arena_sample.yml")),
            listOf("arena_sample.yml"),
            object : TypeReference<SoccerArena>() {}
        )
        val cacheArenaRepository = CachedRepositoryImpl(arenaRepository)
        addService<Repository<SoccerArena>>(cacheArenaRepository)
        addService<CacheRepository<SoccerArena>>(cacheArenaRepository)
        val configSelectedPlayerDataRepository = ConfigSelectedRepositoryImpl<PlayerInformation>(
            plugin,
            "BlockBall",
            plugin.dataFolder.toPath().resolve("BlockBall.sqlite"),
            object : TypeReference<PlayerInformation>() {},
            plugin.minecraftDispatcher
        )
        val playerDataRepository = AutoSavePlayerDataRepositoryImpl(
            1000 * 60L * plugin.config.getInt("database.autoSaveIntervalMinutes"),
            CachePlayerDataRepositoryImpl(configSelectedPlayerDataRepository, plugin.minecraftDispatcher),
            plugin.scope, plugin.minecraftDispatcher
        )
        addService<PlayerDataRepository<PlayerInformation>>(playerDataRepository)
        addService<CachePlayerRepository<PlayerInformation>>(playerDataRepository)
        addService<SignService> {
            SignServiceImpl(plugin, getService(),language.noPermissionMessage.text)
        }

        // Services
        addService<com.github.shynixn.mcutils.common.command.CommandService>(
            com.github.shynixn.mcutils.common.command.CommandServiceImpl(
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch { f.invoke() }
                    }
                })
        )
        addService<StatsService, StatsServiceImpl>()
        addService<PacketService>(PacketServiceImpl(plugin))
        addService<ScoreboardService, ScoreboardServiceImpl>()
        addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        addService<SoundService>(SoundServiceImpl(plugin))
        addService<BossBarService, BossBarServiceImpl>()
        addService<GameService, GameServiceImpl>()
        addService<ItemService>(ItemServiceImpl())
        addService<ChatMessageService>(ChatMessageServiceImpl(plugin))
        addService<HubGameForcefieldService, HubGameForcefieldServiceImpl>()
        addService<SoccerBallFactory, SoccerBallFactoryImpl>()
        addService<AreaSelectionService> {
            AreaSelectionServiceImpl(
                Permission.EDIT_GAME.permission,
                plugin,
                getService<ItemService>(),
                getService<PacketService>(),
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch { f.invoke() }
                    }
                },
                plugin.minecraftDispatcher as CoroutineDispatcher,
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch { f.invoke() }
                    }
                }
            )
        }
        addService<RayTracingService, RayTracingServiceImpl>()

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            addService<PlaceHolderService, DependencyPlaceHolderServiceImpl>()
            plugin.logger.log(Level.INFO, "Loaded dependency PlaceholderAPI.")
        } else {
            addService<PlaceHolderService, PlaceHolderServiceImpl>()
        }
    }
}
