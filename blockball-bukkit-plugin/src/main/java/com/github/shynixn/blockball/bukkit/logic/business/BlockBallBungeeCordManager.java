package com.github.shynixn.blockball.bukkit.logic.business;

import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectionController;
import com.github.shynixn.blockball.api.persistence.controller.LinkSignController;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
public class BlockBallBungeeCordManager implements AutoCloseable{
    public Map<Player, String> signPlacementCache = new HashMap<>();


    private LinkSignController signController;
    private BungeeCordConnectionController connectController;

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
      //  this.signController.close();
    }

    /**
     * Returns the bungeeCord sign Controller.
     * @return controller
     */
    public LinkSignController getBungeeCordSignController() {
        return this.signController;
    }

    /**
     * Returns the bungeeCordConnectController.
     * @return controller
     */
    public BungeeCordConnectionController getBungeeCordConnectController() {
        return this.connectController;
    }




/*
    void setMotd(String motd) {
        try {
            motd = motd.replace("[", "").replace("]", "");
            final Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", BungeeCord.getServerVersion()));
            Object obj = clazz.cast(Bukkit.getServer());
            obj = BungeeCord.invokeMethodByObject(obj, "getServer");
            BungeeCord.invokeMethodByObject(obj, "setMotd", '[' + motd + ChatColor.RESET + ']');
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot set motd.", ex);
        }
    }


    public static Object invokeMethodByObject(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                } catch (final Exception ex) {
                    Bukkit.getLogger().log(Level.WARNING, "Cannot invoke Method.", ex);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    public static String getServerVersion() {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (final Exception ex) {
            throw new RuntimeException("Version not found!");
        }
    }

    private final BungeeCordSignController signController;


    public static boolean isMinigameModeEnabled() {
        return ENABLED && !SIGN_MODE;
    }

    public static boolean isSignModeEnabled() {
        return ENABLED && SIGN_MODE;
    }

    public static void setModt(String modt) {
        if (isMinigameModeEnabled()) {
            minigameListener.setMotd(modt);
        }
    }

    public BlockBallBungeeCordManager() {
        this.signController = new
    }

    /**
     * Returns the sign controller
     *
     * @return controller

    public BungeeCordSignController getSignController() {
        return this.signController;
    }


     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     *
     * @throws Exception if this resource cannot be closed

    @Override
    public void close() throws Exception {

    }*/
}
