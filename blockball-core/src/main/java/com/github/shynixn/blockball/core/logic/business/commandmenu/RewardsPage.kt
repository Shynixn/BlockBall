package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.DependencyVaultService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.CommandMetaEntity

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
        const val ID = 22
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.REWARDSPAGE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        if (command == MenuCommand.REWARD_OPEN) {
            cache[4] = null
            cache[5] = null
        } else if (command == MenuCommand.REWARD_CALLBACK_MONEY && args.size >= 3 && args[2].toIntOrNull() != null) {
            val rewardedAction = RewardType.values()[args[2].toInt()]
            cache[5] = rewardedAction
            if (arena.meta.rewardMeta.moneyReward[rewardedAction] == null) {
                arena.meta.rewardMeta.moneyReward[rewardedAction] = 0
            }
            cache[4] = arena.meta.rewardMeta.moneyReward[rewardedAction]
        } else if (command == MenuCommand.REWARD_CALLBACK_COMMAND && args.size >= 3 && args[2].toIntOrNull() != null) {
            val rewardedAction = RewardType.values()[args[2].toInt()]
            cache[5] = rewardedAction
            if (arena.meta.rewardMeta.commandReward[rewardedAction] == null) {
                val command2 = CommandMetaEntity()
                command2.command = "none"
                command2.mode = CommandMode.CONSOLE_SINGLE
                arena.meta.rewardMeta.commandReward[rewardedAction] = command2
            }
            cache[4] = arena.meta.rewardMeta.commandReward[rewardedAction]
        } else if (command == MenuCommand.REWARD_CALLBACK_COMMANDMODE && args.size >= 3 && args[2].toIntOrNull() != null) {
            val selectedReward = cache[4] as CommandMeta
            selectedReward.mode = CommandMode.values()[args[2].toInt()]
        } else if (command == MenuCommand.REWARD_EDIT_MONEY && args.size >= 3 && args[2].toIntOrNull() != null) {
            arena.meta.rewardMeta.moneyReward[cache[5] as RewardType] = args[2].toInt()
            cache[4] = args[2].toInt()
        } else if (command == MenuCommand.REWARD_EDIT_COMMAND && args.size >= 3) {
            arena.meta.rewardMeta.commandReward[cache[5] as RewardType]!!.command = mergeArgs(2, args)
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
        val builder = ChatBuilderEntity()

        try {
            BlockBallApi.resolve(DependencyVaultService::class.java)

            builder.component("- Money reward (Vault): ").builder()
                .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_REWARDED_MONEY.command)
                .setHoverText("Opens the selectionbox for rewarded actions.")
                .builder().nextLine()
        } catch (e: Exception) {
        }

        builder.component("- Command reward: ").builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_REWARDED_COMMAND.command)
            .setHoverText("Opens the selectionbox for rewarded actions.")
            .builder().nextLine()

        if (selectedReward != null) {
            if (selectedReward is Int) {
                val vaultService: DependencyVaultService

                try {
                    vaultService = BlockBallApi.resolve(DependencyVaultService::class.java)
                } catch (e: Exception) {
                    return builder
                }

                builder.component("- Selected Money reward (Vault): " + (rewardedAction as RewardType).name).builder().nextLine()
                    .component("- " + vaultService.getPluralCurrencyName() + ": " + ChatColor.WHITE + selectedReward).builder()
                    .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                    .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.REWARD_EDIT_MONEY.command)
                    .setHoverText("Changes the amount of money the players receive on the selected action.")
                    .builder()
            } else if (selectedReward is CommandMeta) {
                builder.component("- Selected Command reward: " + (rewardedAction as RewardType).name).builder().nextLine()
                    .component("- Command: " + selectedReward.command).builder()
                    .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                    .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.REWARD_EDIT_COMMAND.command)
                    .setHoverText("Changes the amount of money the players receive on the selected action.")
                    .builder().nextLine()
                    .component("- Mode: " + selectedReward.mode.name).builder()
                    .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
                    .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_COMMANDMODES.command)
                    .setHoverText("Opens the selectionbox for command modes.")
                    .builder().nextLine()//
            }
        } else {
            builder.component("- Selected reward: none")
        }
        return builder
    }
}