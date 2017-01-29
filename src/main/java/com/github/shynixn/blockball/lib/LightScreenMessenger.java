package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * Created by Shynixn
 */
public interface LightScreenMessenger {
    void setActionBar(Player player, String text);

    void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    void setTabHeaderFooter(Player player, String headerText, String footerText);

    class Builder implements LightScreenMessenger {
        private static Builder instance;
        private final LightScreenMessenger holder;

        private Builder() {
            this.holder = invokeCustomNMS();
        }

        public static LightScreenMessenger getInstance() {
            if (instance == null)
                instance = new Builder();
            return instance;
        }

        @Override
        public void setActionBar(Player player, String text) {
            this.holder.setActionBar(player, text);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            this.holder.setPlayerTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }

        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            this.holder.setTabHeaderFooter(player, headerText, footerText);
        }

        private static LightScreenMessenger invokeCustomNMS() {
            try {
                final Class<?> clazz = Class.forName(LightScreenMessenger.class.getName() + "$Container_VERSION".replace("VERSION", getServerVersion()));
                final Constructor constructor = clazz.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                return (LightScreenMessenger) constructor.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot start screenmessenger", e);
                return null;
            }
        }

        private static String getServerVersion() {
            try {
                return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            } catch (Exception ex) {
                throw new RuntimeException("Version not found!");
            }
        }

