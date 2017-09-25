package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Language;
import com.github.shynixn.blockball.lib.DynamicCommandHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class GlobalJoinCommandExecutor extends DynamicCommandHelper {
    private final GameController controller;

    GlobalJoinCommandExecutor(GameController controller) {
        super(Config.getInstance().getGlobalJoinCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            Game game;
            if ((game = this.controller.isInGameLobby(player)) != null)
                game.leave(player);
            if ((game = this.controller.getGameFromPlayer(player)) != null)
                game.leave(player);
            if (args.length >= 3) {
                String s = "";
                for (int i = 1; i < args.length; i++) {
                    if (!s.isEmpty())
                        s += " ";
                    s += args[i];
                }
                args = new String[]{args[0], s};
            }
            if (args.length == 2 &&
                    ((tryPInt(args[0]) && (game = this.controller.getGameFromArenaId(Integer.parseInt(args[0]))) != null) ||
                            ((game = this.controller.getGameFromAlias(args[0])) != null))) {
                if (ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
                    if (game instanceof MiniGameEntity) {
                        if ((((MiniGameEntity) game)).isLobbyFull())
                            player.sendMessage(Language.PREFIX + Language.ARENA_LOBBYFULL_MESSAGE);
                        else
                            ((MiniGameEntity) game).joinLobby(player);
                    } else {
                        if (!game.join(player, Team.RED)) {
                            player.sendMessage(Language.PREFIX + game.getArena().getTeamMeta().getTeamFullMessage());
                        }
                    }
                } else if (ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()).equalsIgnoreCase(ChatColor.stripColor(args[1]))) {
                    if (game instanceof MiniGameEntity) {
                        if ((((MiniGameEntity) game)).isLobbyFull())
                            player.sendMessage(Language.PREFIX + Language.ARENA_LOBBYFULL_MESSAGE);
                        else
                            ((MiniGameEntity) game).joinLobby(player);
                    } else {
                        if (!game.join(player, Team.BLUE)) {
                            player.sendMessage(Language.PREFIX + game.getArena().getTeamMeta().getTeamFullMessage());
                        }
                    }
                }
            } else if (args.length == 1 && ((tryPInt(args[0]) && (game = this.controller.getGameFromArenaId(Integer.parseInt(args[0]))) != null) || ((game = this.controller.getGameFromAlias(args[0])) != null))) {
                if (game instanceof MiniGameEntity) {
                    if ((((MiniGameEntity) game)).isLobbyFull())
                        player.sendMessage(Language.PREFIX + Language.ARENA_LOBBYFULL_MESSAGE);
                    else
                        ((MiniGameEntity) game).joinLobby(player);
                }
            }
        }
    }
}
