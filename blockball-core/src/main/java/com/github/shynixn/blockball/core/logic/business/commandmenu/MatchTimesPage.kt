@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.MatchTimeMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.MatchTimeMetaEntity
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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHERwwt
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class MatchTimesPage @Inject constructor(private val proxyService: ProxyService) : Page(ID, GameSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 34
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.MATCHTIMES
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
        val arena = cache[0] as Arena

        if (command == MenuCommand.MATCHTIMES_OPEN) {
            cache[5] = null
        } else if (command == MenuCommand.MATCHTIMES_CREATE) {
            val matchTime = MatchTimeMetaEntity()
            arena.meta.minigameMeta.matchTimes.add(matchTime)
            cache[5] = matchTime
        } else if (command == MenuCommand.MATCHTIMES_CALLBACK && args.size >= 3) {
            val index = args[2].toInt()
            if (index >= 0 && index < arena.meta.minigameMeta.matchTimes.size) {
                cache[5] = arena.meta.minigameMeta.matchTimes[index]
            }
        } else if (command == MenuCommand.MATCHTIMES_DELETE) {
            arena.meta.minigameMeta.matchTimes.remove(cache[5])
            cache[5] = null
        } else if (command == MenuCommand.MATCHTIMES_DURATION && args[2].toIntOrNull() != null) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.duration = args[2].toInt()
        } else if (command == MenuCommand.MATCHTIMES_SWITCHGOAL) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.isSwitchGoalsEnabled = !matchTime.isSwitchGoalsEnabled
        } else if (command == MenuCommand.MATCHTIMES_BALLAVAILABLE) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.playAbleBall = !matchTime.playAbleBall
        } else if (command == MenuCommand.MATCHTIMES_RESPAWN) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.respawnEnabled = !matchTime.respawnEnabled
        } else if (command == MenuCommand.MATCHTIMES_STARTTITLEMESSAGE && args.size >= 3) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.startMessageTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.MATCHTIMES_STARTSUBTITLEMESSAGE && args.size >= 3) {
            val matchTime = cache[5] as MatchTimeMeta
            matchTime.startMessageSubTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.MATCHTIMES_CALLBACKCLOSETYPE) {
            val index = args[2].toInt()
            val matchTime = cache[5] as MatchTimeMeta
            if (index >= 0 && index < MatchTimeCloseType.values().size) {
                matchTime.closeType = MatchTimeCloseType.values()[index]
            }
        }

        cache[2] =
            arena.meta.minigameMeta.matchTimes.mapIndexed { index, e -> (index + 1).toString() + ". ${e.closeType.name}\\nDuration: ${e.duration} seconds"}
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val arena = cache[0] as Arena
        val selectedMatchTime = cache[5]
        val matchTimeListText = (cache[2] as List<String>).toSingleLine()

        val builder = ChatBuilderEntity()
            .component("- Match Times:").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(matchTimeListText)
            .builder().component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_MATCHTIMES.command)
            .setHoverText("Opens the selectionbox for existing match times.")
            .builder().component(" [add..]").setColor(MenuClickableItem.ADD.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_CREATE.command)
            .setHoverText("Adds a new match time and selects it.")
            .builder().nextLine()

        if (selectedMatchTime != null && selectedMatchTime is MatchTimeMeta) {
            val index = (arena.meta.minigameMeta.matchTimes.indexOf(selectedMatchTime) + 1).toString()
            val selectedMatchTimeText = "Number $index"
            val selectedMatchTimeHover =
                index + ". ${selectedMatchTime.closeType.name}\\nDuration: ${selectedMatchTime.duration} seconds\\nSwitch goals: ${selectedMatchTime.isSwitchGoalsEnabled}"

            builder.component("- Selected match time: $selectedMatchTimeText").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(selectedMatchTimeHover).builder()
                .component(MenuClickableItem.DELETE.text).setColor(MenuClickableItem.DELETE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_DELETE.command)
                .setHoverText("Deletes the selected match time.")
                .builder().nextLine()
                .component("- Close Condition: " + selectedMatchTime.closeType.name).builder()
                .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_MATCHCLOSETYPES.command)
                .setHoverText("Opens the selectionbox for close conditions.")
                .builder().nextLine()
                .component("- Duration: " + selectedMatchTime.duration + " seconds").builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.MATCHTIMES_DURATION.command)
                .setHoverText("Changes the duration (seconds) of this part of the match.")
                .builder().nextLine()
                .component("- Switch goals: " + selectedMatchTime.isSwitchGoalsEnabled).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_SWITCHGOAL.command)
                .setHoverText("Toggles if the team should switch goals when this match time starts.")
                .builder().nextLine()
                .component("- Playable Ball: " + selectedMatchTime.playAbleBall).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_BALLAVAILABLE.command)
                .setHoverText("Toggles if the ball should be spawned during this match time.")
                .builder().nextLine()
                .component("- Respawn: " + selectedMatchTime.respawnEnabled).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_RESPAWN.command)
                .setHoverText("Toggles if players should respawn when this match time starts.")
                .builder().nextLine()
                .component("- StartTitle Message: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(selectedMatchTime.startMessageTitle).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.MATCHTIMES_STARTTITLEMESSAGE.command)
                .setHoverText("Changes the title message getting played when the match time starts.")
                .builder().nextLine()
                .component("- StartSubTitle Message: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(selectedMatchTime.startMessageSubTitle).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.MATCHTIMES_STARTSUBTITLEMESSAGE.command)
                .setHoverText("Changes the subtitle message getting played when the match time starts.")
                .builder()
        }

        return builder
    }
}