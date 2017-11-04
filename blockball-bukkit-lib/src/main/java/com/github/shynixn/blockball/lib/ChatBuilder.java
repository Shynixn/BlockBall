package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * Copyright (c) 2017
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
public class ChatBuilder {

    private final List<Object> components = new ArrayList<>();

    /**
     * Adds a text
     *
     * @param text text
     * @return instance
     */
    public ChatBuilder text(String text) {
        if (text == null)
            throw new IllegalArgumentException("Text cannot be null!");
        this.components.add(text);
        return this;
    }

    /**
     * Creates a component for complex editing from the text and returns it for editing
     *
     * @param text text
     * @return created component
     */
    public Component component(String text) {
        if (text == null)
            throw new IllegalArgumentException("Text cannot be null!");
        final Component component = new Component(this, text);
        this.components.add(component);
        return component;
    }

    /**
     * Sets the text bold
     *
     * @return instance
     */
    public ChatBuilder bold() {
        this.components.add(ChatColor.BOLD);
        return this;
    }

    /**
     * NextLine
     *
     * @return instance
     */
    public ChatBuilder nextLine() {
        this.components.add("\n");
        return this;
    }

    /**
     * Sets the text italic
     *
     * @return instance
     */
    public ChatBuilder italic() {
        this.components.add(ChatColor.ITALIC);
        return this;
    }

    /**
     * Sets the text underlined
     *
     * @return instance
     */
    public ChatBuilder underline() {
        this.components.add(ChatColor.UNDERLINE);
        return this;
    }

    /**
     * Sets the text strikeThrough
     *
     * @return instance
     */
    public ChatBuilder strikeThrough() {
        this.components.add(ChatColor.STRIKETHROUGH);
        return this;
    }

    /**
     * Sets the current color
     *
     * @param chatColor color
     * @return instance
     */
    public ChatBuilder color(ChatColor chatColor) {
        if (chatColor == null)
            throw new IllegalArgumentException("ChatColor cannot be null");
        this.components.add(chatColor);
        return this;
    }

    /**
     * Resets the color
     *
     * @return instance
     */
    public ChatBuilder reset() {
        this.components.add(ChatColor.RESET);
        return this;
    }

    /**
     * Sends the built message to the given players
     *
     * @param players players
     */
    public void sendMessage(Collection<Player> players) {
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null");
        this.sendMessage(players.toArray(new Player[players.size()]));
    }

