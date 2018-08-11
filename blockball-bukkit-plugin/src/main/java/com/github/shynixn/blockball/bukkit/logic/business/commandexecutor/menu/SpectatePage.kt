package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.bukkit.logic.business.extension.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toSingleLine
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
class SpectatePage : Page(ID, SpectatePage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 120
    }


    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.SPECTATE
    }


    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as Arena

        if (command == BlockBallCommand.SPECTATE_TOGGLE) {
            arena.meta.spectatorMeta.spectatorModeEnabled = !arena.meta.spectatorMeta.spectatorModeEnabled
        } else if (command == BlockBallCommand.SPECTATE_SPAWNPOINT) {
            arena.meta.spectatorMeta.spectateSpawnpoint = player.location.toPosition()
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
        val arena = cache[0] as Arena
        val meta = arena.meta.spectatorMeta

        var spectatorSpawnpoint = "none"
        if (meta.spectateSpawnpoint != null) {
            spectatorSpawnpoint = printLocation(meta.spectateSpawnpoint!!)
        }

        return ChatBuilder()
                .component("- Spectator mode enabled: " + meta.spectatorModeEnabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SPECTATE_TOGGLE.command)
                .setHoverText("Toggles the spectator mode to spectate games.")
                .builder().nextLine()
                .component("- Spectate Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color).setHoverText(meta.spectateStartMessage.toSingleLine()).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_SPECTATEJOINMESSAGE.command)
                .setHoverText("Message being send to players who try to join a full game.")
                .builder().nextLine()
                .component("- Spectator Spawnpoint: $spectatorSpawnpoint").builder()
                .component(ClickableComponent.LOCATION.text).setColor(ClickableComponent.LOCATION.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SPECTATE_SPAWNPOINT.command)
                .setHoverText("Sets the spawnpoint for people who spectate the game.")
                .builder().nextLine()
    }
}