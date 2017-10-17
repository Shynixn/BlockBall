package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.ConfigOld;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Language;
import com.github.shynixn.blockball.lib.DynamicCommandHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class GlobalJoinCommandExecutor extends DynamicCommandHelper {
    private final GameController controller;

    GlobalJoinCommandExecutor(GameController controller) {
        super(ConfigOld.getInstance().getGlobalJoinCommand());
        this.controller = controller;
    }

    @Override
    public void onCommandSend(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

        }
    }
}
