package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.mcutils.common.ChatColor

class MainSettingsPage : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 3
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.MAINSETTING
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        if (command == MenuCommand.SETTINGS_OPEN && args.size == 3) {
            val arena = cache[0] as Arena
            arena.gameType = GameType.values()[args[2].toInt()]
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
        return ChatBuilder()
            .component("- GameType: " + arena.gameType.name).builder()
            .component(" [select..]")
            .setColor(ChatColor.AQUA)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_GAMETYPES.command)
            .setHoverText("Opens the selectionbox for game modes.")
            .builder().nextLine()
            .component("- Game Settings:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_OPEN.command)
            .setHoverText("Common and exclusive Game Settings for this GameType.")
            .builder().nextLine()
            .component("- Ball Settings:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.BALL_OPEN.command)
            .setHoverText("Opens the settings page for the ball.")
            .builder().nextLine()
            .component("- Team Red:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_RED_CONFIGURE.command)
            .setHoverText("Opens the settings page for the red team.")
            .builder().nextLine()
            .component("- Team Blue:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_BLUE_CONFIGURE.command)
            .setHoverText("Opens the settings page for the blue team.")
            .builder().nextLine()
            .component("- Signs:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SIGNS_OPEN.command)
            .setHoverText("Configure all signs for this gamemode.").builder().nextLine()
            .component("- Effects:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.EFFECTS_OPEN.command)
            .setHoverText("Opens the settings page for effects like scoreboard, bossbar and holograms.").builder().nextLine()
            .component("- Abilities:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ABILITIES_OPEN.command)
            .setHoverText("Opens the settings page for double jumps and boost effects.").builder().nextLine()
            .component("- Rewards:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.REWARD_OPEN.command)
            .setHoverText("Opens the settings page for rewards.")
            .builder().nextLine()
            .component("- Spectating:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SPECTATOR_OPEN.command)
            .setHoverText("Opens the settings page for spectating.")
            .builder().nextLine()
            .component("- Misc:").builder()
            .component(" [page..]").setColor(ChatColor.YELLOW)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MISC_OPEN.command)
            .setHoverText("Opens the settings page for other settings.").builder()
    }
}
