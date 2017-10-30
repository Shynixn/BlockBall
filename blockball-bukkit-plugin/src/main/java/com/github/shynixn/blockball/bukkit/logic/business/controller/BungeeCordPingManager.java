package com.github.shynixn.blockball.bukkit.logic.business.controller;

import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController;
import com.github.shynixn.blockball.api.business.entity.BungeeCordServerStatus;
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordSign;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallBungeeCordManager;
import com.github.shynixn.blockball.bukkit.logic.business.entity.BungeeCordServerStats;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.logging.Level;

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
public class BungeeCordPingManager implements Runnable, AutoCloseable, PluginMessageListener {
    private final BukkitTask task;
    private Plugin plugin;
    private BlockBallBungeeCordManager blockBallBungeeCordManager;
    private final BungeeCordSignController signController;

    public BungeeCordPingManager(BungeeCordSignController signController, Plugin plugin) {
        this.signController = signController;
        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 10L);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.pingServers();
    }

    /**
     * Connects the given player to the given server.
     *
     * @param player     player
     * @param serverName serverName
     */
    public void connectToServer(Player player, String serverName) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
    }

    /**
     * Pings all servers which are present inside the signController.
     */
    public void pingServers() {
        final Player player = this.getFirstPlayer();
        if (player == null)
            return;
        for (final String s : this.signController.getAllServers()) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerIP");
            out.writeUTF(s);
            player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals("BungeeCord")) {
            return;
        }
        final ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        final String type = in.readUTF();
        if (type.equals("ServerIP")) {
            final String serverName = in.readUTF();
            final String ip = in.readUTF();
            final short port = in.readShort();
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                final String data = this.receiveResultFromServer(serverName, ip, port);
                this.parseData(serverName, data);
            });
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.task.cancel();
    }

    private void updateSigns(BungeeCordServerStatus status) {
        for (final BungeeCordSign signInfo : this.signController.getAll()) {
            if (signInfo.getServer().equals(status.getServerName())) {
                final Location location = (Location) signInfo.getLocation();
                if (location.getBlock().getState() instanceof Sign) {
                    final Sign sign = (Sign) location.getBlock().getState();
               /*     sign.setLine(0, this.replaceSign(BungeeCord.SIGN_LINE_1, status));
                    sign.setLine(1, this.replaceSign(BungeeCord.SIGN_LINE_2, status));
                    sign.setLine(2, this.replaceSign(BungeeCord.SIGN_LINE_3, status));
                    sign.setLine(3, this.replaceSign(BungeeCord.SIGN_LINE_4, status));*/
                    sign.update();
                } else {
                    this.signController.remove(signInfo);
                }
            }
        }
    }

    private void parseData(String serverName, String data) {
        try {
            if (data == null)
                return;
            final BungeeCordServerStatus serverInfo = BungeeCordServerStats.from(serverName, data);
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.updateSigns(serverInfo));
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot parse result from server.", e);
        }
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
            BlockBallPlugin.logger().log(Level.WARNING, "Failed to reach server " + serverName + " (" + hostname + ':' + port + ").");
        }
        return data;
    }

    private String replaceSign(String line, BungeeCordServerStatus info) {
        String customLine = line.replace("<maxplayers>", String.valueOf(info.getMaxPlayerAmount() * 2))
                .replace("<players>", String.valueOf(info.getPlayerAmount()))
                .replace("<server>", info.getServerName());
    /*    if (info.getStatus() == BungeeCordServerState.INGAME)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_INGAME);
        else if (info.getStatus() == BungeeCordServerState.RESTARTING)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_RESTARTING);
        else if (info.getStatus() == BungeeCordServerState.WAITING_FOR_PLAYERS)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_WAITING_FOR_PLAYERS);
        else if (info.getStatus() == BungeeCordServerState.UNKNOWN)
            customLine = customLine.replace("<state>", "UNKNOWN");*/
        return customLine;
    }

    /**
     * Returns a nullable player .
     *
     * @return player
     */
    private Player getFirstPlayer() {
        for (final World world : Bukkit.getWorlds()) {
            if (!world.getPlayers().isEmpty())
                return world.getPlayers().get(0);
        }
        return null;
    }
}
