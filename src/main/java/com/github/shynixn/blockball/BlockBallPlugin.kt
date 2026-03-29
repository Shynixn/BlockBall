package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.folia.*
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.database.api.SqlConnectionService
import com.github.shynixn.mcutils.database.impl.CommonSqlConnectionServiceImpl
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.worldguard.WorldGuardServiceImpl
import com.github.shynixn.shybossbar.ShyBossBarDependencyInjectionModule
import com.github.shynixn.shybossbar.contract.BossBarService
import com.github.shynixn.shybossbar.contract.ShyBossBarLanguage
import com.github.shynixn.shybossbar.entity.ShyBossBarSettings
import com.github.shynixn.shybossbar.impl.commandexecutor.ShyBossBarCommandExecutor
import com.github.shynixn.shybossbar.impl.listener.ShyBossBarListener
import com.github.shynixn.shycommandsigns.ShyCommandSignsDependencyInjectionModule
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.github.shynixn.shycommandsigns.impl.commandexecutor.ShyCommandSignCommandExecutor
import com.github.shynixn.shycommandsigns.impl.listener.ShyCommandSignListener
import com.github.shynixn.shyguild.contract.GuildMetaSqlRepository
import com.github.shynixn.shyguild.contract.GuildService
import com.github.shynixn.shyguild.contract.ShyGuildLanguage
import com.github.shynixn.shyguild.entity.GuildTemplate
import com.github.shynixn.shyguild.entity.ShyGuildSettings
import com.github.shynixn.shyparticles.ShyParticlesDependencyInjectionModule
import com.github.shynixn.shyparticles.contract.ParticleEffectService
import com.github.shynixn.shyparticles.contract.ShyParticlesLanguage
import com.github.shynixn.shyparticles.entity.ShyParticlesSettings
import com.github.shynixn.shyparticles.impl.commandexecutor.ShyParticlesCommandExecutor
import com.github.shynixn.shyparticles.impl.listener.ShyParticlesListener
import com.github.shynixn.shyscoreboard.ShyScoreboardDependencyInjectionModule
import com.github.shynixn.shyscoreboard.contract.ScoreboardService
import com.github.shynixn.shyscoreboard.contract.ShyScoreboardLanguage
import com.github.shynixn.shyscoreboard.entity.ShyScoreboardSettings
import com.github.shynixn.shyscoreboard.impl.commandexecutor.ShyScoreboardCommandExecutor
import com.github.shynixn.shyscoreboard.impl.listener.ShyScoreboardListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import kotlin.coroutines.CoroutineContext

/**
 * Plugin Main.
 * @author Shynixn
 */
class BlockBallPlugin : JavaPlugin(), CoroutineHandler {
    private val prefix: String = ChatColor.BLUE.toString() + "[BlockBall] "
    private var module: DependencyInjectionModule? = null
    private var scoreboardModule: DependencyInjectionModule? = null
    private var bossBarModule: DependencyInjectionModule? = null
    private var signModule: DependencyInjectionModule? = null
    private var particlesModule: DependencyInjectionModule? = null
    private var guildModule: DependencyInjectionModule? = null
    private var immediateDisable = false

    companion object {
        val playerDataKey = "playerData"
        var leaderBoardKey = "leaderBoard"
        var indexKey = "[index]"
        var gameKey = "[game]"
        val patreonOnly = "PatreonOnly"
    }

    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Loading BlockBall ...")
        this.saveDefaultConfig()
        commonServer = Bukkit.getServer()
        val versions = if (BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            arrayOf(
                Version.VERSION_1_8_R3,
                Version.VERSION_1_9_R2,
                Version.VERSION_1_10_R1,
                Version.VERSION_1_11_R1,
                Version.VERSION_1_12_R1,
                Version.VERSION_1_13_R2,
                Version.VERSION_1_14_R1,
                Version.VERSION_1_15_R1,
                Version.VERSION_1_16_R1,
                Version.VERSION_1_16_R2,
                Version.VERSION_1_16_R3,
                Version.VERSION_1_17_R1,
                Version.VERSION_1_18_R1,
                Version.VERSION_1_18_R2,
                Version.VERSION_1_19_R1,
                Version.VERSION_1_19_R2,
                Version.VERSION_1_19_R3,
                Version.VERSION_1_20_R1,
                Version.VERSION_1_20_R2,
                Version.VERSION_1_20_R3,
                Version.VERSION_1_20_R4,
                Version.VERSION_1_21_R1,
                Version.VERSION_1_21_R2,
                Version.VERSION_1_21_R3,
                Version.VERSION_1_21_R4,
                Version.VERSION_1_21_R5,
                Version.VERSION_1_21_R6,
                Version.VERSION_1_21_R7,
                Version.VERSION_26_R1,
            )
        } else {
            arrayOf(
                Version.VERSION_26_R1,
            )
        }

