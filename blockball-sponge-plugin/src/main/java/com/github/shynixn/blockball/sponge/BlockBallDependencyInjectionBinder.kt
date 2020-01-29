package com.github.shynixn.blockball.sponge

import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.core.logic.business.service.*
import com.github.shynixn.blockball.core.logic.persistence.repository.ArenaFileRepository
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.spongepowered.api.plugin.PluginContainer

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
class BlockBallDependencyInjectionBinder(private val plugin: PluginContainer, private val blockBallPlugin: BlockBallPlugin) : AbstractModule() {

    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        bind(PluginProxy::class.java).toInstance(blockBallPlugin)
        // TODO: bind(LoggingService::class.java).toInstance(LoggingSlf4jServiceImpl(plugin.logger))

        // Repositories
        bind(ArenaRepository::class.java).to(ArenaFileRepository::class.java)
        // TODO: Refactor Persistence to core. bind(PlayerRepository::class.java).to(PlayerSqlRepository::class.java)
        // TODO: Refactor Persistence to core. bind(StatsRepository::class.java).to(StatsSqlRepository::class.java)
        // TODO: Refactor Persistence to core. bind(LinkSignRepository::class.java).to(LinkSignFileRepository::class.java).`in`(Scopes.SINGLETON)

        // Persistence Services
        bind(PersistenceLinkSignService::class.java).to(PersistenceLinkSignServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceArenaService::class.java).to(PersistenceArenaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceStatsService::class.java).to(PersistenceStatsServiceImpl::class.java)

        // Services
        // TODO: bind(ItemService::class.java).to(ItemServiceImpl::class.java)
        // TODO: bind(TemplateService::class.java).to(TemplateServiceImpl::class.java)
        // TODO: bind(VirtualArenaService::class.java).to(VirtualArenaServiceImpl::class.java)
        // TODO: bind(ScoreboardService::class.java).to(ScoreboardServiceImpl::class.java)
        // TODO: bind(ScreenMessageService::class.java).to(ScreenMessageServiceImpl::class.java)
        // TODO: bind(ConfigurationService::class.java).to(ConfigurationServiceImpl::class.java)
        // TODO: bind(StatsCollectingService::class.java).to(StatsCollectingServiceImpl::class.java)
        // TODO: bind(ParticleService::class.java).to(ParticleServiceImpl::class.java)
        // TODO: bind(SoundService::class.java).to(SoundServiceImpl::class.java)
        // TODO: bind(BossBarService::class.java).to(BossBarServiceImpl::class.java)
        // TODO: bind(GameService::class.java).to(GameServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(GameActionService::class.java).to(GameActionServiceImpl::class.java)
        // TODO: bind(GameHubGameActionService::class.java).to(GameHubGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(GameMiniGameActionService::class.java).to(GameMiniGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(GameBungeeCordGameActionService::class.java).to(GameBungeeCordGameActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(GameSoccerService::class.java).to(GameSoccerServiceImpl::class.java)
        // TODO: bind(RightclickManageService::class.java).to(RightclickManageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(HubGameForcefieldService::class.java).to(HubGameForcefieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(BungeeCordConnectionService::class.java).to(BungeeCordConnectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(BallEntityService::class.java).to(BallEntityServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(BlockSelectionService::class.java).to(BlockSelectionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(BallForceFieldService::class.java).to(BallForceFieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(YamlService::class.java).to(YamlServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(ConcurrencyService::class.java).to(ConcurrencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(SpigotTimingService::class.java).to(SpigotTimingServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(ProxyService::class.java).to(ProxyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(CommandService::class.java).to(CommandServiceImpl::class.java).`in`(Scopes.SINGLETON)
        // TODO: bind(DependencyService::class.java).to(DependencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BungeeCordService::class.java).to(BungeeCordServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(YamlSerializationService::class.java).to(YamlSerializationServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(UpdateCheckService::class.java).to(UpdateCheckServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GameExecutionService::class.java).to(GameExecutionServiceImpl::class.java).`in`(Scopes.SINGLETON)
    }
}