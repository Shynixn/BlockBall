package com.github.shynixn.blockball.bukkit.nms.v1_9_R1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class Interpreter19 {
    public static ItemStack getItemInHand19(Player player, boolean offHand) {
        if (!ReflectionLib.getServerVersion().equals("1_9_R1")
                && !ReflectionLib.getServerVersion().equals("1_9_R2")
                && !ReflectionLib.getServerVersion().equals("v1_10_R1")
                && !ReflectionLib.getServerVersion().equals("v1_11_R1")
                && !ReflectionLib.getServerVersion().equals("v1_12_R1")) {
            return (ItemStack) ReflectionLib.invokeMethodByObject(player, "getItemInHand");
        } else {
            if (offHand) {
                return (ItemStack) ReflectionLib.invokeMethodByObject(player.getInventory(), "getItemInOffHand");
            } else {
                return (ItemStack) ReflectionLib.invokeMethodByObject(player.getInventory(), "getItemInMainHand");
            }
        }
    }

    public static void setGlowing(Player player, boolean glowing)
    {
        if (ReflectionLib.getServerVersion().equals("1_9_R1")
                || ReflectionLib.getServerVersion().equals("1_9_R2")
                || ReflectionLib.getServerVersion().equals("v1_10_R1")
                || ReflectionLib.getServerVersion().equals("v1_11_R1")
                || ReflectionLib.getServerVersion().equals("v1_12_R1")) {
             ReflectionLib.invokeMethodByObject(player, "setGlowing", glowing);
        }
    }

    public static void setItemInHand19(Player player, ItemStack itemStack, boolean offHand) {
        if (!ReflectionLib.getServerVersion().equals("1_9_R1")
                && !ReflectionLib.getServerVersion().equals("1_9_R2")
                && !ReflectionLib.getServerVersion().equals("v1_10_R1")
                && !ReflectionLib.getServerVersion().equals("v1_11_R1")
                && !ReflectionLib.getServerVersion().equals("v1_12_R1")) {
            ReflectionLib.invokeMethodByObject(player, "setItemInHand", itemStack);
        } else {
            if (offHand) {
                ReflectionLib.invokeMethodByObject(player.getInventory(), "setItemInOffHand", itemStack);
            } else {
                ReflectionLib.invokeMethodByObject(player.getInventory(), "setItemInMainHand", itemStack);
            }
        }
    }
}