        if (!Version.serverVersion.isCompatible(*versions)) {
            immediateDisable = true
            logger.log(Level.SEVERE, "================================================")
            logger.log(Level.SEVERE, "BlockBall does not support your server version")
            logger.log(Level.SEVERE, "Install v" + versions[0].from + " - v" + versions[versions.size - 1].to)
            logger.log(Level.SEVERE, "Need support for a particular version? Go to https://www.patreon.com/Shynixn")
            logger.log(Level.SEVERE, "Plugin gets now disabled!")
            logger.log(Level.SEVERE, "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        logger.log(Level.INFO, "Loaded NMS version ${Version.serverVersion}.")

        if (mcCoroutineConfiguration.isFoliaLoaded && !checkIfFoliaIsLoadable()) {
            logger.log(Level.SEVERE, "================================================")
            logger.log(Level.SEVERE, "BlockBall for Folia requires BlockBall-Premium-Folia.jar")
            logger.log(Level.SEVERE, "Go to https://www.patreon.com/Shynixn to download it.")
            logger.log(Level.SEVERE, "Plugin gets now disabled!")
            logger.log(Level.SEVERE, "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        if (isFoliaLoaded()) {
            logger.log(Level.INFO, "Loading Folia components.")
        }

        // Load BlockBallLanguage
        val language = BlockBallLanguageImpl()
        reloadTranslation(language)
        logger.log(Level.INFO, "Loaded language file.")

        // Module
        val placeHolderService = PlaceHolderServiceImpl(this, Bukkit.getPluginManager())
        val sqlConnectionService = CommonSqlConnectionServiceImpl(this, dataFolder.toPath().resolve("BlockBall.sqlite"))
        sqlConnectionService.connect()
        this.guildModule = loadShyGuildModule(language, placeHolderService, sqlConnectionService)
        this.scoreboardModule = loadShyScoreboardModule(language, placeHolderService)
        this.bossBarModule = loadShyBossBarModule(language, placeHolderService)
        this.signModule = loadShyCommandSignsModule(language, placeHolderService)
        this.particlesModule = loadShyParticlesModule(language, placeHolderService)
        this.module = BlockBallDependencyInjectionModule(
            this,
            language,
            placeHolderService,
            particlesModule!!,
            guildModule!!,
            sqlConnectionService
        ).build()

        // Connect to database
        try {
            val playerDataRepository = module!!.getService<PlayerDataRepository<PlayerInformation>>()
            playerDataRepository.createIfNotExist()
        } catch (e: Exception) {
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        // Register PlaceHolder
        PlaceHolder.registerAll(
            module!!.getService(), module!!.getService(), module!!.getService(), module!!.getService()
        )

        // Register Packet
        module!!.getService<PacketService>().registerPacketListening(PacketInType.USEENTITY)

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module!!.getService<GameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module!!.getService<DoubleJumpListener>(), this)
        Bukkit.getPluginManager().registerEvents(module!!.getService<HubgameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module!!.getService<MinigameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module!!.getService<BallListener>(), this)

        // Register CommandExecutor
        module!!.getService<BlockBallCommandExecutor>()

        // Service dependencies
        Bukkit.getServicesManager().register(
            SoccerBallFactory::class.java, module!!.getService<SoccerBallFactory>(), this, ServicePriority.Normal
        )
        Bukkit.getServicesManager()
            .register(GameService::class.java, module!!.getService<GameService>(), this, ServicePriority.Normal)
        val plugin = this
        plugin.launch {
            // Load Games
            val gameService = module!!.getService<GameService>()
            try {
                gameService.reloadAll()
            } catch (e: SoccerGameException) {
                plugin.logger.log(Level.WARNING, " Cannot start game of soccerArena ${e.arena.name}.", e)
            }

            // Enable stats
            module!!.getService<StatsService>().register()
            val playerDataRepository = module!!.getService<PlayerDataRepository<PlayerInformation>>()
            val guildService = guildModule!!.getService<GuildService>()
            for (player in Bukkit.getOnlinePlayers()) {
                playerDataRepository.getByPlayer(player)
                guildService.getGuilds(player)
            }

            Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Enabled BlockBall " + plugin.description.version + " by Shynixn")
        }
    }

    override fun execute(
        coroutineContext: CoroutineContext,
        f: suspend () -> Unit
    ): Job {
        return launch(coroutineContext) {
            f.invoke()
        }
    }

    override fun execute(f: suspend () -> Unit): Job {
        return launch {
            f.invoke()
        }
    }

    override fun fetchEntityDispatcher(entity: Entity): CoroutineContext {
        return entityDispatcher(entity)
    }

    override fun fetchGlobalRegionDispatcher(): CoroutineContext {
        return globalRegionDispatcher
    }

    override fun fetchLocationDispatcher(location: Location): CoroutineContext {
        return regionDispatcher(location)
    }

    /**
     * Override on disable.
     */
    override fun onDisable() {
        if (immediateDisable) {
            return
        }
        val playerDataRepository = module?.getService<CachePlayerRepository<PlayerInformation>>()
        runBlocking {
            playerDataRepository?.saveAll()
            playerDataRepository?.clearAll()
            playerDataRepository?.close()
        }

        module?.close()
        scoreboardModule?.close()
        bossBarModule?.close()
        signModule?.close()
        particlesModule?.close()
    }

    private fun loadShyBossBarModule(
        language: ShyBossBarLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyBossBarSettings({ s ->
            s.addPermission = "blockball.shybossbar.add"
            s.baseCommand = "blockballbossbar"
            s.checkForChangeChangeSeconds = config.getInt("bossbar.checkForChangeChangeSeconds")
            s.commandAliases = config.getStringList("commands.blockballbossbar.aliases")
            s.commandPermission = "blockball.shybossbar.command"
            s.defaultBossBars = listOf(
                "bossbar/blockball_bossbar.yml" to "blockball_bossbar.yml"
            )
            s.dynBossBarPermission = "blockball.shybossbar.bossbar."
            s.joinDelaySeconds = config.getInt("bossbar.joinDelaySeconds")
            s.reloadPermission = "blockball.shybossbar.reload"
            s.removePermission = "blockball.shybossbar.remove"
            s.setPermission = "blockball.shybossbar.set"
            s.updatePermission = "blockball.shybossbar.update"
            s.worldGuardFlag = "blockballshybossbar"
        })
        settings.reload()
        val module = ShyBossBarDependencyInjectionModule(
            this,
            settings,
            language,
            WorldGuardServiceImpl(this),
            placeHolderService
        ).build()

        // Register PlaceHolders
        com.github.shynixn.shybossbar.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService,
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyBossBarListener>(), this)

        // Register CommandExecutor
        module.getService<ShyBossBarCommandExecutor>()
        val bossBarService = module.getService<BossBarService>()
        launch {
            bossBarService.reload()
        }

        return module
    }

    private fun loadShyScoreboardModule(
        language: ShyScoreboardLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyScoreboardSettings({ s ->
            s.addPermission = "blockball.shyscoreboard.add"
            s.baseCommand = "blockballscoreboard"
            s.checkForChangeChangeSeconds = config.getInt("scoreboard.checkForChangeChangeSeconds")
            s.commandAliases = config.getStringList("commands.blockballscoreboard.aliases")
            s.commandPermission = "blockball.shyscoreboard.command"
            s.defaultScoreboards = listOf(
                "scoreboard/blockball_scoreboard.yml" to "blockball_scoreboard.yml"
            )
            s.dynScoreboardPermission = "blockball.shyscoreboard.scoreboard."
            s.joinDelaySeconds = config.getInt("scoreboard.joinDelaySeconds")
            s.reloadPermission = "blockball.shyscoreboard.reload"
            s.removePermission = "blockball.shyscoreboard.remove"
            s.setPermission = "blockball.shyscoreboard.set"
            s.updatePermission = "blockball.shyscoreboard.update"
            s.worldGuardFlag = "blockballscoreboard"
        })
        settings.reload()
        val module =
            ShyScoreboardDependencyInjectionModule(
                this,
                settings,
                language,
                WorldGuardServiceImpl(this),
                placeHolderService
            ).build()

        // Register PlaceHolders
        com.github.shynixn.shyscoreboard.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyScoreboardListener>(), this)

        // Register CommandExecutor
        module.getService<ShyScoreboardCommandExecutor>()
        val scoreboardService = module.getService<ScoreboardService>()
        launch {
            scoreboardService.reload()
        }

        return module
    }

