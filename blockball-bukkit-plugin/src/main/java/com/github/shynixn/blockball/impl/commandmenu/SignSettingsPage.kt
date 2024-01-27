package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.RightclickManageService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.entity.ChatBuilderEntity
import com.github.shynixn.mcutils.common.ChatColor
import com.google.inject.Inject

class SignSettingsPage @Inject constructor(
    private val proxyService: ProxyService,
    private val rightclickManageService: RightclickManageService
) : Page(SignSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 11
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.SIGNS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        when (command) {
            MenuCommand.SIGNS_ADDTEAMRED -> {
                proxyService.sendMessage(player, BlockBallLanguage.rightClickOnSign)
                rightclickManageService.watchForNextRightClickSign<P, Any>(player) { location ->
                    arena.meta.redTeamMeta.signs.add(proxyService.toPosition(location))
                    proxyService.sendMessage(player, BlockBallLanguage.saveAndReloadSign)
                }
            }
            MenuCommand.SIGNS_ADDTEAMBLUE -> {
                proxyService.sendMessage(player,BlockBallLanguage.rightClickOnSign)
                rightclickManageService.watchForNextRightClickSign<P, Any>(player) { location ->
                    arena.meta.blueTeamMeta.signs.add(proxyService.toPosition(location))
                    proxyService.sendMessage(player, BlockBallLanguage.saveAndReloadSign)
                }
            }
            MenuCommand.SIGNS_ADDJOINANY -> {
                proxyService.sendMessage(player, BlockBallLanguage.rightClickOnSign)
                rightclickManageService.watchForNextRightClickSign<P, Any>(player) { location ->
                    arena.meta.lobbyMeta.joinSigns.add(proxyService.toPosition(location))
                    proxyService.sendMessage(player, BlockBallLanguage.saveAndReloadSign)
                }
            }
            MenuCommand.SIGNS_LEAVE -> {
                proxyService.sendMessage(player,BlockBallLanguage.rightClickOnSign)
                rightclickManageService.watchForNextRightClickSign<P, Any>(player) { location ->
                    arena.meta.lobbyMeta.leaveSigns.add(proxyService.toPosition(location))
                    proxyService.sendMessage(player, BlockBallLanguage.saveAndReloadSign)
                }
            }
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
        val arena = cache[0] as Arena

        val teamSignsRed = arena.meta.redTeamMeta.signs.map { p -> p.toString() }
        val teamSignsBlue = arena.meta.blueTeamMeta.signs.map { p -> p.toString() }
        val joinSigns = arena.meta.lobbyMeta.joinSigns.map { p -> p.toString() }
        val leaveSigns = arena.meta.lobbyMeta.leaveSigns.map { p -> p.toString() }

        if (arena.gameType == GameType.HUBGAME) {
            return ChatBuilderEntity()
                .component("- Signs Team Red: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(teamSignsRed.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDTEAMRED.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the red team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
                .component("- Template Signs Team Red: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.redTeamMeta.signLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " red")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Team Blue: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(teamSignsBlue.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDTEAMBLUE.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and the blue team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
                .component("- Template Signs Team Blue: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.blueTeamMeta.signLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " blue")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Join any team: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(joinSigns.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDJOINANY.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically join the game and team.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
                .component("- Template Signs Join: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.lobbyMeta.joinSignLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " join")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Leave: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(leaveSigns.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_LEAVE.command)
                .setHoverText(ChatColor.YELLOW.toString() + "Players clicking this sign automatically leave the game.\n&6&m      \n&rEnables the next sign to be added after you rightclicked it.\nDestroy the sign to remove it.")
                .builder().nextLine()
                .component("- Template Signs Leave: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.lobbyMeta.leaveSignLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " leave")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
        } else if (arena.gameType == GameType.MINIGAME || arena.gameType == GameType.BUNGEE) {
            return ChatBuilderEntity()
                .component("- Signs Team Red: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(teamSignsRed.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDTEAMRED.command)
                .setHoverText(ChatColor.WHITE.toString() + "Only players in Lobbies can click on this sign and will be queued for this team if the team has not reached it's max amount yet and even teams is disabled.")
                .builder().nextLine()
                .component("- Template Signs Team Red: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.redTeamMeta.signLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " red")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Team Blue: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(teamSignsBlue.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDTEAMBLUE.command)
                .setHoverText(ChatColor.WHITE.toString() + "Only players in Lobbies can click on this sign and will be queued for this team if the team has not reached it's max amount yet and even teams is disabled.")
                .builder().nextLine()
                .component("- Template Signs Team Blue: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.blueTeamMeta.signLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " blue")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Join any team: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(joinSigns.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_ADDJOINANY.command)
                .setHoverText(ChatColor.WHITE.toString() + "Players clicking this sign automatically join the game lobby.")
                .builder().nextLine()
                .component("- Template Signs Join: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.lobbyMeta.joinSignLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " join")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
                .component("- Signs Leave: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(leaveSigns.toSingleLine())
                .builder()
                .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_LEAVE.command)
                .setHoverText(ChatColor.WHITE.toString() + "Players clicking this sign automatically leave the game or lobby.").builder().nextLine()
                .component("- Template Signs Leave: ").builder().component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.lobbyMeta.leaveSignLines.toList().toSingleLine()).builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_TEAMSIGNTEMPLATE.command + " leave")
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
        }

        return ChatBuilderEntity()
    }
}
