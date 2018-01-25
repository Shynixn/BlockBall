package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class JoinCommandExecutor extends SimpleCommandExecutor.UnRegistered {

    private final GameController gameController;

    /**
     * Initializes a new commandExecutor by all required parameters
     *
     * @param plugin plugin
     */
    public JoinCommandExecutor(GameController gameController, Plugin plugin) throws Exception {
        super(plugin.getConfig().get("global-join"), (JavaPlugin) plugin);
        this.gameController = gameController;
    }

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
      //  Optional<Game> gameOpt = this.gameController.getGameFromPlayer(player);
      //  gameOpt.ifPresent(game -> game.leave(player));
      //  if (args.length >= 3) {
      //      args = this.mergeArguments(args);
      //  }
        /*
        if (args.length == 2 && ((MathUtils.tryParseInteger(args[0])
                && (gameOpt = this.gameController.getGameFromArenaId(Integer.parseInt(args[0]))).isPresent())
                || ((gameOpt = this.gameController.getGameFromAlias(args[0])).isPresent()))) {
            final Game game = gameOpt.get();


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
        }*/
    }

    /**
     * Merges the given parameters arguments.
     *
     * @param args args
     * @return mergedArguments
     */
    private String[] mergeArguments(String[] args) {
        final StringBuilder s = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (s.length() != 0) {
                s.append(' ');
            }
            s.append(args[i]);
        }
        return new String[]{args[0], s.toString()};
    }
}
