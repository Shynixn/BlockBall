package com.github.shynixn.blockball.business.bungee.connector;

import com.github.shynixn.blockball.lib.ReflectionLib;
import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Shynixn
 */
class BungeeCordCommandExecutor extends BukkitCommand {
    private final BungeeCordController controller;

    BungeeCordCommandExecutor(BungeeCordController controller) {
        super(BungeeCord.COMMAND_COMMAND);
        this.controller = controller;
        this.description = BungeeCord.COMMAND_DESCRIPTION;
        this.usageMessage = BungeeCord.COMMAND_USEAGE;
        this.setPermission(BungeeCord.COMMAND_PERMISSION);
        this.setPermissionMessage(BungeeCord.COMMAND_PERMISSION_MESSAGE);
        this.setAliases(new ArrayList<>());
        this.registerDynamicCommand(BungeeCord.COMMAND_COMMAND, this);
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(this.getPermissionMessage());
            return true;
        }
        if (sender instanceof Player) {
            if (args.length == 1) {
                this.controller.lastServer.put((Player) sender, args[0]);
                sender.sendMessage(ChatColor.YELLOW + "Rightclick on a sign to convert it into a server sign.");
            }
        }
        return true;
    }

    private void registerDynamicCommand(String command, BukkitCommand clazz) {
        try {
            final Class<?> subclazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", BungeeCord.getServerVersion()));
            Object instance = subclazz.cast(Bukkit.getServer());
            instance = BungeeCord.invokeMethodByObject(instance, "getCommandMap");
            ReflectionLib.invokeMethodByObject(instance, "register", command, clazz);
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot register dynamic command.", ex);
        }
    }
}
