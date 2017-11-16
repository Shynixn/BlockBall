package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2016
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
public class ScreenUtils {

    /**
     * Initializes a new instance of screenUtils
     */
    private ScreenUtils() {
        super();
    }

    /**
     * Sets the action bar for the given players
     *
     * @param message message
     * @param players players
     * @return hasBeenSend
     */
    public static boolean setActionBar(String message, Player... players) {
        if (message == null)
            throw new IllegalArgumentException("Message cannot be null!");
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null!");
        switch (getServerVersion()) {
            case "v1_12_R1":
                ScreenUtils12R1.setActionBar(players, message);
                break;
            case "v1_11_R1":
                ScreenUtils11R1.setActionBar(players, message);
                break;
            case "v1_10_R1":
                ScreenUtils10R1.setActionBar(players, message);
                break;
            case "v1_9_R2":
                ScreenUtils9R2.setActionBar(players, message);
                break;
            case "v1_9_R1":
                ScreenUtils9R1.setActionBar(players, message);
                break;
            case "v1_8_R3":
                ScreenUtils8R3.setActionBar(players, message);
                break;
            case "v1_8_R2":
                ScreenUtils8R2.setActionBar(players, message);
                break;
            case "v1_8_R1":
                ScreenUtils8R1.setActionBar(players, message);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
     *
     * @param title    title
     * @param subTitle subTitle
     * @param fadeIn   fadeIn
     * @param stay     stay
     * @param fadeOut  fadeOut
     * @param players  players
     * @return hasBeenSend
     */
    public static boolean setTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut, Collection<Player> players) {
        return setTitle(title, subTitle, fadeIn, stay, fadeOut, players.toArray(new Player[players.size()]));
    }

    /**
     * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
     *
     * @param title    title
     * @param subTitle subTitle
     * @param fadeIn   fadeIn
     * @param stay     stay
     * @param fadeOut  fadeOut
     * @param players  players
     * @return hasBeenSend
     */
    public static boolean setTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut, Player... players) {
        if (title == null)
            throw new IllegalArgumentException("Title cannot be null!");
        if (subTitle == null)
            throw new IllegalArgumentException("Subtitle cannot be null!");
        if (fadeIn < 0)
            throw new IllegalArgumentException("Fadein cannot be less than 0!");
        if (fadeOut < 0)
            throw new IllegalArgumentException("Fadein cannot be less than 0!");
        if (stay < 0)
            throw new IllegalArgumentException("Fadein cannot be less than 0!");
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null!");
        switch (getServerVersion()) {
            case "v1_12_R1":
                ScreenUtils12R1.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_11_R1":
                ScreenUtils11R1.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_10_R1":
                ScreenUtils10R1.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_9_R2":
                ScreenUtils9R2.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_9_R1":
                ScreenUtils9R1.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_8_R3":
                ScreenUtils8R3.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_8_R2":
                ScreenUtils8R2.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            case "v1_8_R1":
                ScreenUtils8R1.setTitle(players, title, subTitle, fadeIn, stay, fadeOut);
                break;
            default:
                return false;
        }
        return true;
    }

    /***
     * Sets the tabBar header and footer for the given players
     *
     * @param header  header
     * @param footer  footer
     * @param players players
     * @return hasBeenSend
     */
    public static boolean setTabBar(String header, String footer, Player... players) {
        if (header == null)
            throw new IllegalArgumentException("Header cannot be null!");
        if (footer == null)
            throw new IllegalArgumentException("Footer cannot be null!");
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null!");
        switch (getServerVersion()) {
            case "v1_12_R1":
                ScreenUtils12R1.setTabBar(players, header, footer);
                break;
            case "v1_11_R1":
                ScreenUtils11R1.setTabBar(players, header, footer);
                break;
            case "v1_10_R1":
                ScreenUtils10R1.setTabBar(players, header, footer);
                break;
            case "v1_9_R2":
                ScreenUtils9R2.setTabBar(players, header, footer);
                break;
            case "v1_9_R1":
                ScreenUtils9R1.setTabBar(players, header, footer);
                break;
            case "v1_8_R3":
                ScreenUtils8R3.setTabBar(players, header, footer);
                break;
            case "v1_8_R2":
                ScreenUtils8R2.setTabBar(players, header, footer);
                break;
            case "v1_8_R1":
                ScreenUtils8R1.setTabBar(players, header, footer);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Returns the server version.
     *
     * @return version
     */
    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    /**
     * Sets the packet fields for the tab packet
     *
     * @param packet packet
     * @param header header
     * @param footer footer
     */
    private static void setPacketField(Object packet, Object header, Object footer) {
        try {
            final Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, header);
            headerField.setAccessible(!headerField.isAccessible());

            final Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footer);
            footerField.setAccessible(!footerField.isAccessible());
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot set header/footer.", e);
        }
    }

