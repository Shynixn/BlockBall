package com.github.shynixn.blockball.bukkit

import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.api.persistence.repository.PlayerRepository
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.bukkit.logic.business.service.*
import com.github.shynixn.blockball.bukkit.logic.persistence.repository.ArenaFileRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.repository.PlayerSqlRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.repository.StatsSqlRepository
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
class BlockBallDependencyInjectionBinder(private val plugin: Plugin) : AbstractModule() {

    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        bind(Plugin::class.java).toInstance(plugin)

        // Repositories
        bind(ArenaRepository::class.java).to(ArenaFileRepository::class.java)
        bind(PlayerRepository::class.java).to(PlayerSqlRepository::class.java)
        bind(StatsRepository::class.java).to(StatsSqlRepository::class.java)

        // Services
        bind(TemplateService::class.java).to(TemplateServiceImpl::class.java)
        bind(VirtualArenaService::class.java).to(VirtualArenaServiceImpl::class.java)
        bind(ScoreboardService::class.java).to(ScoreboardServiceImpl::class.java)
        bind(ScreenMessageService::class.java).to(ScreenMessageServiceImpl::class.java)
        bind(UpdateCheckService::class.java).to(UpdateCheckServiceImpl::class.java)
        bind(ConfigurationService::class.java).to(ConfigurationServiceImpl::class.java)
        bind(StatsCollectingService::class.java).to(StatsCollectingServiceImpl::class.java)
        bind(RightclickManageService::class.java).to(RightclickManageServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(HubGameForcefieldService::class.java).to(HubGameForcefieldServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DoubleJumpService::class.java).to(DoubleJumpServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(BungeeCordService::class.java).to(BungeeCordServiceImpl::class.java).`in`(Scopes.SINGLETON)

        // Persistence Services
        bind(PersistenceLinkSignService::class.java).to(PersistenceLinkSignServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceArenaService::class.java).to(PersistenceArenaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistenceStatsService::class.java).to(PersistenceStatsServiceImpl::class.java)

        // Dependency Services
        bind(DependencyVaultService::class.java).to(DependencyVaultServiceImpl::class.java)
        bind(DependencyBossBarApiService::class.java).to(DependencyBossBarApiServiceImpl::class.java)
        bind(DependencyService::class.java).to(DependencyServiceImpl::class.java)
        bind(DependencyWorldEditService::class.java).to(DependencyWorldEditServiceImpl::class.java)
        bind(DependencyPlaceholderApiService::class.java).to(DependencyPlaceholderApiServiceImpl::class.java)
    }
}