package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper class to set and retrieve skins on skull itemstack.
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

    /**
     * Sets the skin of the itemStack regardless if it's the name of a player or a skin Url.
     *
     * @param itemStack itemStack
     * @param skin      skin
     * @throws Exception exception
     */
    public static void setItemStackSkin(ItemStack itemStack, String skin) throws Exception {
        if (itemStack == null)
            throw new IllegalArgumentException("ItemStack skin cannot be null");
        if (skin == null)
            throw new IllegalArgumentException("Skin cannot be null!");
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
                final Class<?> cls = createClass();
                final Object real = cls.cast(meta);
                final Field field = real.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(real, getNonPlayerProfile(newSkin));
                itemStack.setItemMeta(SkullMeta.class.cast(real));
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
                throw new Exception("Failed to set url of itemStack.", e);
            }
        } else {
            ((SkullMeta) meta).setOwner(skin);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Retrieves the skin of the itemStack regardless if it's the name of a player or a skin URL.
     *
     * @param itemStack itemStack
     * @return skin
     * @throws Exception exception
     */
    public static Optional<String> getItemStackSkin(ItemStack itemStack) throws Exception {
        if (itemStack == null)
            throw new IllegalArgumentException("ItemStack skin cannot be null");
        final ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            return Optional.empty();
        }

        final SkullMeta skullMeta = (SkullMeta) meta;
        if (skullMeta.getOwner() != null) {
            return Optional.of(skullMeta.getOwner());
        } else {
            return obtainSkinFromSkull(meta);
        }
    }

    private static Optional<String> obtainSkinFromSkull(ItemMeta meta) throws Exception {
        try {
            final Class<?> cls = createClass();
            final Object real = cls.cast(meta);
            final Field field = real.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            final GameProfile profile = (GameProfile) field.get(real);
            final Collection<Property> props = profile.getProperties().get("textures");
            for (final Property property : props) {
                if (property.getName().equals("textures")) {
                    final String text = Base64Coder.decodeString(property.getValue());
                    final StringBuilder s = new StringBuilder();
                    boolean start = false;
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) == '"') {
                            start = !start;
                        } else if (start) {
                            s.append(text.charAt(i));
                        }
                    }
                    return Optional.of(s.toString());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
            throw new Exception("Failed to obtain url of itemStack.", e);
        }
        return Optional.empty();
    }

    private static Class<?> createClass() throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return Class.forName("org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull".replace("VERSION", version));
    }

    private static GameProfile getNonPlayerProfile(String skinUrl) {
        final GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinUrl + "\"}}}")));
        return newSkinProfile;
    }
}
