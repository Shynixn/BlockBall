package com.github.shynixn.blockball

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.impl.service.*
import com.github.shynixn.blockball.impl.service.nms.v1_13_R2.Particle113R2ServiceImpl
import com.github.shynixn.blockball.impl.service.nms.v1_8_R3.Particle18R3ServiceImpl
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.item.ItemServiceImpl
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
import com.github.shynixn.mcutils.packet.api.EntityService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.EntityServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.RayTracingServiceImpl
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.TypeLiteral
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

/**
 * BlockBall dependency locator.
 */
class BlockBallDependencyInjectionBinder(
    private val plugin: BlockBallPlugin,
    private val packetService: PacketService
) : AbstractModule() {
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
        val dependencyService = DependencyServiceImpl()

        bind(Version::class.java).toInstance(Version.serverVersion)
        bind(Plugin::class.java).toInstance(plugin)
        bind(BlockBallPlugin::class.java).toInstance(plugin)

        // Arena Repository
        val arenaRepository = YamlFileRepositoryImpl<Arena>(plugin, "arena",
            listOf(Pair("arena_sample.yml", "arena_sample.yml")),
            listOf("arena_sample.yml"),
            object : TypeReference<Arena>() {}
        )
        val cacheArenaRepository = CachedRepositoryImpl(arenaRepository)
        bind(object : TypeLiteral<Repository<Arena>>() {}).toInstance(cacheArenaRepository)
        bind(object : TypeLiteral<CacheRepository<Arena>>() {}).toInstance(cacheArenaRepository)
        // PlayerData Repository.
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
        bind(object : TypeLiteral<PlayerDataRepository<PlayerInformation>>() {}).toInstance(playerDataRepository)
        bind(object : TypeLiteral<CachePlayerRepository<PlayerInformation>>() {}).toInstance(playerDataRepository)
        bind(PlayerDataRepository::class.java).toInstance(playerDataRepository)
        bind(CachePlayerRepository::class.java).toInstance(playerDataRepository)

        // Services
        bind(CommandService::class.java).to(CommandServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PacketService::class.java).toInstance(packetService)
        bind(EntityService::class.java).toInstance(EntityServiceImpl())
        bind(ScoreboardService::class.java).to(ScoreboardServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ConfigurationService::class.java).toInstance(ConfigurationServiceImpl(plugin))
        bind(SoundService::class.java).toInstance(SoundServiceImpl(plugin))
        bind(BossBarService::class.java).to(BossBarServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameService::class.java).to(GameServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameActionService::class.java).to(GameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameHubGameActionService::class.java).to(GameHubGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameMiniGameActionService::class.java).to(GameMiniGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameBungeeCordGameActionService::class.java).to(GameBungeeCordGameActionServiceImpl::class.java)
            .`in`(Scopes.SINGLETON)
        bind(ItemService::class.java).to(ItemServiceImpl::class.java)
            .`in`(Scopes.SINGLETON)
        bind(ChatMessageService::class.java).toInstance(ChatMessageServiceImpl())
        bind(GameSoccerService::class.java).to(GameSoccerServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(RightclickManageService::class.java).to(RightclickManageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(HubGameForcefieldService::class.java).to(HubGameForcefieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BallEntityService::class.java).to(BallEntityServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BlockSelectionService::class.java).to(BlockSelectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ItemTypeService::class.java).to(ItemTypeServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameExecutionService::class.java).to(GameExecutionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyBossBarApiService::class.java).to(DependencyBossBarApiServiceImpl::class.java)
            .`in`(Scopes.SINGLETON)
        bind(DependencyService::class.java).to(DependencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(RayTracingService::class.java).toInstance(RayTracingServiceImpl())

        when {
            Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_13_R2) -> bind(ParticleService::class.java).to(
                Particle113R2ServiceImpl::class.java
            ).`in`(
                Scopes.SINGLETON
            )
            else -> bind(ParticleService::class.java).to(Particle18R3ServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        if (Bukkit.getPluginManager().getPlugin(PluginDependency.PLACEHOLDERAPI.pluginName) != null) {
            bind(PlaceHolderService::class.java).to(DependencyPlaceHolderServiceImpl::class.java)
                .`in`(Scopes.SINGLETON)
            plugin.logger.log(Level.INFO, "Loaded dependency ${PluginDependency.PLACEHOLDERAPI.pluginName}.")
        } else {
            bind(PlaceHolderService::class.java).to(PlaceHolderServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        if (dependencyService.isInstalled(PluginDependency.VAULT)) {
            bind(DependencyVaultService::class.java).to(DependencyVaultServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }
    }
}
