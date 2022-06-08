package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.business.service.TemplateService
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.google.inject.Inject

/**
 * [Page] to display values as a selection box for players to choose from.
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
class ListablePage @Inject constructor(
    private val templateService: TemplateService,
    private val proxyService: ProxyService,
    private val soundService: SoundService
) : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {
    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.LISTABLE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(
        player: P,
        command: MenuCommand,
        cache: Array<Any?>,
        args: Array<String>
    ): MenuCommandResult {
        when (command) {
            MenuCommand.LIST_GAMETYPES -> {
                cache[2] = GameType.values().map { p -> p.name }
                cache[3] = MenuCommand.SETTINGS_OPEN
            }
            MenuCommand.LIST_PARTICLE_EFFECTINGTYPES -> {
                cache[2] = EffectTargetType.values().map { p -> p.name }
                cache[3] = MenuCommand.PARTICLE_CALLBACK_EFFECTING
            }
            MenuCommand.LIST_TEMPLATES -> {
                cache[2] = templateService.getAvailableTemplates().map { p -> p.name }
                cache[3] = MenuCommand.TEMPLATE_SELECT_CALLBACK
            }
            MenuCommand.LIST_BUKKITGAMESMODES -> {
                cache[2] = proxyService.gameModes.filterNot { g -> g == "SPECTATOR" }
                cache[3] = MenuCommand.GAMESETTINGS_CALLBACK_BUKKITGAMEMODES
            }
            MenuCommand.LIST_BALL_PARTICLEFFECTS -> {
                cache[2] = BallActionType.values().map { p -> p.name }
                cache[3] = MenuCommand.BALL_PARTICLEACTION_CALLBACK
            }
            MenuCommand.LIST_BALL_SOUNDEFFECTS -> {
                cache[2] = BallActionType.values().map { p -> p.name }
                cache[3] = MenuCommand.BALL_SOUNDACTION_CALLBACK
            }
            MenuCommand.LIST_BALLSIZES -> {
                cache[2] = BallSize.values().map { p -> p.name }
                cache[3] = MenuCommand.BALL_SIZE_CALLBACK
            }
            MenuCommand.LIST_COMMANDMODES -> {
                cache[2] = CommandMode.values().map { p -> p.name }
                cache[3] = MenuCommand.REWARD_CALLBACK_COMMANDMODE
            }
            MenuCommand.LIST_PARTICLE_TYPES -> {
                cache[2] = ParticleType.values().map { p -> p.name }
                cache[3] = MenuCommand.PARTICLE_CALLBACK_TYPE
            }
            MenuCommand.LIST_REWARDED_MONEY -> {
                cache[2] = RewardType.values().map { p -> p.name }
                cache[3] = MenuCommand.REWARD_CALLBACK_MONEY
            }
            MenuCommand.LIST_REWARDED_COMMAND -> {
                cache[2] = RewardType.values().map { p -> p.name }
                cache[3] = MenuCommand.REWARD_CALLBACK_COMMAND
            }
            MenuCommand.LIST_SOUND_TYPES -> {
                cache[2] = soundService.soundNames
                cache[3] = MenuCommand.SOUND_CALLBACK_TYPE
            }
            MenuCommand.LIST_SOUND_EFFECTINGTYPES -> {
                cache[2] = EffectTargetType.values().map { p -> p.name }
                cache[3] = MenuCommand.SOUND_CALLBACK_EFFECTING
            }
            MenuCommand.LIST_LINES -> cache[3] = MenuCommand.MULTILINES_ANY
            MenuCommand.LIST_BOSSBARSTYLES -> {
                cache[2] = BossbarStyle.values().map { p -> p.name }
                cache[3] = MenuCommand.BOSSBAR_OPEN
            }
            MenuCommand.LIST_BOSSBARFLAGS -> {
                cache[2] = BossBarFlag.values().map { p -> p.name }
                cache[3] = MenuCommand.BOSSBAR_CALLBACKFLAGS
            }
            MenuCommand.LIST_BOSSBARCOLORS -> {
                cache[2] = BossbarColor.values().map { p -> p.name }
                cache[3] = MenuCommand.BOSSBAR_CALLBACKCOLORS
            }
            MenuCommand.LIST_MATCHCLOSETYPES -> {
                cache[2] = MatchTimeCloseType.values().map { p -> p.name }
                cache[3] = MenuCommand.MATCHTIMES_CALLBACKCLOSETYPE
            }
            MenuCommand.LIST_HOLOGRAMS -> cache[3] = MenuCommand.HOLOGRAM_CALLBACK
            MenuCommand.LIST_MATCHTIMES -> cache[3] = MenuCommand.MATCHTIMES_CALLBACK
            else -> {
            }
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val infoList = cache[2] as ArrayList<*>
        val callBackCommand = cache[3] as MenuCommand
        val builder = ChatBuilderEntity()

        if (infoList.size == 0) {
            builder.text("No data found.")
        } else {
            infoList.forEachIndexed { index, p ->
                builder.component((index + 1).toString() + ": [$p]")
                    .setClickAction(ChatClickAction.RUN_COMMAND, callBackCommand.command + index)
                    .setHoverText("").builder().nextLine().nextLine()
            }
        }
        return builder
    }
}
