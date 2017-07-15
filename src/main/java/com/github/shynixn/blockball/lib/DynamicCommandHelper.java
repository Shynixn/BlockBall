package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class DynamicCommandHelper extends BukkitCommand {
    private final Config.CommandContainer c;

    public DynamicCommandHelper(Config.CommandContainer c) {
        super(c.getCommand(),c.getDescription(),c.getUseage(), new ArrayList<>());
        this.c = c;
        this.setPermission(c.getPermission());
        this.setAliases(new ArrayList<>());
        NMSRegistry.registerDynamicCommand(c.getCommand(), this);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(this.c.getPermissionMessage());
        } else if (sender instanceof Player) {
            this.onCommandSend(sender, args);
        }
        return true;
    }

    public abstract void onCommandSend(CommandSender sender, String[] args);


    public String getText(String[] args) {
        String s = "";
        for (final String k : args) {
            if (!s.equalsIgnoreCase(""))
                s += " ";
            s += k;
        }
        return s;
    }
}
