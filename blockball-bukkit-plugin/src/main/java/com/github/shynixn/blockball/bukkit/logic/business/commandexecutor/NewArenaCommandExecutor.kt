package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu.*
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


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
class NewArenaCommandExecutor @Inject constructor(plugin: Plugin) : SimpleCommandExecutor.Registered("blockball", plugin as JavaPlugin) {
    companion object {
        private val HEADER_STANDARD = ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                          BlockBall                         "
        private val FOOTER_STANDARD = ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            "
    }


    private val cache = HashMap<Player, Array<Any?>>()

    @Inject
    private val openPage: OpenPage? = null

    @Inject
    private val mainConfigurationPage: MainConfigurationPage? = null

    @Inject
    private val mainSettingsPage: MainSettingsPage? = null

    @Inject
    private val listablePage: ListablePage? = null

    @Inject
    private val teamSettingsPage: TeamSettingsPage? = null


    @Inject
    private val effectsSettingsPage: EffectsSettingsPage? = null

    @Inject
    private val multipleLinesPage: MultipleLinesPage? = null

    @Inject
    private val hologramsPage: HologramPage? = null

    @Inject
    private val scoreboardPage: ScoreboardPage? = null

    @Inject
    private val bossbarPage: BossbarPage? = null

    @Inject
    private val signSettingsPage: SignSettingsPage? = null

    @Inject
    private val arenaController: ArenaRepository? = null