    private fun loadShyCommandSignsModule(
        language: ShyCommandSignsLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyCommandSignSettings({ s ->
            s.addPermission = "blockball.shycommandsigns.add"
            s.baseCommand = "blockballsign"
            s.commandAliases = config.getStringList("commands.blockballsign.aliases")
            s.commandPermission = "blockball.shycommandsigns.command"
            s.coolDownTicks = config.getInt("sign.clickCooldownTicks")
            s.defaultSigns = listOf(
                "sign/blockball_join_sign.yml" to "blockball_join_sign.yml",
                "sign/blockball_join_red_sign.yml" to "blockball_join_red_sign.yml",
                "sign/blockball_join_blue_sign.yml" to "blockball_join_blue_sign.yml",
                "sign/blockball_leave_sign.yml" to "blockball_leave_sign.yml"
            )
            s.otherPlayerPermission = "blockball.shycommandsigns.other"
            s.reloadPermission = "blockball.shycommandsigns.reload"
            s.serverPermission = "blockball.shycommandsigns.server"
        })
        settings.reload()
        val module =
            ShyCommandSignsDependencyInjectionModule(
                this,
                settings,
                language,
                placeHolderService
            ).build()

        // Register PlaceHolders
        com.github.shynixn.shycommandsigns.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyCommandSignListener>(), this)

