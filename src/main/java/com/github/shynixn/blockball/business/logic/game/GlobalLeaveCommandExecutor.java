package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.lib.DynamicCommandHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Shynixn
 */
class GlobalLeaveCommandExecutor extends DynamicCommandHelper {
    private GameController controller;

    GlobalLeaveCommandExecutor(GameController controller) {
        super(Config.getInstance().getGlobalLeaveCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Game game;
            Player player = (Player) sender;
            if ((game = controller.isInGameLobby(player)) != null)
                game.leave(player);
            if ((game = controller.getGameFromPlayer(player)) != null)
                game.leave(player);
        }
    }
}