package com.github.shynixn.blockball.core.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.enumeration.ChatClickAction
import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.enumeration.MenuCommand
import com.github.shynixn.blockball.api.business.enumeration.MenuCommandResult
import com.github.shynixn.blockball.api.business.executor.CommandExecutor
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.core.logic.business.commandmenu.*
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.google.inject.Inject

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
class ArenaCommandExecutor @Inject constructor(
    private val configurationService: ConfigurationService,
    private val proxyService: ProxyService,
    private val loggingService: LoggingService,
    openPage: OpenPage,
    mainConfigurationPage: MainConfigurationPage,
    spectatePage: SpectatePage,
    mainSettingsPage: MainSettingsPage,
    ballSettingsPage: BallSettingsPage,
    ballModifierPage: BallModifierSettingsPage,
    listablePage: ListablePage,
    teamSettingsPage: TeamSettingsPage,
    effectsSettingsPage: EffectsSettingsPage,
    multipleLinesPage: MultipleLinesPage,
    hologramsPage: HologramPage,
    scoreboardPage: ScoreboardPage,
    bossbarPage: BossbarPage,
    templatePage: TemplateSettingsPage,
    signSettingsPage: SignSettingsPage,
    rewardsPage: RewardsPage,
    particlesPage: ParticleEffectPage,
    soundsPage: SoundEffectPage,
    abilitiesPage: AbilitiesSettingsPage,
    doubleJumpPage: DoubleJumpPage,
    miscPage: MiscSettingsPage,
    gamePropertiesPage: GamePropertiesPage,
    areaProtectionPage: AreaProtectionPage,
    teamTextBookPage: TeamTextBookPage,
    gameSettingsPage: GameSettingsPage,
    spectatingSettingsPage: SpectatingSettingsPage,
    notificationPage: NotificationPage,
    matchtimesPage : MatchTimesPage
) : CommandExecutor {


    companion object {
        private val HEADER_STANDARD =
            ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                          BlockBall                         "
        private val FOOTER_STANDARD =
            ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            "
    }

    private val cache = HashMap<Any, Array<Any?>>()
    private var pageCache: MutableList<Page> = arrayListOf(
        openPage, mainConfigurationPage, spectatePage, mainSettingsPage, ballSettingsPage
        , ballModifierPage, listablePage, teamSettingsPage, effectsSettingsPage, multipleLinesPage, hologramsPage, scoreboardPage, bossbarPage
        , templatePage, signSettingsPage, rewardsPage, particlesPage, soundsPage, abilitiesPage, doubleJumpPage, miscPage, gamePropertiesPage
        , areaProtectionPage, teamTextBookPage, gameSettingsPage, spectatingSettingsPage, notificationPage, matchtimesPage
    )

    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun <S> onExecuteCommand(source: S, args: Array<out String>): Boolean {
        try {
            if (source !is Any) {
                return false
            }

            for (i in 0..19) {
                proxyService.sendMessage(source, "")
            }
            proxyService.sendMessage(source, HEADER_STANDARD)
            proxyService.sendMessage(source, "\n")
            if (!this.cache.containsKey(source)) {
                val anyArray = arrayOfNulls<Any>(8)
                this.cache[source] = anyArray
            }
            val cache: Array<Any?>? = this.cache[source]
            val command = MenuCommand.from(args)
                ?: throw IllegalArgumentException("Command is not registered!")
            var usedPage: Page? = null
            for (page in this.pageCache) {
                if (page.getCommandKey() === command.key) {
                    usedPage = page
                    if (command == MenuCommand.BACK) {
                        val newPage = this.getPageById(Integer.parseInt(args[2]))
                        val b = newPage.buildPage(cache!!)!!
                        proxyService.sendMessage(source, b)
                    } else if (command == MenuCommand.CLOSE) {
                        this.cache.remove(source)
                        for (i in 0..19) {
                            proxyService.sendMessage(source, "")
                        }
                        return true
                    } else {
                        @Suppress("UNCHECKED_CAST") val result = page.execute(source, command, cache!!, args as Array<String>)
                        if (result == MenuCommandResult.BACK) {
                            proxyService.performPlayerCommand(source, "blockball open back " + usedPage.getPreviousIdFrom(cache))
                            return true
                        }
                        if (result != MenuCommandResult.SUCCESS && result != MenuCommandResult.CANCEL_MESSAGE) {
                            val b = ChatBuilderEntity()
                                .component(ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "[" + ChatColor.RED + ChatColor.BOLD + "!" + ChatColor.WHITE + ChatColor.BOLD + "] " + ChatColor.RED + "Error (Hover me)")
                                .setHoverText(result.message!!).builder()
                            proxyService.sendMessage(source, b)
                        }
                        if (result != MenuCommandResult.CANCEL_MESSAGE) {
                            val b = page.buildPage(cache)!!
                            proxyService.sendMessage(source, b)
                        }
                    }
                    break
                }
            }
            if (usedPage == null)
                throw IllegalArgumentException("Cannot find page with key " + command.key)
            val builder = ChatBuilderEntity()
                .text(ChatColor.STRIKETHROUGH.toString() + "----------------------------------------------------").nextLine()
                .component(" >>Save<< ")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SAVE.command)
                .setHoverText("Saves the current arena if possible.")
                .builder()
            if (usedPage is ListablePage) {
                builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(ChatClickAction.RUN_COMMAND, (cache!![3] as MenuCommand).command)
                    .setHoverText("Goes back to the previous page.")
                    .builder()
            } else {
                builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BACK.command + usedPage.getPreviousIdFrom(cache!!))
                    .setHoverText("Goes back to the previous page.")
                    .builder()
            }
            val b = builder.component(" >>Save and reload<<")
                .setColor(ChatColor.BLUE)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_RELOAD.command)
                .setHoverText("Saves the current arena and reloads all games on the server.")
                .builder()

            proxyService.sendMessage(source, b)
            proxyService.sendMessage(source, FOOTER_STANDARD)
        } catch (e: Exception) {
            loggingService.debug("Command completion failed.", e)
            val prefix = configurationService.findValue<String>("messages.prefix")
            proxyService.sendMessage(source, prefix + "Cannot find command.")
            val data = StringBuilder()
            args.map { d -> data.append(d).append(" ") }
            loggingService.info("Cannot find command for args $data.")
        }

        return true
    }

    /**
     * Returns the [Page] with the given [id] and throws
     * a [RuntimeException] if the page is not found.
     */
    private fun getPageById(id: Int): Page {
        for (page in this.pageCache) {
            if (page.id == id) {
                return page
            }
        }
        throw RuntimeException("Page does not exist!")
    }
}
