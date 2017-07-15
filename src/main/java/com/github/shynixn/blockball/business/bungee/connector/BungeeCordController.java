package com.github.shynixn.blockball.business.bungee.connector;

import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class BungeeCordController implements BungeeCordProvider.CallBack {
    public final String PREFIX;
    private final JavaPlugin plugin;
    private final BungeeCordProvider provider;

    Map<Player, String> lastServer = new HashMap<>();
    List<BungeeCordSignInfo> signs = new ArrayList<>();

    public BungeeCordController(JavaPlugin plugin, String prefix) {
        super();
        this.PREFIX = prefix;
        this.plugin = plugin;
        this.provider = new BungeeCordProvider(this, plugin, this);
        new BungeeCordListener(this, plugin);
        new BungeeCordCommandExecutor(this);
        this.load(plugin);
        this.run(plugin);
    }

    void connect(Player player, String serverName) {
        this.provider.connectToServer(player, serverName);
    }

    private void run(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, BungeeCordController.this.provider::ping, 0L, 10L);
    }

    public void add(String server, Location location) {
        final BungeeCordSignInfo info = new BungeeCordSignInfo.Container(location, server);
        this.signs.add(info);
        final BungeeCordSignInfo[] signInfos = this.signs.toArray(new BungeeCordSignInfo[this.signs.size()]);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final FileConfiguration configuration = new YamlConfiguration();
                final File file = new File(BungeeCordController.this.plugin.getDataFolder(), "bungeecord_signs.yml");
                if (file.exists()) {
                    if (!file.delete()) {
                        Bukkit.getLogger().log(Level.WARNING, "File cannot get deleted.");
                    }
                }
                for (int i = 0; i < signInfos.length; i++) {
                    configuration.set("signs." + i, signInfos[i].serialize());
                }
                configuration.save(file);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Save sign location.", e);
            }
        });
    }

    @Override
    public void run(ServerInfo serverInfo) {
        for (final BungeeCordSignInfo signInfo : this.signs.toArray(new BungeeCordSignInfo[this.signs.size()])) {
            if (signInfo.getServer().equals(serverInfo.getServerName())) {
                final Location location = signInfo.getLocation();
                if (location.getBlock().getState() instanceof Sign) {
                    this.updateSign((Sign) location.getBlock().getState(), serverInfo);
                } else {
                    this.signs.remove(signInfo);
                }
            }
        }
    }

    Set<String> getServers() {
        final Set<String> server = new HashSet<>();
        for (final BungeeCordSignInfo signInfo : this.signs) {
            server.add(signInfo.getServer());
        }
        return server;
    }

    void updateSign(Sign sign, ServerInfo info) {
        sign.setLine(0, this.replaceSign(BungeeCord.SIGN_LINE_1, info));
        sign.setLine(1, this.replaceSign(BungeeCord.SIGN_LINE_2, info));
        sign.setLine(2, this.replaceSign(BungeeCord.SIGN_LINE_3, info));
        sign.setLine(3, this.replaceSign(BungeeCord.SIGN_LINE_4, info));
        sign.update();
    }

    private void load(JavaPlugin plugin) {
        try {
            final FileConfiguration configuration = new YamlConfiguration();
            final File file = new File(plugin.getDataFolder(), "bungeecord_signs.yml");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Bukkit.getLogger().log(Level.WARNING, "File cannot get created.");
                }
            }
            configuration.load(file);
            if (configuration.getConfigurationSection("signs") != null) {
                final Map<String, Object> data = configuration.getConfigurationSection("signs").getValues(false);
                for (final String s : data.keySet()) {
                    this.signs.add(new BungeeCordSignInfo.Container(((ConfigurationSection) data.get(s)).getValues(true)));
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Save load location.", e);
        }
    }

    private String replaceSign(String line, ServerInfo info) {
        line = line.replace("<maxplayers>", String.valueOf(info.getMaxPlayerAmount()*2));
        line = line.replace("<players>", String.valueOf(info.getPlayerAmount()));
        line = line.replace("<server>", info.getServerName());
        if (info.getState() == ServerInfo.State.INGAME)
            line = line.replace("<state>", BungeeCord.SIGN_INGAME);
        else if (info.getState() == ServerInfo.State.RESTARTING)
            line = line.replace("<state>", BungeeCord.SIGN_RESTARTING);
        else if (info.getState() == ServerInfo.State.WAITING_FOR_PLAYERS)
            line = line.replace("<state>", BungeeCord.SIGN_WAITING_FOR_PLAYERS);
        else if (info.getState() == ServerInfo.State.UNKNOWN)
            line = line.replace("<state>", "UNKNOWN");
        return line;
    }
}
