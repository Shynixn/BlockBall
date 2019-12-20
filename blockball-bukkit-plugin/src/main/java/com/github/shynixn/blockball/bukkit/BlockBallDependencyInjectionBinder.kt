package com.github.shynixn.blockball.bukkit

import com.github.shynixn.blockball.api.business.enumeration.PluginDependency
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.context.SqlDbContext
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.api.persistence.repository.LinkSignRepository
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_10_R1.EntityRegistration110R1ServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_11_R1.EntityRegistration111R1ServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_13_R2.EntityRegistration113R2ServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_14_R1.EntityRegistration114R1ServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_15_R1.EntityRegistration115R1ServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_8_R3.EntityRegistrationLegacyServiceImpl
import com.github.shynixn.blockball.bukkit.logic.business.service.*
import com.github.shynixn.blockball.core.logic.business.service.*
import com.github.shynixn.blockball.core.logic.persistence.context.SqlDbContextImpl
import com.github.shynixn.blockball.core.logic.persistence.repository.ArenaFileRepository
import com.github.shynixn.blockball.core.logic.persistence.repository.LinkSignFileRepository
import com.github.shynixn.blockball.core.logic.persistence.repository.StatsSqlRepository
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.bukkit.plugin.Plugin

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class BlockBallDependencyInjectionBinder(private val plugin: BlockBallPlugin) : AbstractModule() {

    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        val version = plugin.getServerVersion()
        val dependencyService = DependencyServiceImpl()

        bind(Plugin::class.java).toInstance(plugin)
        bind(Version::class.java).toInstance(version)
        bind(PluginProxy::class.java).toInstance(plugin)
        bind(LoggingService::class.java).toInstance(LoggingUtilServiceImpl(plugin.logger))

        // Repositories
        bind(ArenaRepository::class.java).to(ArenaFileRepository::class.java).`in`(Scopes.SINGLETON)
        bind(StatsRepository::class.java).to(StatsSqlRepository::class.java).`in`(Scopes.SINGLETON)
        bind(LinkSignRepository::class.java).to(LinkSignFileRepository::class.java).`in`(Scopes.SINGLETON)

        // Services
        bind(SqlDbContext::class.java).to(SqlDbContextImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ItemService::class.java).to(ItemServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(TemplateService::class.java).to(TemplateServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(VirtualArenaService::class.java).to(VirtualArenaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ScoreboardService::class.java).to(ScoreboardServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ScreenMessageService::class.java).to(ScreenMessageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(UpdateCheckService::class.java).to(UpdateCheckServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ConfigurationService::class.java).to(ConfigurationServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(StatsCollectingService::class.java).to(StatsCollectingServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(SoundService::class.java).to(SoundServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BossBarService::class.java).to(BossBarServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameService::class.java).to(GameServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameActionService::class.java).to(GameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameHubGameActionService::class.java).to(GameHubGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameMiniGameActionService::class.java).to(GameMiniGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameBungeeCordGameActionService::class.java).to(GameBungeeCordGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameSoccerService::class.java).to(GameSoccerServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(RightclickManageService::class.java).to(RightclickManageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(HubGameForcefieldService::class.java).to(HubGameForcefieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BungeeCordService::class.java).to(BungeeCordServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BungeeCordConnectionService::class.java).to(BungeeCordConnectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BallEntityService::class.java).to(BallEntityServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BlockSelectionService::class.java).to(BlockSelectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BallForceFieldService::class.java).to(BallForceFieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(YamlSerializationService::class.java).to(YamlSerializationServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(YamlService::class.java).to(YamlServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ItemTypeService::class.java).to(ItemTypeServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ConcurrencyService::class.java).to(ConcurrencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ProxyService::class.java).to(ProxyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(CommandService::class.java).to(CommandServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameExecutionService::class.java).to(GameExecutionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceLinkSignService::class.java).to(PersistenceLinkSignServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceArenaService::class.java).to(PersistenceArenaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceStatsService::class.java).to(PersistenceStatsServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyBossBarApiService::class.java).to(DependencyBossBarApiServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyService::class.java).to(DependencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PackageService::class.java).to(PackageServiceImpl::class.java).`in`(Scopes.SINGLETON)

        when {
            version.isVersionSameOrGreaterThan(Version.VERSION_1_15_R1)
            -> bind(EntityRegistrationService::class.java).to(EntityRegistration115R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            version.isVersionSameOrGreaterThan(Version.VERSION_1_14_R1)
            -> bind(EntityRegistrationService::class.java).to(EntityRegistration114R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            version.isVersionSameOrGreaterThan(Version.VERSION_1_13_R2)
            -> bind(EntityRegistrationService::class.java).to(EntityRegistration113R2ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            version.isVersionSameOrGreaterThan(Version.VERSION_1_11_R1)
            -> bind(EntityRegistrationService::class.java).to(EntityRegistration111R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            version.isVersionSameOrGreaterThan(Version.VERSION_1_10_R1)
            -> bind(EntityRegistrationService::class.java).to(EntityRegistration110R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            else -> bind(EntityRegistrationService::class.java).to(EntityRegistrationLegacyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        when {
            version.isVersionSameOrGreaterThan(Version.VERSION_1_13_R2) -> bind(ParticleService::class.java).to(Particle113R2ServiceImpl::class.java).`in`(
                Scopes.SINGLETON
            )
            else -> bind(ParticleService::class.java).to(Particle18R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        if (dependencyService.isInstalled(PluginDependency.WORLEDIT, "7")) {
            bind(DependencyWorldEditService::class.java).to(DependencyWorldEdit7ServiceImpl::class.java).`in`(Scopes.SINGLETON)
        } else {
            bind(DependencyWorldEditService::class.java).to(DependencyWorldEdit6ServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        if (dependencyService.isInstalled(PluginDependency.PLACEHOLDERAPI)) {
            val placeHolderApiService = DependencyPlaceholderApiServiceImpl(plugin)
            bind(DependencyPlaceholderApiService::class.java).toInstance(placeHolderApiService)
            placeHolderApiService.registerListener()
        }

        if (dependencyService.isInstalled(PluginDependency.VAULT)) {
            bind(DependencyVaultService::class.java).to(DependencyVaultServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }
    }
}
