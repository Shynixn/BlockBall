package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.GameType;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.entities.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class EventCommandExecutor extends DynamicCommandHelper {
    private static final String HEADER_STANDARD = ChatColor.WHITE + "" +  ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                         Event                      ";
    private static final String FOOTER_STANDARD = ChatColor.WHITE + "" +  ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           1/1                            ";
    private static final String MENU_BACK = "b - Go back";
    private static final String MENU_EXIT = "e - Close chat menu";
    private final GameController controller;

    EventCommandExecutor(GameController controller) {
        super(Config.getInstance().getEventContainerCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Game game;
            if (args.length == 1 && SMathUtils.tryPInt(args[0]) && ((game = this.controller.getGameFromArenaId(Integer.parseInt(args[0]))) != null)) {
                if (game.getArena().getGameType() == GameType.EVENT && game.getArena().getEventMeta().getReferee() != null && game.getArena().getEventMeta().getReferee().equalsIgnoreCase(player.getName())) {
                    SChatMenuManager.getInstance().open(player, new ControllerPage(player, game));
                } else {
                    player.sendMessage(Language.PREFIX + "Your are not the referee of this game.");
                }
            } else {
                player.sendMessage(Language.PREFIX + "This arena does not exist. /" + Config.getInstance().getEventContainerCommand().getCommand() + " <id>");
            }
        }
    }

    private static class ControllerPage extends SChatpage {
        private final EventGameEntity game;

        ControllerPage(Player player, Game game) {
            super(player);
            this.game = (EventGameEntity) game;
            ((EventGameEntity) game).referee = player;
        }

        @Override
        public void onPlayerSelect(int number) {
            if (number == 1) {
                this.game.executeJoinAllCommand();
            } else if (number == 2) {
                this.game.innerForcefield = !this.game.innerForcefield;
                if (this.game.innerForcefield) {
                    this.player.sendMessage(Language.PREFIX + "Enabled inner forcefield.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled inner forcefield.");
                }
            } else if (number == 3) {
                this.game.visitorForceField = !this.game.visitorForceField;
                if (this.game.visitorForceField) {
                    this.player.sendMessage(Language.PREFIX + "Enabled visitor forcefield.");
                } else {
                    this.player.sendMessage(Language.PREFIX + "Disabled visitor forcefield.");
                }
            } else if (number == 4) {
                this.game.executeInterrupt();
                this.player.sendMessage(Language.PREFIX + "Enabled interrupt");
            } else if (number == 5) {
                this.game.executeContinue();
                this.player.sendMessage(Language.PREFIX + "Continue game.");
            } else if (number == 6) {
                this.game.executeEndGame();
                this.player.sendMessage(Language.PREFIX + "End game.");
            } else if (number == 7) {
                this.game.reset();
                this.player.sendMessage(Language.PREFIX + "Disable game.");
            } else if (number == 8) {
                if (this.game.getBall() != null)
                    this.game.getBall().teleport(this.player.getLocation().add(0, 1, 0));
            }
        }

        @Override
        public void show() {
            this.player.sendMessage("");
            this.player.sendMessage(HEADER_STANDARD);
            this.player.sendMessage("");
            this.player.sendMessage(Language.PREFIX + "1 - Activate game");
            this.player.sendMessage(Language.PREFIX + "2 - Toggle Player Forcefield");
            this.player.sendMessage(Language.PREFIX + "3 - Toggle Spectator Forcefield");
            this.player.sendMessage(Language.PREFIX + "4 - Interrupt");
            this.player.sendMessage(Language.PREFIX + "5 - Start/Continue");
            this.player.sendMessage(Language.PREFIX + "6 - Stop");
            this.player.sendMessage(Language.PREFIX + "7 - Deactivate game");
            this.player.sendMessage(Language.PREFIX + "8 - Teleport ball");
            this.player.sendMessage(Language.PREFIX + ChatColor.GREEN + MENU_BACK);
            this.player.sendMessage(Language.PREFIX + ChatColor.RED + MENU_EXIT);
            this.player.sendMessage("");
            this.player.sendMessage(FOOTER_STANDARD);
            this.player.sendMessage("");
        }
    }
}
