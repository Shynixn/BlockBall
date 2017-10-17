package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallManager;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.entity.EventGameEntity;
import com.github.shynixn.blockball.lib.ChatBuilder;
import com.github.shynixn.blockball.lib.MathUtils;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventCommandExecutor extends SimpleCommandExecutor.UnRegistered {
    private static final String HEADER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                         Event                      ";
    private static final String FOOTER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           1/1                            ";
    private final GameController gameController;

    /**
     * Initializes a new commandExecutor by all required parameters.
     *
     * @param plugin plugin
     */
    public EventCommandExecutor(BlockBallManager blockBallManager, Plugin plugin) {
        super(Config.getInstance().getRefereeCommandName(), Config.getInstance().getRefereeCommandUseag(),
                Config.getInstance().getRefereeCommandDescription(), Config.getInstance().getRefereeCommandPermission(),
                Config.getInstance().getRefereeCommandPermissionMessage(), (JavaPlugin) plugin);
        this.gameController = blockBallManager.getGameController();
    }

    /**
     * Can be overwritten to listen to player executed commands
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
        super.onPlayerExecuteCommand(player, args);
        final EventGameEntity game;
        if (args.length >= 1 && MathUtils.tryParseInteger(args[0]) && ((game = (EventGameEntity) this.gameController.getGameFromArenaId(Integer.parseInt(args[0]))) != null)) {
            if (game.getArena().getGameType() == GameType.EVENT
                    && game.getArena().getEventMeta().getRefereeName().isPresent()
                    && game.getArena().getEventMeta().getRefereeName().get().equalsIgnoreCase(player.getName())) {
                player.sendMessage(HEADER_STANDARD);
                if (args.length == 2) {
                    this.executeActions(player, args, game);
                }
                this.printOptions(args[0]);
                player.sendMessage(FOOTER_STANDARD);
            } else {
                player.sendMessage(Config.getInstance().getPrefix() + "Your are not the referee of this game.");
            }
        } else {
            player.sendMessage(Config.getInstance().getPrefix() + "This arena does not exist. /" + Config.getInstance().getRefereeCommandName() + " <id>");
        }
    }

    /**
     * Executes the actions behind the options.
     *
     * @param player players
     * @param args   args
     * @param game   game
     */
    private void executeActions(Player player, String[] args, EventGameEntity game) {
        if (args[1].equalsIgnoreCase("acg")) {
            game.executeJoinAllCommand();
        } else if (this.isText(args, "tif")) {
            game.innerForcefield = !game.innerForcefield;
            if (game.innerForcefield) {
                player.sendMessage(Config.getInstance().getPrefix() + "Enabled inner forcefield.");
            } else {
                player.sendMessage(Config.getInstance().getPrefix() + "Disabled inner forcefield.");
            }
        } else if (this.isText(args, "tsf")) {
            game.visitorForceField = !game.visitorForceField;
            if (game.visitorForceField) {
                player.sendMessage(Config.getInstance().getPrefix() + "Enabled visitor forcefield.");
            } else {
                player.sendMessage(Config.getInstance().getPrefix() + "Disabled visitor forcefield.");
            }
        } else if (this.isText(args, "in")) {
            game.executeInterrupt();
            player.sendMessage(Config.getInstance().getPrefix() + "Enabled interrupt");
        } else if (this.isText(args, "sc")) {
            game.executeContinue();
            player.sendMessage(Config.getInstance().getPrefix() + "Continue game.");
        } else if (this.isText(args, "sto")) {
            game.executeEndGame();
            player.sendMessage(Config.getInstance().getPrefix() + "End game.");
        } else if (this.isText(args, "dg")) {
            game.reset();
            player.sendMessage(Config.getInstance().getPrefix() + "Disable game.");
        } else if (this.isText(args, "tpb")) {
            if (game.getBall() != null) {
                game.getBall().teleport(player.getLocation().add(0, 1, 0));
            }
        }
    }

    /**
     * Prints all available options.
     *
     * @param arg arg
     */
    private void printOptions(String arg) {
        new ChatBuilder().component("Activate game").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "acg");
        new ChatBuilder().component("Toggle Player Forcefield").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "tif");
        new ChatBuilder().component("Toggle Spectator Forcefield").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "tsf");
        new ChatBuilder().component("Interrupt").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "in");
        new ChatBuilder().component("Start/Continue").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "sc");
        new ChatBuilder().component("Stop").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "sto");
        new ChatBuilder().component("Deactivate game").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "dg");
        new ChatBuilder().component("Teleport ball").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, Config.getInstance().getRefereeCommandName() + ' ' + arg + "tpb");
    }

    /**
     * Checks if the given text is present at args[1]
     *
     * @param args args
     * @param text text
     * @return isPresent
     */
    private boolean isText(String[] args, String text) {
        return args[1].equalsIgnoreCase(text);
    }
}
