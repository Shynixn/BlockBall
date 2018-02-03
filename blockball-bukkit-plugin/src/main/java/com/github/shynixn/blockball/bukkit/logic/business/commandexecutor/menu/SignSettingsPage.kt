package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.ball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toSingleLine
import com.github.shynixn.blockball.bukkit.logic.business.listener.SignPlacementListener
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
class SignSettingsPage : Page(SignSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 11
    }

    @Inject
    private var listener: SignPlacementListener? = null;

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.SIGNS
    }


    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player?, command: BlockBallCommand?, cache: Array<Any>?, args: Array<out String>?): CommandResult {
        val arena = cache!![0] as BukkitArena
        if (command == BlockBallCommand.SIGNS_ADDTEAMRED) {
            player!!.sendMessage(Config.prefix + "Rightclick on a sign.")
            listener!!.placementCallBack[player!!] = (object : SignPlacementListener.CallBack {
                override fun run(position: IPosition) {
                    arena.meta.hubLobbyMeta.redTeamSigns.add(position)
                }
            })
        } else if (command == BlockBallCommand.SIGNS_ADDTEAMBLUE) {
            player!!.sendMessage(Config.prefix + "Rightclick on a sign.")
            listener!!.placementCallBack[player!!] = (object : SignPlacementListener.CallBack {
                override fun run(position: IPosition) {
                    arena.meta.hubLobbyMeta.blueTeamSigns.add(position)
                }
            })
        }
        else if (command == BlockBallCommand.SIGNS_ADDJOINANY) {
            player!!.sendMessage(Config.prefix + "Rightclick on a sign.")
            listener!!.placementCallBack[player!!] = (object : SignPlacementListener.CallBack {
                override fun run(position: IPosition) {
                    arena.meta.hubLobbyMeta.joinSigns.add(position)
                }
            })
        }
        else if (command == BlockBallCommand.SIGNS_LEAVE) {
            player!!.sendMessage(Config.prefix + "Rightclick on a sign.")
            listener!!.placementCallBack[player!!] = (object : SignPlacementListener.CallBack {
                override fun run(position: IPosition) {
                    arena.meta.hubLobbyMeta.leaveSigns.add(position)
                }
            })
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any>?): ChatBuilder {
        val arena = cache!![0] as BukkitArena

        val teamSignsRed = arena.meta.hubLobbyMeta.redTeamSigns.map { p -> this.printLocation(p) }
        val teamSignsBlue = arena.meta.hubLobbyMeta.blueTeamSigns.map { p -> this.printLocation(p) }
        val joinSigns = arena.meta.hubLobbyMeta.joinSigns.map { p -> this.printLocation(p) }
        val leaveSigns = arena.meta.hubLobbyMeta.leaveSigns.map { p -> this.printLocation(p) }

        if (arena.gameType == GameType.HUBGAME) {
            return ChatBuilder()
                    .component("- Signs Team Red: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                    .setHoverText(teamSignsRed.toSingleLine())
                    .builder()
                    .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_ADDTEAMRED.command)
                    .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the red team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                    .builder().nextLine()
                    .component("- Signs Team Blue: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                    .setHoverText(teamSignsBlue.toSingleLine())
                    .builder()
                    .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_ADDTEAMBLUE.command)
                    .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the blue team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                    .builder().nextLine()
                    .component("- Signs Join any team: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                    .setHoverText(joinSigns.toSingleLine())
                    .builder()
                    .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_ADDJOINANY.command)
                    .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                    .builder().nextLine()
                    .component("- Signs Leave: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                    .setHoverText(leaveSigns.toSingleLine())
                    .builder()
                    .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SIGNS_LEAVE.command)
                    .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically leave the game.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                    .builder().nextLine()
        }
        return ChatBuilder();
    }
}