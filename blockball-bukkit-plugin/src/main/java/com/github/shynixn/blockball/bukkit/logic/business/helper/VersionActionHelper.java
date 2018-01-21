package com.github.shynixn.blockball.bukkit.logic.business.helper;

import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.nms.VersionSupport;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * Compatibility modifications when using different server versions.
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
public class VersionActionHelper {

    /**
     * Returns the item in the hand of the player.
     *
     * @param player  player
     * @param offHand offHand
     * @return itemStack
     */
    public static ItemStack getItemInHand(Player player, boolean offHand) {
        if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
            try {
                if (offHand) {
                    return ReflectionUtils.invokeMethodByObject(player.getInventory(), "getItemInOffHand", new Class[]{}, new Object[]{});
                } else {
                    return ReflectionUtils.invokeMethodByObject(player.getInventory(), "getItemInMainHand", new Class[]{}, new Object[]{});
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to get item in hand.");
                throw new RuntimeException(e);
            }
        } else {
            try {
                return ReflectionUtils.invokeMethodByObject(player, "getItemInHand", new Class[]{}, new Object[]{});
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to get item in hand.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Sets the item in the hand of the player,
     *
     * @param player    player
     * @param itemStack itemstack
     * @param offHand   offHand
     */
    public static void setItemInHand(Player player, ItemStack itemStack, boolean offHand) {
        if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
            try {
                if (offHand) {
                    ReflectionUtils.invokeMethodByObject(player.getInventory(), "setItemInOffHand", new Class[]{ItemStack.class}, new Object[]{itemStack});
                } else {
                    ReflectionUtils.invokeMethodByObject(player.getInventory(), "setItemInMainHand", new Class[]{ItemStack.class}, new Object[]{itemStack});
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to set item in hand.");
                throw new RuntimeException(e);
            }
        } else {
            try {
                ReflectionUtils.invokeMethodByObject(player, "setItemInHand", new Class[]{ItemStack.class}, new Object[]{itemStack});
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to set item in hand.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Sets the given player glowing/notGlowing.
     *
     * @param player  player
     * @param glowing glowing
     */
    public static void setGlowing(Player player, boolean glowing) {
        if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
            try {
                ReflectionUtils.invokeMethodByObject(player, "setGlowing", new Class[]{boolean.class}, new Object[]{glowing});
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to set player glowing.", e);
            }
        }
    }

}
