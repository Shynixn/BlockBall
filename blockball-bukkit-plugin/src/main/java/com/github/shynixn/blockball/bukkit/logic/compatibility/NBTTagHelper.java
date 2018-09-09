package com.github.shynixn.blockball.bukkit.logic.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
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
public class NBTTagHelper {

    /**
     * Sets the itemStack nbt Tags.
     * @param itemStack itemStack
     * @param nbtTags nbtTags
     * @return itemStack
     */
    public static ItemStack setItemStackNBTTag(ItemStack itemStack, Map<String, Object> nbtTags) {
        if(itemStack == null)
            throw new IllegalArgumentException("Itemstack cannot be null!");
        if(nbtTags == null)
            throw new IllegalArgumentException("Nbt tags cannot be null");
        try {
            final Method nmsCopyMethod = createClass("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);

            final Class<?> nbtTagClass = createClass("net.minecraft.server.VERSION.NBTTagCompound");
            final Class<?> nmsItemStackClass = createClass("net.minecraft.server.VERSION.ItemStack");
            final Method bukkitCopyMethod = createClass("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", nmsItemStackClass);
            final Method getNBTTag = nmsItemStackClass.getDeclaredMethod("getTag");
            final Method setNBTTag = nmsItemStackClass.getDeclaredMethod("setTag", nbtTagClass);
            final Object nmsItemStack = nmsCopyMethod.invoke(null, itemStack);

            final Method nbtSetString = nbtTagClass.getDeclaredMethod("setString", String.class, String.class);
            final Method nbtSetBoolean = nbtTagClass.getDeclaredMethod("setBoolean", String.class, boolean.class);
            final Method nbtSetInteger = nbtTagClass.getDeclaredMethod("setInt", String.class, int.class);

            for (final String key : nbtTags.keySet()) {
                final Object value = nbtTags.get(key);
                Object nbtTag;
                if ((nbtTag = getNBTTag.invoke(nmsItemStack)) == null) {
                    nbtTag = nbtTagClass.newInstance();
                }

                if (value instanceof String) {
                    final String data = (String) value;
                    nbtSetString.invoke(nbtTag, key, data);
                } else if (value instanceof Integer) {
                    final int data = (int) value;
                    nbtSetInteger.invoke(nbtTag, key, data);
                } else if (value instanceof Boolean) {
                    final boolean data = (boolean) value;
                    nbtSetBoolean.invoke(nbtTag, key, data);
                }
                setNBTTag.invoke(nmsItemStack, nbtTag);
            }
            return (ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack);
        } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to set nbt tag.", e);
        }
        return null;
    }

    private static Class<?> createClass(String path) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return Class.forName(path.replace("VERSION", version));
    }
}