        private static void hookPacket(Object packet, Object header, Object footer) {
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
                Bukkit.getLogger().log(Level.WARNING, "Cannot hook packet.", e);
            }
        }
    }

    class Container_v1_8_R1 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_8_R1.IChatBaseComponent cbc = net.minecraft.server.v1_8_R1.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_8_R1.PacketPlayOutChat poc = new net.minecraft.server.v1_8_R1.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R1.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', title) + "'}");
            final net.minecraft.server.v1_8_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R1.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', subtitle) + "'}");
            final net.minecraft.server.v1_8_R1.Packet length = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R1.Packet titlePacket = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R1.Packet subtitlePacket = new net.minecraft.server.v1_8_R1.PacketPlayOutTitle(net.minecraft.server.v1_8_R1.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }


        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R1.IChatBaseComponent header = net.minecraft.server.v1_8_R1.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', headerText) + "'}");
            final net.minecraft.server.v1_8_R1.IChatBaseComponent footer = net.minecraft.server.v1_8_R1.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', footerText) + "'}");
            final net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_8_R2 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_8_R2.IChatBaseComponent cbc = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_8_R2.PacketPlayOutChat poc = new net.minecraft.server.v1_8_R2.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R2.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R2.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', title) + "'}");
            final net.minecraft.server.v1_8_R2.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', subtitle) + "'}");
            final net.minecraft.server.v1_8_R2.Packet length = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R2.Packet titlePacket = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R2.Packet subtitlePacket = new net.minecraft.server.v1_8_R2.PacketPlayOutTitle(net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }


        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R2.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R2.IChatBaseComponent header = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', headerText) + "'}");
            final net.minecraft.server.v1_8_R2.IChatBaseComponent footer = net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', footerText) + "'}");
            final net.minecraft.server.v1_8_R2.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R2.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_8_R3 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_8_R3.IChatBaseComponent cbc = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_8_R3.PacketPlayOutChat poc = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R3.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R3.IChatBaseComponent titleJSON = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', title) + "'}");
            final net.minecraft.server.v1_8_R3.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{'text': '" + ChatColor.translateAlternateColorCodes('&', subtitle) + "'}");
            final net.minecraft.server.v1_8_R3.Packet length = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R3.Packet titlePacket = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_8_R3.Packet subtitlePacket = new net.minecraft.server.v1_8_R3.PacketPlayOutTitle(net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }


        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player;
            final net.minecraft.server.v1_8_R3.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_8_R3.IChatBaseComponent header = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', headerText) + "'}");
            final net.minecraft.server.v1_8_R3.IChatBaseComponent footer = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{'color': '', 'text': '" + ChatColor.translateAlternateColorCodes('&', footerText) + "'}");
            final net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_9_R1 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_9_R1.IChatBaseComponent cbc = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_9_R1.PacketPlayOutChat poc = new net.minecraft.server.v1_9_R1.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_9_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_9_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_9_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_9_R1.Packet length = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R1.Packet titlePacket = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R1.Packet subtitlePacket = new net.minecraft.server.v1_9_R1.PacketPlayOutTitle(net.minecraft.server.v1_9_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }


        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_9_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_9_R1.IChatBaseComponent header = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_9_R1.IChatBaseComponent footer = net.minecraft.server.v1_9_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_9_R1.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_9_R2 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_9_R2.IChatBaseComponent cbc = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_9_R2.PacketPlayOutChat poc = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player;
            final net.minecraft.server.v1_9_R2.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_9_R2.IChatBaseComponent titleJSON = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_9_R2.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_9_R2.Packet length = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R2.Packet titlePacket = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_9_R2.Packet subtitlePacket = new net.minecraft.server.v1_9_R2.PacketPlayOutTitle(net.minecraft.server.v1_9_R2.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }

        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player;
            final net.minecraft.server.v1_9_R2.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_9_R2.IChatBaseComponent header = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_9_R2.IChatBaseComponent footer = net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_9_R2.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_9_R2.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_10_R1 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_10_R1.IChatBaseComponent cbc = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_10_R1.PacketPlayOutChat poc = new net.minecraft.server.v1_10_R1.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_10_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_10_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_10_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_10_R1.Packet length = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_10_R1.Packet titlePacket = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_10_R1.Packet subtitlePacket = new net.minecraft.server.v1_10_R1.PacketPlayOutTitle(net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }


        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_10_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_10_R1.IChatBaseComponent header = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_10_R1.IChatBaseComponent footer = net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }

    class Container_v1_11_R1 implements LightScreenMessenger {
        @Override
        public void setActionBar(Player player, String text) {
            final net.minecraft.server.v1_11_R1.IChatBaseComponent cbc = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            final net.minecraft.server.v1_11_R1.PacketPlayOutChat poc = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(cbc, (byte) 2);
            ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(poc);
        }

        @Override
        public void setPlayerTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
            final org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_11_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_11_R1.IChatBaseComponent titleJSON = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            final net.minecraft.server.v1_11_R1.IChatBaseComponent subtitleJSON = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            final net.minecraft.server.v1_11_R1.Packet length = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.TIMES, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_11_R1.Packet titlePacket = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
            final net.minecraft.server.v1_11_R1.Packet subtitlePacket = new net.minecraft.server.v1_11_R1.PacketPlayOutTitle(net.minecraft.server.v1_11_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay, fadeOut);
            if (!title.isEmpty()) {
                connection.sendPacket(titlePacket);
                connection.sendPacket(length);
            }
            if (!subtitle.isEmpty()) {
                connection.sendPacket(subtitlePacket);
            }
        }

        @Override
        public void setTabHeaderFooter(Player player, String headerText, String footerText) {
            final org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player;
            final net.minecraft.server.v1_11_R1.PlayerConnection connection = craftPlayer.getHandle().playerConnection;
            final net.minecraft.server.v1_11_R1.IChatBaseComponent header = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', headerText) + "\"}");
            final net.minecraft.server.v1_11_R1.IChatBaseComponent footer = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"color\": \"\", \"text\": \"" + ChatColor.translateAlternateColorCodes('&', footerText) + "\"}");
            final net.minecraft.server.v1_11_R1.PacketPlayOutPlayerListHeaderFooter packet = new net.minecraft.server.v1_11_R1.PacketPlayOutPlayerListHeaderFooter();
            Builder.hookPacket(packet, header, footer);
            connection.sendPacket(packet);
        }
    }
}
