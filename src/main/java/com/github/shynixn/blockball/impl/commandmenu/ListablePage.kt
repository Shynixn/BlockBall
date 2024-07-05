package com.github.shynixn.blockball.impl.commandmenu

import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.mcutils.common.EffectTargetType
import com.google.inject.Inject
import org.bukkit.GameMode

class ListablePage @Inject constructor(
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
            MenuCommand.LIST_BUKKITGAMESMODES -> {
                cache[2] = GameMode.values().map { e -> e.name }.filterNot { g -> g == "SPECTATOR" }
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
            MenuCommand.LIST_REWARDED_MONEY -> {
                cache[2] = RewardType.values().map { p -> p.name }
                cache[3] = MenuCommand.REWARD_CALLBACK_MONEY
            }
            MenuCommand.LIST_REWARDED_COMMAND -> {
                cache[2] = RewardType.values().map { p -> p.name }
                cache[3] = MenuCommand.REWARD_CALLBACK_COMMAND
            }
            MenuCommand.LIST_SOUND_TYPES -> {
                cache[2] = org.bukkit.Sound.values().map { s -> s.name }
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
        val builder = ChatBuilder()

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
