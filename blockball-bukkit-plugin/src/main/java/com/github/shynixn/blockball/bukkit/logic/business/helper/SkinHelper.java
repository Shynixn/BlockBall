package com.github.shynixn.blockball.bukkit.logic.business.helper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.UUID;
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
public class SkinHelper {

    public static void setItemStackSkin(ItemStack itemStack, String skin) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            return;
        }

        String newSkin = skin;
        if (newSkin.contains("textures.minecraft.net")) {
            if (!newSkin.startsWith("http://")) {
                newSkin = "http://" + newSkin;
            }
            try {
                final Class<?> cls = createClass("org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull");
                final Object real = cls.cast(meta);
                final Field field = real.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(real, getNonPlayerProfile(newSkin));
                itemStack.setItemMeta(SkullMeta.class.cast(real));
            } catch (final IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to set url of itemstack.", e);
            }
        } else {
            ((SkullMeta) meta).setOwner(skin);
            itemStack.setItemMeta(meta);
        }
    }

    private static Class<?> createClass(String path) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return Class.forName(path.replace("VERSION", version));
    }

    private static GameProfile getNonPlayerProfile(String skinUrl) {
        final GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinUrl + "\"}}}")));
        return newSkinProfile;
    }
}