package com.github.shynixn.blockball.business.bungee.connector;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Created by Shynixn
 */
class BungeeCordProvider implements PluginMessageListener {
    private final BungeeCordController controller;
    private final JavaPlugin plugin;
    private final CallBack callBack;

    BungeeCordProvider(BungeeCordController controller, JavaPlugin plugin, CallBack callBack) {
        this.controller = controller;
        this.plugin = plugin;
        this.callBack = callBack;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    void ping() {
        final Player player = this.getFirstPlayer();
        if (player == null)
            return;
        for (final String s : this.controller.getServers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerIP");
            out.writeUTF(s);
            player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
        }
    }

    void connectToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String type = in.readUTF();
        if (type.equals("ServerIP")) {
            final String serverName = in.readUTF();
            final String ip = in.readUTF();
            final short port = in.readShort();
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    final String data = BungeeCordProvider.this.receiveResultFromServer(serverName, ip, port);
                    BungeeCordProvider.this.parseData(serverName, data);
                }
            });
        }
    }

    private void parseData(String serverName, String data) {
        try {
            if (data == null)
                return;
            final ServerInfo serverInfo = new ServerInfo.Container(serverName, data);
            this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable() {
                @Override
                public void run() {
                    BungeeCordProvider.this.callBack.run(serverInfo);
                }
            });
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot parse result from server.", e);
        }
    }

    interface CallBack {
        void run(ServerInfo serverInfo);
    }

    private Player getFirstPlayer() {
        for (final World world : Bukkit.getWorlds()) {
            if (!world.getPlayers().isEmpty())
                return world.getPlayers().get(0);
        }
        return null;
    }

    private String receiveResultFromServer(String serverName, String hostname, int port) {
        String data = null;
        try (Socket socket = new Socket(hostname, port)) {
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                out.write(0xFE);
                try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
                    final StringBuilder buffer = new StringBuilder();
                    int b;
                    while ((b = in.read()) != -1) {
                        if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                            buffer.append((char) b);
                        }
                    }
                    data = buffer.toString();
                }
            }
        } catch (final Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(this.controller.PREFIX + ChatColor.RED + "Failed to reach server " + serverName + " (" + hostname + ':' + port + ").");
        }
        return data;
    }
}
