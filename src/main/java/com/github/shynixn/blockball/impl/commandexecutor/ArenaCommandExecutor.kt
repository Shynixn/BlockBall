package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.entity.ChatBuilder
import com.github.shynixn.blockball.enumeration.ChatClickAction
import com.github.shynixn.blockball.enumeration.MenuCommand
import com.github.shynixn.blockball.enumeration.MenuCommandResult
import com.github.shynixn.blockball.impl.commandmenu.*
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class ArenaCommandExecutor @Inject constructor(
    private val plugin: Plugin,
    private val chatMessageService: ChatMessageService,
    openPage: OpenPage,
    mainConfigurationPage: MainConfigurationPage,
    spectatePage: SpectatePage,
    mainSettingsPage: MainSettingsPage,
    ballSettingsPage: BallSettingsPage,
    ballModifierPage: BallModifierSettingsPage,
    listablePage: ListablePage,
    teamSettingsPage: TeamSettingsPage,
    effectsSettingsPage: EffectsSettingsPage,
    multipleLinesPage: MultipleLinesPage,
    hologramsPage: HologramPage,
    scoreboardPage: ScoreboardPage,
    bossbarPage: BossbarPage,
    signSettingsPage: SignSettingsPage,
    soundsPage: SoundEffectPage,
    abilitiesPage: AbilitiesSettingsPage,
    doubleJumpPage: DoubleJumpPage,
    miscPage: MiscSettingsPage,
    gamePropertiesPage: GamePropertiesPage,
    areaProtectionPage: AreaProtectionPage,
    teamTextBookPage: TeamTextBookPage,
    gameSettingsPage: GameSettingsPage,
    spectatingSettingsPage: SpectatingSettingsPage,
    notificationPage: NotificationPage,
    matchtimesPage: MatchTimesPage
) : CommandExecutor {


    companion object {
        private val HEADER_STANDARD =
            ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                          BlockBall                         "
        private val FOOTER_STANDARD =
            ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            "
    }

    private val cache = HashMap<Any, Array<Any?>>()
    private var pageCache: MutableList<Page> = arrayListOf(
        openPage,
        mainConfigurationPage,
        spectatePage,
        mainSettingsPage,
        ballSettingsPage,
        ballModifierPage,
        listablePage,
        teamSettingsPage,
        effectsSettingsPage,
        multipleLinesPage,
        hologramsPage,
        scoreboardPage,
        bossbarPage,
        signSettingsPage,
        soundsPage,
        abilitiesPage,
        doubleJumpPage,
        miscPage,
        gamePropertiesPage,
        areaProtectionPage,
        teamTextBookPage,
        gameSettingsPage,
        spectatingSettingsPage,
        notificationPage,
        matchtimesPage
    )

    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        try {
            for (i in 0..19) {
                source.sendMessage("")
            }
            source.sendMessage(HEADER_STANDARD)
            source.sendMessage("\n")
            if (!this.cache.containsKey(source)) {
                val anyArray = arrayOfNulls<Any>(8)
                this.cache[source] = anyArray
            }
            val cache: Array<Any?>? = this.cache[source]
            val command = MenuCommand.from(args)
                ?: throw IllegalArgumentException("Command is not registered!")
            var usedPage: Page? = null
            for (page in this.pageCache) {
                if (page.getCommandKey() === command.key) {
                    usedPage = page
                    if (command == MenuCommand.BACK) {
                        val newPage = this.getPageById(Integer.parseInt(args[2]))
                        val b = newPage.buildPage(cache!!)!!
                        if (source is Player) {
                            chatMessageService.sendChatMessage(source, b.convertToTextComponent())
                        }
                    } else if (command == MenuCommand.CLOSE) {
                        this.cache.remove(source)
                        for (i in 0..19) {
                            source.sendMessage("")
                        }
                        return true
                    } else {
                        @Suppress("UNCHECKED_CAST") val result =
                            page.execute(source, command, cache!!, args as Array<String>)
                        if (result == MenuCommandResult.BACK) {
                            if (source is Player) {
                                source.performCommand("blockball open back " + usedPage.getPreviousIdFrom(cache))
                            }
                            return true
                        }
                        if (result == MenuCommandResult.EXIT_COMP) {
                            return true
                        }
                        if (result != MenuCommandResult.SUCCESS && result != MenuCommandResult.CANCEL_MESSAGE) {
                            source.sendMessage(ChatColor.RED.toString() + result.message)
                        }
                        if (result != MenuCommandResult.CANCEL_MESSAGE) {
                            val b = page.buildPage(cache)!!
                            if (source is Player) {
                                chatMessageService.sendChatMessage(source, b.convertToTextComponent())
                            }
                        }
                    }
                    break
                }
            }
            if (usedPage == null)
                throw IllegalArgumentException("Cannot find page with key " + command.key)
            val builder = ChatBuilder()
                .text(ChatColor.STRIKETHROUGH.toString() + "----------------------------------------------------")
                .nextLine()
                .component(" >>Save<< ")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_SAVE.command)
                .setHoverText("Saves the current arena if possible.")
                .builder()
            if (usedPage is ListablePage) {
                builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(ChatClickAction.RUN_COMMAND, (cache!![3] as MenuCommand).command)
                    .setHoverText("Goes back to the previous page.")
                    .builder()
            } else {
                builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(
                        ChatClickAction.RUN_COMMAND,
                        MenuCommand.BACK.command + usedPage.getPreviousIdFrom(cache!!)
                    )
                    .setHoverText("Goes back to the previous page.")
                    .builder()
            }
            val b = builder.component(" >>Save and reload<<")
                .setColor(ChatColor.BLUE)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.ARENA_RELOAD.command)
                .setHoverText("Saves the current arena and reloads all games on the server.")
                .builder()

            if (source is Player) {
                chatMessageService.sendChatMessage(source, b.convertToTextComponent())
                source.sendMessage(FOOTER_STANDARD)
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Command completion failed.", e)
            source.sendMessage("[BlockBall] Cannot find command.")
            val data = StringBuilder()
            args.map { d -> data.append(d).append(" ") }
            plugin.logger.log(Level.INFO, "Cannot find command for args $data.")
        }

        return true
    }

    /**
     * Returns the [Page] with the given [id] and throws
     * a [RuntimeException] if the page is not found.
     */
    private fun getPageById(id: Int): Page {
        for (page in this.pageCache) {
            if (page.id == id) {
                return page
            }
        }
        throw RuntimeException("Page does not exist!")
    }
}
