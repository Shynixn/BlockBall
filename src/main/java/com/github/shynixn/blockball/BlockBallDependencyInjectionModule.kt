package com.github.shynixn.blockball

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.PluginDependency
import com.github.shynixn.blockball.impl.service.*
import com.github.shynixn.blockball.impl.service.nms.v1_13_R2.Particle113R2ServiceImpl
import com.github.shynixn.blockball.impl.service.nms.v1_8_R3.Particle18R3ServiceImpl
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.sound.SoundServiceImpl
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.database.impl.AutoSavePlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.CachePlayerDataRepositoryImpl
import com.github.shynixn.mcutils.database.impl.ConfigSelectedRepositoryImpl
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.EntityService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.impl.service.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class BlockBallDependencyInjectionModule(
    private val plugin: BlockBallPlugin
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

        // Repositories
        val arenaRepository = YamlFileRepositoryImpl<Arena>(plugin, "arena",
            listOf(Pair("arena_sample.yml", "arena_sample.yml")),
            listOf("arena_sample.yml"),
            object : TypeReference<Arena>() {}
        )
        val cacheArenaRepository = CachedRepositoryImpl(arenaRepository)
        addService<Repository<Arena>>(cacheArenaRepository)
        addService<CacheRepository<Arena>>(cacheArenaRepository)
        val configSelectedPlayerDataRepository = ConfigSelectedRepositoryImpl<PlayerInformation>(
            plugin,
            "BlockBall",
            plugin.dataFolder.toPath().resolve("BlockBall.sqlite"),
            object : TypeReference<PlayerInformation>() {}
        )
        val playerDataRepository = AutoSavePlayerDataRepositoryImpl(
            "stats",
            1000 * 60L * plugin.config.getInt("database.autoSaveIntervalMinutes"),
            CachePlayerDataRepositoryImpl(configSelectedPlayerDataRepository, plugin),
            plugin
        )
        addService<PlayerDataRepository<PlayerInformation>>(playerDataRepository)
        addService<CachePlayerRepository<PlayerInformation>>(playerDataRepository)

        // Services
        addService<CommandService, CommandServiceImpl>()
        addService<PacketService>(PacketServiceImpl(plugin))
        addService<EntityService>(EntityServiceImpl())
        addService<ScoreboardService, ScoreboardServiceImpl>()
        addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        addService<SoundService>(SoundServiceImpl(plugin))
        addService<BossBarService, BossBarServiceImpl>()
        addService<GameService, GameServiceImpl>()
        addService<GameActionService, GameActionServiceImpl>()
        addService<GameHubGameActionService, GameHubGameActionServiceImpl>()
        addService<GameMiniGameActionService, GameMiniGameActionServiceImpl>()
        addService<GameBungeeCordGameActionService, GameBungeeCordGameActionServiceImpl>()
        addService<ItemService>(ItemServiceImpl())
        addService<ChatMessageService, ChatMessageServiceImpl>()
        addService<GameSoccerService, GameSoccerServiceImpl>()
        addService<RightclickManageService, RightclickManageServiceImpl>()
        addService<HubGameForcefieldService, HubGameForcefieldServiceImpl>()
        addService<BallEntityService, BallEntityServiceImpl>()
        addService<BlockSelectionService, BlockSelectionServiceImpl>()
        addService<ItemTypeService, ItemTypeServiceImpl>()
        addService<GameExecutionService, GameExecutionServiceImpl>()
        addService<DependencyBossBarApiService, DependencyBossBarApiServiceImpl>()
        addService<DependencyService, DependencyServiceImpl>()
        addService<RayTracingService, RayTracingServiceImpl>()

        when {
            Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_13_R2) -> addService<ParticleService, Particle113R2ServiceImpl>()
            else -> addService<ParticleService, Particle18R3ServiceImpl>()
        }

        if (Bukkit.getPluginManager().getPlugin(PluginDependency.PLACEHOLDERAPI.pluginName) != null) {
            addService<PlaceHolderService, DependencyPlaceHolderServiceImpl>()
            plugin.logger.log(Level.INFO, "Loaded dependency ${PluginDependency.PLACEHOLDERAPI.pluginName}.")
        } else {
            addService<PlaceHolderService, PlaceHolderServiceImpl>()
        }

        if (Bukkit.getPluginManager().getPlugin(PluginDependency.VAULT.pluginName) != null) {
            addService<DependencyVaultService, DependencyVaultServiceImpl>()
        }
    }
}
