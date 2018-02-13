package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta
import com.github.shynixn.ball.api.persistence.enumeration.EffectingType
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.RewardMeta
import com.github.shynixn.blockball.bukkit.dependencies.RegisterHelper
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc.CommandProperties
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

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
class RewardsPage : Page(SoundEffectPage.ID, MainSettingsPage.ID) {
    companion object {
        /** Id of the page. */
        val ID = 22
    }

    @Inject
    private var arenaRepository: ArenaRepository? = null

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.REWARDSPAGE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as BukkitArena
        if (command == BlockBallCommand.REWARD_OPEN) {
            cache[4] = null
            cache[5] = null
        } else if (command == BlockBallCommand.REWARD_CALLBACK_MONEY && args.size >= 3 && args[2].toIntOrNull() != null) {
            val rewardedAction = RewardMeta.RewardedAction.values()[args[2].toInt()]
            cache[5] = rewardedAction
            if (arena.meta.rewardMeta.moneyReward[rewardedAction] == null) {
                arena.meta.rewardMeta.moneyReward[rewardedAction] = 0
            }
            cache[4] = arena.meta.rewardMeta.moneyReward[rewardedAction]
        } else if (command == BlockBallCommand.REWARD_CALLBACK_COMMAND && args.size >= 3 && args[2].toIntOrNull() != null) {
            val rewardedAction = RewardMeta.RewardedAction.values()[args[2].toInt()]
            cache[5] = rewardedAction
            if (arena.meta.rewardMeta.commandReward[rewardedAction] == null) {
                val command =  CommandProperties();
                command.command = "none"
                command.mode = CommandMeta.CommandMode.CONSOLE_SINGLE
                arena.meta.rewardMeta.commandReward[rewardedAction] = command
            }
            cache[4] = arena.meta.rewardMeta.commandReward[rewardedAction]
        } else if (command == BlockBallCommand.REWARD_CALLBACK_COMMANDMODE && args.size >= 3 && args[2].toIntOrNull() != null) {
            val selectedReward = cache[4] as CommandMeta
            selectedReward.mode = CommandMeta.CommandMode.values()[args[2].toInt()]
        } else if (command == BlockBallCommand.REWARD_EDIT_MONEY && args.size >= 3 && args[2].toIntOrNull() != null) {
            arena.meta.rewardMeta.moneyReward[cache[5] as RewardMeta.RewardedAction] = args[2].toInt()
            cache[4] = args[2].toInt()
        } else if (command == BlockBallCommand.REWARD_EDIT_COMMAND && args.size >= 3) {
            arena.meta.rewardMeta.commandReward[cache[5] as RewardMeta.RewardedAction]!!.command = mergeArgs(2,args)
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val selectedReward = cache[4]
        val rewardedAction = cache[5]
        val builder = ChatBuilder()
                .component("- Money reward (Vault): ").builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_REWARDED_MONEY.command)
                .setHoverText("Opens the selectionbox for rewarded actions.")
                .builder().nextLine()
                .component("- Command reward: ").builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_REWARDED_COMMAND.command)
                .setHoverText("Opens the selectionbox for rewarded actions.")
                .builder().nextLine()
        if (selectedReward != null) {
            if (selectedReward is Int) {
                builder.component("- Selected Money reward (Vault): " + (rewardedAction as RewardMeta.RewardedAction).name).builder().nextLine()
                        .component("- " + RegisterHelper.getCurrencyName() + ": " + ChatColor.WHITE + selectedReward).builder()
                        .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                        .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.REWARD_EDIT_MONEY.command)
                        .setHoverText("Changes the amount of money the players receive on the selected action.")
                        .builder()
            } else if (selectedReward is CommandMeta) {
                builder.component("- Selected Command reward: " + (rewardedAction as RewardMeta.RewardedAction).name).builder().nextLine()
                        .component("- Command: " + selectedReward.command).builder()
                        .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                        .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.REWARD_EDIT_COMMAND.command)
                        .setHoverText("Changes the amount of money the players receive on the selected action.")
                        .builder().nextLine()
                        .component("- Mode: " + selectedReward.mode.name).builder()
                        .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_COMMANDMODES.command)
                        .setHoverText("Opens the selectionbox for command modes.")
                        .builder().nextLine()//
            }
        } else {
            builder.component("- Selected reward: none")
        }
        return builder
    }
}