        // Register CommandExecutor
        module.getService<ShyCommandSignCommandExecutor>()
        val signService = module.getService<ShyCommandSignService>()
        launch {
            signService.reload()
        }
        return module
    }

    private fun loadShyParticlesModule(
        language: ShyParticlesLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyParticlesSettings({ s ->
            s.baseCommand = "blockballparticles"
            s.commandAliases = config.getStringList("commands.blockballparticles.aliases")
            s.commandPermission = "blockball.shyparticles.command"
            s.reloadPermission = "blockball.shyparticles.reload"
            s.listPermission = "blockball.shyparticles.list"
            s.playPermission = "blockball.shyparticles.play"
            s.stopPermission = "blockball.shyparticles.stop"
            s.followPermission = "blockball.shyparticles.follow"
            s.followOtherPermission = "blockball.shyparticles.followother"
            s.stopFollowPermission = "blockball.shyparticles.stopfollow"
            s.stopFollowOtherPermission = "blockball.shyparticles.stopfollowother"
            s.effectStartPermission = "blockball.shyparticles.effect.start."
            s.effectVisiblePermission = "blockball.shyparticles.effect.visible."
            s.defaultParticles = listOf(
                "effects/blue_sphere.yml" to "blue_sphere.yml",
                "effects/yellow_star.yml" to "yellow_star.yml",
                "effects/box_tower.yml" to "box_tower.yml",
                "effects/rainbow_spiral.yml" to "rainbow_spiral.yml",
                "effects/pulsing_heart.yml" to "pulsing_heart.yml",
                "effects/double_jump.yml" to "double_jump.yml",
                "effects/ball_kick.yml" to "ball_kick.yml"
            )
        })
        settings.reload()
        val module =
            ShyParticlesDependencyInjectionModule(
                this,
                settings,
                language,
                placeHolderService
            ).build()

        // Register PlaceHolders
        com.github.shynixn.shyparticles.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService
        )

        // Register Listener
        Bukkit.getPluginManager().registerEvents(module.getService<ShyParticlesListener>(), this)

        // Register CommandExecutor
        module.getService<ShyParticlesCommandExecutor>()
        val effectService = module.getService<ParticleEffectService>()
        launch {
            effectService.reload()
        }
        return module
    }

    private fun loadShyGuildModule(
        language: ShyGuildLanguage,
        placeHolderService: PlaceHolderService,
        sqlConnectionService: SqlConnectionService
    ): DependencyInjectionModule {
        val settings = ShyGuildSettings { settings ->
            settings.baseCommand = "blockballclub"
            settings.commandAliases = config.getStringList("commands.blockballclub.aliases")
            settings.guildArgument = "club"
            settings.maxJoinGuildsPerPlayer = 1
            settings.maxJoinGuildsPerPlayer = 3
            settings.maxCreateGuildsPerPlayer = config.getInt("club.maxCreateClubsPerPlayer")
            settings.maxJoinGuildsPerPlayer = config.getInt("club.maxJoinClubsPerPlayer")
            settings.joinDelaySeconds = config.getInt("club.joinDelaySeconds")
            settings.synchronizeGuildsSeconds = config.getInt("club.synchronizeClubsSeconds")
            settings.blackList = config.getStringList("club.blackList")
            settings.guildMaxInvites = config.getInt("club.clubMaxInvites")
            settings.guildNameMinLength = config.getInt("club.clubNameMinLength")
            settings.guildNameMaxLength = config.getInt("club.clubNameMaxLength")
            settings.guildDisplayNameMinLength = config.getInt("club.clubDisplayNameMinLength")
            settings.guildDisplayNameMaxLength = config.getInt("club.clubDisplayNameMaxLength")
            settings.defaultTemplates = listOf(
                "club/blockball_club.yml" to "blockball_club.yml"
            )
            settings.commandPermission = "blockball.shyguild.command"
            settings.createCmdPermission = "blockball.shyguild.cmd.create"
            settings.templateUsePermission = "blockball.shyguild.template.<template>"
            settings.deleteCmdPermission = "blockball.shyguild.cmd.delete"
            settings.guildDeletePermission = "blockball.shyguild.guild.<guild>.delete"
            settings.reloadCmdPermission = "blockball.shyguild.cmd.reload"
            settings.templateListCmdPermission = "blockball.shyguild.cmd.template.list"
            settings.addRoleCmdPermission = "blockball.shyguild.cmd.role.add"
            settings.guildAddRolePermission = "blockball.shyguild.guild.<guild>.role.add.<role>"
            settings.removeRoleCmdPermission = "blockball.shyguild.cmd.role.remove"
            settings.guildRemoveRolePermission = "blockball.shyguild.guild.<guild>.role.remove.<role>"
            settings.listRoleCmdPermission = "blockball.shyguild.cmd.role.list"
            settings.guildListRolePermission = "blockball.shyguild.guild.<guild>.role.list"
            settings.addMemberPermission = "blockball.shyguild.cmd.member.add"
            settings.guildMemberAddPermission = "blockball.shyguild.guild.<guild>.member.add"
            settings.removeMemberPermission = "blockball.shyguild.cmd.member.remove"
            settings.guildMemberRemovePermission = "blockball.shyguild.guild.<guild>.member.remove"
            settings.listMembersPermission = "blockball.shyguild.cmd.member.list"
            settings.guildMemberListPermission = "blockball.shyguild.guild.<guild>.member.list"
            settings.inviteMemberPermission = "blockball.shyguild.cmd.member.invite"
            settings.guildMemberInvitePermission = "blockball.shyguild.guild.<guild>.member.invite"
            settings.acceptMemberPermission = "blockball.shyguild.cmd.member.accept"
            settings.leaveMemberPermission = "blockball.shyguild.cmd.member.leave"
            settings.guildMemberLeavePermission = "blockball.shyguild.guild.<guild>.member.leave"
            settings.guildListPermission = "blockball.shyguild.cmd.list"

        }
        settings.reload()
        val module =
            com.github.shynixn.shyguild.ShyGuildDependencyInjectionModule(
                this,
                this,
                settings,
                language,
                placeHolderService,
                sqlConnectionService
            ).build()
        val guildService = module.getService<GuildService>()

        // Register PlaceHolders
        com.github.shynixn.shyguild.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService,
            guildService
        )

        // Register Listener
        Bukkit.getPluginManager()
            .registerEvents(module.getService<com.github.shynixn.shyguild.impl.listener.ShyGuildListener>(), this)

        // Register CommandExecutor
        module.getService<com.github.shynixn.shyguild.impl.commandexecutor.ShyGuildCommandExecutor>()

        val templateService = module.getService<CacheRepository<GuildTemplate>>()
        val playerDataRepository =
            module.getService<PlayerDataRepository<com.github.shynixn.shyguild.entity.PlayerInformation>>()
        val guildMetaRepository = module.getService<GuildMetaSqlRepository>()
        launch {
            templateService.getAll()
            playerDataRepository.createIfNotExist()
            guildMetaRepository.createIfNotExist()
        }
        return module
    }
}
