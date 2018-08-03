package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta
import com.github.shynixn.ball.api.persistence.enumeration.ActionEffect
import com.github.shynixn.ball.api.persistence.enumeration.BallSize
import com.github.shynixn.ball.api.persistence.enumeration.EffectingType
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.service.TemplateService
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.RewardMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ChatBuilder
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player

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
class ListablePage : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {

    @Inject
    private lateinit var templateService: TemplateService

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.LISTABLE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        when (command) {
            BlockBallCommand.LIST_GAMETYPES -> {
                cache[2] = GameType.values().map { p -> p.name }
                cache[3] = BlockBallCommand.SETTINGS_OPEN
            }
            BlockBallCommand.LIST_PARTICLE_EFFECTINGTYPES -> {
                cache[2] = EffectingType.values().map { p -> p.name }
                cache[3] = BlockBallCommand.PARTICLE_CALLBACK_EFFECTING
            }
            BlockBallCommand.LIST_TEMPLATES -> {
                cache[2] = templateService.getAvailableTemplates().map { p -> p.name }
                cache[3] = BlockBallCommand.TEMPLATE_SELECT_CALLBACK
            }
            BlockBallCommand.LIST_BUKKITGAMESMODES -> {
                cache[2] = GameMode.values().filterNot { g -> g == GameMode.SPECTATOR }.map { p -> p.name }
                cache[3] = BlockBallCommand.GAMESETTINGS_CALLBACK_BUKKITGAMEMODES
            }
            BlockBallCommand.LIST_BALL_PARTICLEFFECTS -> {
                cache[2] = ActionEffect.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BALL_PARTICLEACTION_CALLBACK
            }
            BlockBallCommand.LIST_BALL_SOUNDEFFECTS -> {
                cache[2] = ActionEffect.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BALL_SOUNDACTION_CALLBACK
            }
            BlockBallCommand.LIST_BALLSIZES -> {
                cache[2] = BallSize.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BALL_SIZE_CALLBACK
            }
            BlockBallCommand.LIST_COMMANDMODES -> {
                cache[2] = CommandMeta.CommandMode.values().map { p -> p.name }
                cache[3] = BlockBallCommand.REWARD_CALLBACK_COMMANDMODE
            }
            BlockBallCommand.LIST_PARTICLE_TYPES -> {
                cache[2] = ParticleEffectMeta.ParticleEffectType.values().map { p -> p.name }
                cache[3] = BlockBallCommand.PARTICLE_CALLBACK_TYPE
            }
            BlockBallCommand.LIST_REWARDED_MONEY -> {
                cache[2] = RewardMeta.RewardedAction.values().map { p -> p.name }
                cache[3] = BlockBallCommand.REWARD_CALLBACK_MONEY
            }
            BlockBallCommand.LIST_REWARDED_COMMAND -> {
                cache[2] = RewardMeta.RewardedAction.values().map { p -> p.name }
                cache[3] = BlockBallCommand.REWARD_CALLBACK_COMMAND
            }
            BlockBallCommand.LIST_SOUND_TYPES -> {
                cache[2] = Sound.values().map { p -> p.name }
                cache[3] = BlockBallCommand.SOUND_CALLBACK_TYPE
            }
            BlockBallCommand.LIST_SOUND_EFFECTINGTYPES -> {
                cache[2] = EffectingType.values().map { p -> p.name }
                cache[3] = BlockBallCommand.SOUND_CALLBACK_EFFECTING
            }
            BlockBallCommand.LIST_LINES -> cache[3] = BlockBallCommand.MULTILINES_ANY
            BlockBallCommand.LIST_BOSSBARSTYLES -> {
                cache[2] = BossBarMeta.Style.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BOSSBAR_OPEN
            }
            BlockBallCommand.LIST_BOSSBARFLAGS -> {
                cache[2] = BossBarMeta.Flag.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BOSSBAR_CALLBACKFLAGS
            }
            BlockBallCommand.LIST_BOSSBARCOLORS -> {
                cache[2] = BossBarMeta.Color.values().map { p -> p.name }
                cache[3] = BlockBallCommand.BOSSBAR_CALLBACKCOLORS
            }
            BlockBallCommand.LIST_HOLOGRAMS -> cache[3] = BlockBallCommand.HOLOGRAM_CALLBACK
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
        val callBackCommand = cache[3] as BlockBallCommand
        val builder = ChatBuilder()
        if (infoList.size == 0) {
            builder.text("No data found.")
        } else {
            infoList.forEachIndexed { index, p ->
                builder.component((index + 1).toString() + ": [$p]")
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, callBackCommand.command + " " + index)
                        .setHoverText("").builder().nextLine()
            }

        }
        return builder
    }
}