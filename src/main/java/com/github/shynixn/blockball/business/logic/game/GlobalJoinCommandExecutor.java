package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.lib.DynamicCommandHelper;
import com.github.shynixn.blockball.lib.SMathUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Shynixn
 */
class GlobalJoinCommandExecutor extends DynamicCommandHelper {
    private GameController controller;

    GlobalJoinCommandExecutor(GameController controller) {
        super(Config.getInstance().getGlobalJoinCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Game game;
            if ((game = controller.isInGameLobby(player)) != null)
                game.leave(player);
            if ((game = controller.getGameFromPlayer(player)) != null)
                game.leave(player);
            if (args.length >= 3) {
                String s = "";
                for (int i = 1; i < args.length; i++) {
                    if (!s.equals(""))
                        s = s + " ";
                    s = s + args[i];
                }
                args = new String[]{args[0], s};
            }
            if (args.length == 2 &&
                    ((SMathUtils.tryPInt(args[0]) && (game = controller.getGameFromArenaId(Integer.parseInt(args[0]))) != null) ||
                            ((game = controller.getGameFromAlias(args[0])) != null))) {
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
            } else if (args.length == 1 && ((SMathUtils.tryPInt(args[0]) && (game = controller.getGameFromArenaId(Integer.parseInt(args[0]))) != null) || ((game = controller.getGameFromAlias(args[0])) != null))) {
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
