package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
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
class SpectatePage @Inject constructor(private val proxyService: ProxyService) : Page(ID, SpectatePage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 120
    }


    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.SPECTATE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena

        if (command == MenuCommand.SPECTATE_TOGGLE) {
            arena.meta.spectatorMeta.spectatorModeEnabled = !arena.meta.spectatorMeta.spectatorModeEnabled
        } else if (command == MenuCommand.SPECTATE_SPAWNPOINT) {
            arena.meta.spectatorMeta.spectateSpawnpoint = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
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
            spectatorSpawnpoint = meta.spectateSpawnpoint!!.toString()
        }

        return ChatBuilderEntity()
            .component("- Spectator mode enabled: " + meta.spectatorModeEnabled).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SPECTATE_TOGGLE.command)
            .setHoverText("Toggles the spectator mode to spectate games.")
            .builder().nextLine()
            .component("- Spectate Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color).setHoverText(meta.spectateStartMessage.toSingleLine())
            .builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_SPECTATEJOINMESSAGE.command)
            .setHoverText("Message being send to players who try to join a full game.")
            .builder().nextLine()
            .component("- Spectator Spawnpoint: $spectatorSpawnpoint").builder()
            .component(MenuClickableItem.LOCATION.text).setColor(MenuClickableItem.LOCATION.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SPECTATE_SPAWNPOINT.command)
            .setHoverText("Sets the spawnpoint for people who spectate the game.")
            .builder().nextLine()
    }
}