    /**
     * Sends the built message to the given players
     *
     * @param players players
     */
    public void sendMessage(Player... players) {
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null");
        final StringBuilder finalMessage = new StringBuilder();
        final StringBuilder cache = new StringBuilder();
        finalMessage.append("{\"text\": \"\"");
        finalMessage.append(", \"extra\" : [");
        boolean firstExtra = false;
        for (final Object component : this.components) {
            if (!(component instanceof ChatColor) && firstExtra) {
                finalMessage.append(", ");
            }
            if (component instanceof ChatColor) {
                cache.append(component);
            } else if (component instanceof String) {
                finalMessage.append("{\"text\": \"");
                finalMessage.append(ChatColor.translateAlternateColorCodes('&', cache.toString() + component));
                finalMessage.append("\"}");
                cache.setLength(0);
                firstExtra = true;
            } else {
                finalMessage.append(component);
                firstExtra = true;
            }
        }
        finalMessage.append("]}");
        try {
            final Class<?> clazz;
            if (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].equals("v1_8_R1")) {
                clazz = findClass("net.minecraft.server.VERSION.ChatSerializer");
            } else {
                clazz = findClass("net.minecraft.server.VERSION.IChatBaseComponent$ChatSerializer");
            }
            final Class<?> packetClazz = findClass("net.minecraft.server.VERSION.PacketPlayOutChat");
            final Class<?> chatBaseComponentClazz = findClass("net.minecraft.server.VERSION.IChatBaseComponent");
            final Object chatComponent = invokeMethod(null, clazz, "a", new Class[]{String.class}, new Object[]{finalMessage.toString()});
            final Object packet;
            if (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].equals("v1_12_R1")) {
                final Class<?> chatEnumMessage = findClass("net.minecraft.server.VERSION.ChatMessageType");
                packet = invokeConstructor(packetClazz, new Class[]{chatBaseComponentClazz, chatEnumMessage}, new Object[]{chatComponent, chatEnumMessage.getEnumConstants()[0]});
            } else {
                packet = invokeConstructor(packetClazz, new Class[]{chatBaseComponentClazz, byte.class}, new Object[]{chatComponent, (byte) 0});
            }
            for (final Player player : players) {
                sendPacket(player, packet);
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException | NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to send packet.", e);
        }
    }

    /**
     * Sends a packet to the client player
     *
     * @param player player
     * @param packet packet
     * @throws ClassNotFoundException    exception
     * @throws IllegalAccessException    exception
     * @throws NoSuchMethodException     exception
     * @throws InvocationTargetException exception
     * @throws NoSuchFieldException      exception
     */

    private static void sendPacket(Player player, Object packet) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        final Object craftPlayer = findClass("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer").cast(player);
        final Object entityPlayer = invokeMethod(craftPlayer, craftPlayer.getClass(), "getHandle", new Class[]{}, new Object[]{});
        final Field field = entityPlayer.getClass().getDeclaredField("playerConnection");
        field.setAccessible(true);
        final Object connection = field.get(entityPlayer);
        invokeMethod(connection, connection.getClass(), "sendPacket", new Class[]{packet.getClass().getInterfaces()[0]}, new Object[]{packet});
    }

    /**
     * Invokes a constructor by the given parameters
     *
     * @param clazz      clazz
     * @param paramTypes paramTypes
     * @param params     params
     * @return instance
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws InvocationTargetException exception
     * @throws InstantiationException    exception
     */
    private static Object invokeConstructor(Class<?> clazz, Class[] paramTypes, Object[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(params);
    }

    /**
     * Invokes a method by the given parameters
     *
     * @param instance   instance
     * @param clazz      clazz
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @return returnedObject
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     * @throws NoSuchMethodException     exception
     */
    private static Object invokeMethod(Object instance, Class<?> clazz, String name, Class[] paramTypes, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final Method method = clazz.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return method.invoke(instance, params);
    }

    /**
     * Finds a class regarding of the server Version
     *
     * @param name name
     * @return clazz
     * @throws ClassNotFoundException exception
     */
    private static Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name.replace("VERSION", Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]));
    }

    /**
     * Types of ClickAction
     */
    public enum ClickAction {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE,
    }

    public static class Component {
        private final StringBuilder text = new StringBuilder();
        private ChatColor color;
        private boolean bold;
        private boolean italic;
        private boolean underlined;
        private boolean strikethrough;
        private final ChatBuilder builder;

        private ClickAction clickAction;
        private String clickActionData;
        private Component hoverActionData;
        private final Component parentComponent;

        /**
         * Initializes a new component with the given builder and text
         *
         * @param builder builder
         * @param text    text
         */
        Component(ChatBuilder builder, String text) {
            this(builder, text, null);
        }

        /**
         * Initializes a new component with the given builder text and parentComponent
         *
         * @param builder   builder
         * @param text      text
         * @param component parent
         */
        Component(ChatBuilder builder, String text, Component component) {
            super();
            if (builder == null)
                throw new IllegalArgumentException("Builder cannot be null!");
            if (text == null)
                throw new IllegalArgumentException("Text cannot be null!");
            this.builder = builder;
            this.text.append(text);
            this.parentComponent = component;
        }

        /**
         * Returns the parent component. Returns null if there is no parent
         *
         * @return componend
         */
        public Component getParentComponent() {
            return this.parentComponent;
        }

        /**
         * Returns the builder of the component
         *
         * @return builder
         */
        public ChatBuilder builder() {
            return this.builder;
        }

        /**
         * Sets the text of the component
         *
         * @param text text
         * @return instance
         */
        public Component setText(String text) {
            if (text == null)
                throw new IllegalArgumentException("Text cannot be null!");
            this.text.setLength(0);
            this.text.append(text);
            return this;
        }

        /**
         * Sets the click Action of the component
         *
         * @param action action
         * @param text   text
         * @return instance
         */
        public Component setClickAction(ClickAction action, String text) {
            if (text == null)
                throw new IllegalArgumentException("Text cannot be null!");
            this.clickAction = action;
            this.clickActionData = text;
            return this;
        }

        /**
         * Sets the hover text of the component and returns the component for the hover-text
         *
         * @param text text
         * @return childComponent
         */
        public Component setHoverText(String text) {
            if (text == null)
                throw new IllegalArgumentException("Text cannot be null!");
            this.hoverActionData = new Component(this.builder, text, this);
            return this.hoverActionData;
        }

        /**
         * Returns the text
         *
         * @return text
         */
        public String getText() {
            return ChatColor.translateAlternateColorCodes('&', this.text.toString());
        }

        /**
         * Appends a text to the component
         *
         * @param text text
         * @return instance
         */
        public Component appendText(String text) {
            if (text == null)
                throw new IllegalArgumentException("Text cannot be null!");
            this.text.append(text);
            return this;
        }

        /**
         * Sets the component color
         *
         * @param color color
         * @return instance
         */
        public Component setColor(ChatColor color) {
            this.color = color;
            return this;
        }

        /**
         * Returns if the component is bold
         *
         * @return bold
         */
        public boolean isBold() {
            return this.bold;
        }

        /**
         * Sets the component bold
         *
         * @param bold bold
         * @return isBold
         */
        public Component setBold(boolean bold) {
            this.bold = bold;
            return this;
        }

        /**
         * Returns if the component is italic
         *
         * @return isItalic
         */
        public boolean isItalic() {
            return this.italic;
        }

        /**
         * Sets the component italic
         *
         * @param italic italic
         * @return instance
         */
        public Component setItalic(boolean italic) {
            this.italic = italic;
            return this;
        }

        /**
         * Returns if the component isUnderlined
         *
         * @return isUnderLined
         */
        public boolean isUnderlined() {
            return this.underlined;
        }

        /**
         * Sets the component underLined
         *
         * @param underlined underLines
         * @return instance
         */
        public Component setUnderlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        /**
         * Returns if the component isStrikeThrough
         *
         * @return isStrikeTrough
         */
        public boolean isStrikethrough() {
            return this.strikethrough;
        }

        /**
         * Sets the component strikeThrough
         *
         * @param strikethrough strikeThrough
         * @return instance
         */
        public Component setStrikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
            return this;
        }

        /**
         * Returns a string representation of the object. In general, the
         * {@code toString} method returns a string that
         * "textually represents" this object. The result should
         * be a concise but informative representation that is easy for a
         * person to read.
         * It is recommended that all subclasses override this method.
         *
         * @return a string representation of the object.
         */
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("{ \"text\": \"");
            builder.append(ChatColor.translateAlternateColorCodes('&', this.text.toString()));
            builder.append('"');
            if (this.color != null) {
                builder.append(", \"color\": \"");
                builder.append(this.color.name().toLowerCase());
                builder.append('"');
            }
            if (this.bold) {
                builder.append(", \"bold\": \"");
                builder.append(this.bold);
                builder.append('"');
            }
            if (this.italic) {
                builder.append(", \"italic\": \"");
                builder.append(this.italic);
                builder.append('"');
            }
            if (this.underlined) {
                builder.append(", \"underlined\": \"");
                builder.append(this.underlined);
                builder.append('"');
            }
            if (this.strikethrough) {
                builder.append(", \"strikethrough\": \"");
                builder.append(this.strikethrough);
                builder.append('"');
            }
            if (this.clickAction != null) {
                builder.append(", \"clickEvent\": {\"action\": \"");
                builder.append(this.clickAction.name().toLowerCase());
                builder.append("\" , \"value\" : \"");
                builder.append(this.clickActionData);
                builder.append("\"}");
            }
            if (this.hoverActionData != null) {
                builder.append(", \"hoverEvent\": {\"action\": \"");
                builder.append("show_text");
                builder.append("\" , \"value\" : ");
                builder.append(this.hoverActionData.toString());
                builder.append('}');
            }
            builder.append('}');
            return builder.toString();
        }
    }
}
