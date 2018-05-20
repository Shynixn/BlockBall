package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

/**
 * Holds the different types of commandExecutors.
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
public class SimpleCommandExecutor {

    /**
     * Creates a new commandExecutor from preDefined commands in the plugin.yml.
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
    public static class Registered implements CommandExecutor {
        final protected JavaPlugin plugin;

        /**
         * Initializes a new commandExecutor by command and pluginName.
         *
         * @param command    command
         * @param pluginName pluginName
         */
        public Registered(String command, String pluginName) {
            this(command, (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName));
        }

        /**
         * Initializes a new commandExecutor by command and pluginClass.
         *
         * @param command     command
         * @param pluginClass pluginClass
         * @param <T>         pluginClassType
         */
        public <T extends JavaPlugin> Registered(String command, Class<T> pluginClass) {
            this(command, JavaPlugin.getPlugin(pluginClass));
        }

        /**
         * Initializes a new commandExecutor by command and plugin.
         *
         * @param command command
         * @param plugin  plugin
         */
        public Registered(String command, JavaPlugin plugin) {
            super();
            if (plugin == null)
                throw new IllegalArgumentException("Plugin cannot be null!");
            if (command == null)
                throw new IllegalArgumentException("Command cannot be null!");
            this.plugin = plugin;
            plugin.getCommand(command).setExecutor(this);
        }

        /**
         * Gets called when the user enters a command and redirects on onPlayerExecute or onCommandSenderExecuteCommand.
         *
         * @param commandSender commandSender
         * @param command       command
         * @param s             text
         * @param args          args
         * @return success
         */
        @Override
        public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
            if (commandSender instanceof Player) {
                this.onPlayerExecuteCommand((Player) commandSender, args);
            }
            this.onCommandSenderExecuteCommand(commandSender, args);
            return true;
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        public void onPlayerExecuteCommand(Player player, String[] args) {

        }

        /**
         * Can be overwritten to listener to all executed commands.
         *
         * @param sender sender
         * @param args   args
         */
        public void onCommandSenderExecuteCommand(CommandSender sender, String[] args) {

        }
    }

    /**
     * Creates a new independent commandExecutor from the given constructor parameters.
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
    public static class UnRegistered extends BukkitCommand {
        final protected JavaPlugin plugin;

        /**
         * Initializes a new commandExecutor by testing parameters and plugin.
         *
         * @param command command
         * @param plugin  plugin
         */
        public UnRegistered(String command, JavaPlugin plugin) {
            this(command, '/' + command, "", command + ".admin", "You are not allowed to use this command.", plugin);
            Bukkit.getLogger().log(Level.WARNING, "This is only for production useage and should be replaced in the release version.");
        }

        /**
         * Initializes a new commandExecutor by using the given config configuration and plugin.
         *
         * @param configuration configuration
         * @param plugin        plugin
         * @throws Exception exception
         */
        public UnRegistered(Object configuration, JavaPlugin plugin) throws Exception {
            super((String) getConfigValues(configuration).get("command"));
            final Map<String, Object> configurationMap = getConfigValues(configuration);
            this.plugin = plugin;
            this.setDescription((String) configurationMap.get("description"));
            this.setUsage((String) configurationMap.get("useage"));
            this.setPermission((String) configurationMap.get("permission"));
            this.setPermissionMessage((String) configurationMap.get("permission-message"));
            this.setAliases(new ArrayList<>());
            this.registerDynamicCommand((String) configurationMap.get("command"));
        }

        /**
         * Initializes a new commandExecutor by all required parameters and plugin.
         *
         * @param command           command
         * @param useAge            useAge
         * @param description       description
         * @param permission        permission
         * @param permissionMessage permissionMessage
         * @param plugin            plugin
         */
        public UnRegistered(String command, String useAge, String description, String permission, String permissionMessage, JavaPlugin plugin) {
            super(command);
            if (useAge == null)
                throw new IllegalArgumentException("Useage cannot be null!");
            if (description == null)
                throw new IllegalArgumentException("Description cannot be null!");
            if (permission == null)
                throw new IllegalArgumentException("Permission cannot be null!");
            if (permissionMessage == null)
                throw new IllegalArgumentException("PermissionMessage cannot be null!");
            if (plugin == null)
                throw new IllegalArgumentException("Plugin cannot be null!");
            this.plugin = plugin;
            this.setDescription(description);
            this.setUsage(useAge);
            this.setPermission(permission);
            this.setPermissionMessage(permissionMessage);
            this.setAliases(new ArrayList<>());
            this.registerDynamicCommand(command);
        }

        /**
         * Gets called when the user enters a command and redirects on onPlayerExecute or onCommandSenderExecuteCommand.
         *
         * @param sender sender
         * @param alias  alias
         * @param args   args
         * @return success
         */
        @Override
        public final boolean execute(CommandSender sender, String alias, String[] args) {
            if (!sender.hasPermission(this.getPermission())) {
                sender.sendMessage(this.getPermissionMessage());
                return true;
            }
            if (sender instanceof Player) {
                this.onPlayerExecuteCommand((Player) sender, args);
            }
            this.onCommandSenderExecuteCommand(sender, args);
            return true;
        }

        /**
         * Can be overwritten to listen to player executed commands.
         *
         * @param player player
         * @param args   args
         */
        public void onPlayerExecuteCommand(Player player, String[] args) {

        }

        /**
         * Can be overwritten to listener to all executed commands.
         *
         * @param sender sender
         * @param args   args
         */
        public void onCommandSenderExecuteCommand(CommandSender sender, String[] args) {

        }

        protected final String mergeArgs(int starting, int amount, String[] args) {
            final StringBuilder builder = new StringBuilder();
            int counter = 0;
            for (int i = starting; counter < amount; i++) {
                if (builder.length() != 0)
                    builder.append(' ');
                if (i < args.length) {
                    builder.append(ChatColor.stripColor(args[i]));
                }
                counter++;
            }
            return builder.toString();
        }

        /**
         * Returns the configuration values.
         *
         * @param configuration configuration
         * @return values
         */
        @SuppressWarnings("unchecked")
        private static Map<String, Object> getConfigValues(Object configuration) {
            final Map<String, Object> configurationMap;
            if (!(configuration instanceof Map)) {
                final MemorySection section = (MemorySection) configuration;
                configurationMap = section.getValues(true);
            } else {
                configurationMap = (Map<String, Object>) configuration;
            }
            return configurationMap;
        }

        /**
         * Registers the dynamic command.
         *
         * @param command command
         */
        private void registerDynamicCommand(String command) {
            try {
                final Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", getServerVersion()));
                final Object server = clazz.cast(Bukkit.getServer());
                final SimpleCommandMap map = (SimpleCommandMap) server.getClass().getDeclaredMethod("getCommandMap").invoke(server);
                map.register(command, this);
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot register dynamic command.", ex);
            }
        }

        /**
         * Returns the server version.
         *
         * @return version
         */
        private static String getServerVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        }
    }
}