    /**
     * Can be overwritten to listener to all executed commands.
     *
     * @param sender sender
     * @param args   args
     */
    override fun onCommandSenderExecuteCommand(sender: CommandSender, args: Array<out String>) {
        super.onCommandSenderExecuteCommand(sender, args)
        if (sender !is Player) {
            sender.sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "This command does not support console or command blocks.")
        }
    }

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    override fun onPlayerExecuteCommand(player: Player, args: Array<String>) {
        for (i in 0..19) {
            player.sendMessage("")
        }
        player.sendMessage(HEADER_STANDARD)
        player.sendMessage("\n")
        var cache: Array<Any?>?
        if (!this.cache.containsKey(player)) {
            val anyArray = arrayOfNulls<Any>(8);
            this.cache[player] = anyArray
        }
        cache = this.cache[player]
        val command = BlockBallCommand.from(args) ?: throw IllegalArgumentException("Command is not registered!")
        var usedPage: Page? = null
        for (page in this.getPageCache()) {
            if (page.getCommandKey() != null && page.getCommandKey() === command.key) {
                usedPage = page
                if (command == BlockBallCommand.BACK) {
                    val newPage = this.getPageById(Integer.parseInt(args[2]))
                    this.sendMessage(player, newPage.buildPage(cache!!)!!)
                } else if (command == BlockBallCommand.CLOSE) {
                    this.cache.remove(player)
                    for (i in 0..19) {
                        player.sendMessage("")
                    }
                    return
                } else {
                    val result = page.execute(player, command, cache!!, args!!)
                    if (result == CommandResult.BACK) {
                        player.performCommand("blockball open back " + usedPage!!.getPreviousIdFrom(cache))
                        return
                    }
                    if (result != CommandResult.SUCCESS && result != CommandResult.CANCEL_MESSAGE) {
                        ChatBuilder()
                                .component(ChatColor.WHITE.toString() + "" + ChatColor.BOLD + "[" + ChatColor.RED + ChatColor.BOLD + "!" + ChatColor.WHITE + ChatColor.BOLD + "]")
                                .setHoverText(result.getMessage()).builder().sendMessage(player)
                    }
                    if (result != CommandResult.CANCEL_MESSAGE) {
                        this.sendMessage(player, page.buildPage(cache)!!)
                    }
                    if (result == CommandResult.ARENA_NOTVALID) {
                        //          this.sendMessage(player, CommandResult.ARENA_NOTVALID.getMessage());
                    }
                }
                break
            }
        }
        if (usedPage == null)
            throw IllegalArgumentException("Cannot find page with key " + command.key)
        val builder = ChatBuilder()
                .text(ChatColor.STRIKETHROUGH.toString() + "----------------------------------------------------").nextLine()
                .component(" >>Save<< ")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SAVE.command)
                .setHoverText("Saves the current arena if possible.")
                .builder()
        if (usedPage is ListablePage) {
            builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, (cache!![3] as BlockBallCommand).command)
                    .setHoverText("Back.")
                    .builder()
        } else {
            builder.component(">>Back<<")
                    .setColor(ChatColor.RED)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.BACK.command + " " + usedPage!!.getPreviousIdFrom(cache!!))
                    .setHoverText("Opens the blockball arena configuration.")
                    .builder()
        }
        builder.component(" >>Save and reload<<")
                .setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_RELOAD.command)
                .setHoverText("Opens the blockball arena configuration.")
                .builder().sendMessage(player)

        player.sendMessage(FOOTER_STANDARD)


        /*   if (args.length == 0) {
            this.printFirstPage(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("crna")) {

                ArenaController controller = this.blockBallManager.getGameController().getArenaController();
                final Arena arena = controller.create();

                this.cache.put(player, arena);
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("cna")) {
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("set-wecorners") && this.cache.containsKey(player)) {
                final Location left = WorldEditConnection.getLeftSelection(player);
                final Location right = WorldEditConnection.getRightSelection(player);
                if (left != null && right != null) {
                    this.cache.get(player).setCorners(left, right);
                }
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("set-goalred") && this.cache.containsKey(player)) {
                this.setGoal(player, Team.RED);
            } else if (args[0].equalsIgnoreCase("set-goalblue") && this.cache.containsKey(player)) {
                this.setGoal(player, Team.BLUE);
            } else if (args[0].equalsIgnoreCase("set-ballspawn") && this.cache.containsKey(player)) {
                this.cache.get(player).setBallSpawnLocation(player.getLocation());
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("page-settings") && this.cache.containsKey(player)) {
                this.printSettingsSelectionPage(player);
            }

        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("set-displayname") && this.cache.containsKey(player)) {
                final String name = this.mergeArgs(1, args);
                this.cache.get(player).setDisplayName(name);
                this.printArenaPage(player);
            }
            if (args[0].equalsIgnoreCase("save") && this.cache.containsKey(player)) {
                final Arena arena = this.cache.get(player);
                this.blockBallManager.getGameController().getArenaController().store(arena);
                player.sendMessage("Arena was saved.");
                this.onPlayerExecuteCommand(player, new String[]{args[1]});
            }
        }*/

    }

    private var pagecache: MutableList<Page>? = null

    private fun getPageCache(): List<Page> {
        if (this.pagecache == null) {
            this.pagecache = ArrayList()
            this.pagecache!!.add(this.openPage!!)
            this.pagecache!!.add(this.mainConfigurationPage!!)
            this.pagecache!!.add(this.mainSettingsPage!!)
            this.pagecache!!.add(listablePage!!)
            this.pagecache!!.add(teamSettingsPage!!)
            this.pagecache!!.add(effectsSettingsPage!!)
            this.pagecache!!.add(scoreboardPage!!)
            this.pagecache!!.add(multipleLinesPage!!)
            this.pagecache!!.add(bossbarPage!!)
            this.pagecache!!.add(signSettingsPage!!)
            this.pagecache!!.add(hologramsPage!!);
        }
        return this.pagecache!!
    }

    private fun fullCommand(args: Array<String>): String {
        val builder = StringBuilder()
        builder.append("/blockball")
        for (s in args) {
            builder.append(" ")
            builder.append(s)
        }
        return builder.toString()
    }

    private fun getPageById(id: Int): Page {
        for (page in this.getPageCache()) {
            if (page.id == id) {
                return page
            }
        }
        throw RuntimeException("Page does not exist!")
    }

    private fun setGoal(player: Player, team: Team) {
        /*  final Location left = WorldEditConnection.getLeftSelection(player);
        final Location right = WorldEditConnection.getRightSelection(player);
        if (left != null && right != null) {
            this.getTeamMeta(player, team).getGoal().setCorners(left, right);
        }
        this.printArenaPage(player);*/
    }

    private fun printHologramEditingPage(player: Player) {
        if (!this.cache.containsKey(player))
            return

        this.sendMessage(player, ChatBuilder()
                .nextLine()
                .component("- Type configuration:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball page-set-types")
                .setHoverText("Opens the type configuration to change the gamemodes.")
                .builder().nextLine()
                .text(ChatColor.STRIKETHROUGH.toString() + "--------------------")
                .nextLine()
                .component(">>Save<<")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball save")
                .setHoverText("Saves the arena.")
                .builder().text(" ")
                .component(">>Back<<")
                .setColor(ChatColor.RED)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball crna")
                .setHoverText("Closes the current page.")
        )
    }

    private fun printSettingsSelectionPage(player: Player) {
        if (!this.cache.containsKey(player))
            return

        this.sendMessage(player, ChatBuilder()
                .nextLine()
                .component("- Add line of text:").builder()
                .component(" [edit..]").setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball add-holo-text")
                .setHoverText("Add a line of text to the hologram")
                .builder().nextLine()
                .text(ChatColor.STRIKETHROUGH.toString() + "--------------------")
                .nextLine()
                .component(">>Save<<")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball save")
                .setHoverText("Saves the arena.")
                .builder().text(" ")
                .component(">>Back<<")
                .setColor(ChatColor.RED)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball crna")
                .setHoverText("Closes the current page.")
        )

    }

    private fun sendMessage(player: Player, builder: ChatBuilder) {
        builder.sendMessage(player)
    }

    private fun sendMessage(player: Player, builder: ChatBuilder.Component) {
        this.sendMessage(player, builder.builder())
    }

}