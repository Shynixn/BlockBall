package com.github.shynixn.blockball.bukkit.dependencies.bossbar;

import com.github.shynixn.blockball.bukkit.logic.business.helper.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
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
public final class BossBarConnection {
    /**
     * Initialize.
     */
    private BossBarConnection() {
        super();
    }

    /**
     * Updates the bossBar of the BossBar plugin.
     *
     * @param player  player
     * @param message message
     */
    public static void updateBossBar(Player player, String message, float percentage) {
        try {
            ReflectionUtils.invokeMethodByClass(ReflectionUtils.invokeClass("org.inventivetalent.bossbar.BossBarAPI"), "setMessage", new Class[]{Player.class, String.class, float.class}, new Object[]{player, message, percentage});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to update Bossbar.", e);
        }
    }

    /**
     * Removes the bossBar of the BossBar plugin.
     *
     * @param player player
     */
    public static void removeBossBar(Player player) {
        try {
            final Class<?> clazz = ReflectionUtils.invokeClass("org.inventivetalent.bossbar.BossBarAPI");
            if (ReflectionUtils.invokeMethodByClass(clazz, "hasBar", new Class[]{Player.class}, new Object[]{player})) {
                ReflectionUtils.invokeMethodByClass(clazz, "removeBar", new Class[]{Player.class}, new Object[]{player});
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to remove Bossbar.", e);
        }
    }
}
