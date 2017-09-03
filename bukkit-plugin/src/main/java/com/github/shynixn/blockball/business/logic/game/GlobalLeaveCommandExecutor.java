package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.lib.DynamicCommandHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class GlobalLeaveCommandExecutor extends DynamicCommandHelper {
    private final GameController controller;

    GlobalLeaveCommandExecutor(GameController controller) {
        super(Config.getInstance().getGlobalLeaveCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Game game;
            final Player player = (Player) sender;
            if ((game = this.controller.isInGameLobby(player)) != null)
                game.leave(player);
            if ((game = this.controller.getGameFromPlayer(player)) != null)
                game.leave(player);
        }
    }
}