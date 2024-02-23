package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.DependencyVaultService
import com.github.shynixn.blockball.contract.HubGameForcefieldService
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.deprecated.YamlSerializationService
import com.github.shynixn.blockball.deprecated.YamlService
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.impl.repository.ArenaFileRepository
import com.github.shynixn.blockball.impl.service.DependencyServiceImpl
import com.github.shynixn.blockball.impl.service.*
import com.github.shynixn.blockball.impl.service.nms.v1_13_R2.Particle113R2ServiceImpl
import com.github.shynixn.blockball.impl.service.nms.v1_13_R2.RayTracingService113R2Impl
import com.github.shynixn.blockball.impl.service.nms.v1_13_R2.ScreenMessage113R1ServiceImpl
import com.github.shynixn.blockball.impl.service.nms.v1_8_R3.Particle18R3ServiceImpl
import com.github.shynixn.blockball.impl.service.nms.v1_8_R3.RayTracingService18R3Impl
import com.github.shynixn.blockball.impl.service.nms.v1_8_R3.ScreenMessage18R3ServiceImpl
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.item.ItemServiceImpl
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.sound.SoundServiceImpl
import com.github.shynixn.mcutils.packet.api.EntityService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.EntityServiceImpl
import com.google.inject.AbstractModule
import com.google.inject.Scopes
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

    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        val dependencyService = DependencyServiceImpl()

        bind(Version::class.java).toInstance(Version.serverVersion)
        bind(Plugin::class.java).toInstance(plugin)
        bind(BlockBallPlugin::class.java).toInstance(plugin)

        // Repositories
        bind(ArenaRepository::class.java).to(ArenaFileRepository::class.java).`in`(Scopes.SINGLETON)

        // Services
        bind(PacketService::class.java).toInstance(packetService)
        bind(EntityService::class.java).toInstance(EntityServiceImpl())
        bind(TemplateService::class.java).to(TemplateServiceImpl::class.java).`in`(Scopes.SINGLETON)
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
        bind(GameSoccerService::class.java).to(GameSoccerServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(RightclickManageService::class.java).to(RightclickManageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(HubGameForcefieldService::class.java).to(HubGameForcefieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BallEntityService::class.java).to(BallEntityServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BlockSelectionService::class.java).to(BlockSelectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(YamlSerializationService::class.java).to(YamlSerializationServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(YamlService::class.java).to(YamlServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ItemTypeService::class.java).to(ItemTypeServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ProxyService::class.java).to(ProxyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameExecutionService::class.java).to(GameExecutionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceArenaService::class.java).to(PersistenceArenaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyBossBarApiService::class.java).to(DependencyBossBarApiServiceImpl::class.java)
            .`in`(Scopes.SINGLETON)
        bind(DependencyService::class.java).to(DependencyServiceImpl::class.java).`in`(Scopes.SINGLETON)

        when {
            Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_13_R2)
            -> bind(RayTracingService::class.java).to(RayTracingService113R2Impl::class.java)
                .`in`(Scopes.SINGLETON)
            else -> bind(RayTracingService::class.java).to(RayTracingService18R3Impl::class.java)
                .`in`(Scopes.SINGLETON)
        }

        when {
            Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_17_R1)
            -> bind(ScreenMessageService::class.java).to(ScreenMessage113R1ServiceImpl::class.java)
                .`in`(Scopes.SINGLETON)
            else -> bind(ScreenMessageService::class.java).to(ScreenMessage18R3ServiceImpl::class.java)
                .`in`(Scopes.SINGLETON)
        }

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