    static class ScreenUtils8R1 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_8_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_8_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R1.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_8_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_8_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_8_R1.Packet length = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R1.Packet titlePacket = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R1.Packet subtitlePacket = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_8_R1.IChatBaseComponent header = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_8_R1.IChatBaseComponent footer = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils8R2 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_8_R2.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_8_R2.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R2.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_8_R2.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_8_R2.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_8_R2.Packet length = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R2.Packet titlePacket = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R2.Packet subtitlePacket = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_8_R2.IChatBaseComponent header = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_8_R2.IChatBaseComponent footer = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_8_R2.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R2.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils8R3 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_8_R3.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_8_R3.PacketPlayOutChat packet = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_8_R3.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_8_R3.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_8_R3.Packet length = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R3.Packet titlePacket = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R3.Packet subtitlePacket = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_8_R3.IChatBaseComponent header = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_8_R3.IChatBaseComponent footer = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils9R1 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_9_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_9_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_9_R1.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_9_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_9_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_9_R1.Packet length = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R1.Packet titlePacket = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R1.Packet subtitlePacket = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_9_R1.IChatBaseComponent header = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_9_R1.IChatBaseComponent footer = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils9R2 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_9_R2.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_9_R2.PacketPlayOutChat packet = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_9_R2.IChatBaseComponent titleJSON = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_9_R2.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_9_R2.Packet length = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R2.Packet titlePacket = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R2.Packet subtitlePacket = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_9_R2.IChatBaseComponent header = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_9_R2.IChatBaseComponent footer = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_9_R2.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_9_R2.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils10R1 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_10_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_10_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_10_R1.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_10_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_10_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_10_R1.Packet length = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_10_R1.Packet titlePacket = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_10_R1.Packet subtitlePacket = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_10_R1.IChatBaseComponent header = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_10_R1.IChatBaseComponent footer = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils11R1 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_11_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_11_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(chatBaseComponent, (byte) 2);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_11_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_11_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_11_R1.Packet length = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_11_R1.Packet titlePacket = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_11_R1.Packet subtitlePacket = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_11_R1.IChatBaseComponent header = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_11_R1.IChatBaseComponent footer = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_11_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_11_R1.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    static class ScreenUtils12R1 {
        /**
         * Sets the action bar for the given players
         *
         * @param players players
         * @param message message
         */
        static void setActionBar(Player[] players, String message) {
            final net.minecraft.server.v1_12_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
            final net.minecraft.server.v1_12_R1.PacketPlayOutChat packet = new net.minecraft.server.v1_12_R1.PacketPlayOutChat(chatBaseComponent, net.minecraft.server.v1_12_R1.ChatMessageType.a((byte) 2));
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }

        /**
         * Sets the title and subtitle for the given players for a given time with fadeIn, stay and fadeOut animations
         *
         * @param players  players
         * @param title    title
         * @param subtitle subtitle
         * @param fadeIn   fadeIn
         * @param stay     stay
         * @param fadeOut  fadeOut
         */
        static void setTitle(Player[] players, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final net.minecraft.server.v1_12_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_12_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_12_R1.Packet length = new net.minecraft.server.v1_12_R1.PacketPlayOutTitle(net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_12_R1.Packet titlePacket = new net.minecraft.server.v1_12_R1.PacketPlayOutTitle(net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_12_R1.Packet subtitlePacket = new net.minecraft.server.v1_12_R1.PacketPlayOutTitle(net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            for (final Player player : players) {
                if (!title.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                }
                if (!subtitle.isEmpty()) {
                    ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitlePacket);
                }
            }
        }

        /**
         * Sets the tabBar header and footer for the given players
         *
         * @param players    players
         * @param headerText headerText
         * @param footerText footerText
         */
        static void setTabBar(Player[] players, String headerText, String footerText) {
            final net.minecraft.server.v1_12_R1.IChatBaseComponent header = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_12_R1.IChatBaseComponent footer = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter();
            setPacketField(packet, header, footer);
            for (final Player player : players) {
                ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }
}