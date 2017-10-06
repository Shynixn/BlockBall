package com.github.shynixn.blockball.lib;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

@Deprecated
public final class SSKulls {
    public static ItemStack getSkull(String skin) {
        final ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (skin.length() > 20) {
            if (skin.contains("http")) {
                return activateHeadByURL(skin, itemStack);
            } else {
                return activateHeadByCode(skin, itemStack);
            }
        } else {
            return activateHeadByName(skin, itemStack);
        }
    }

    public static ItemStack activateHeadByName(String name, ItemStack itemStack) {
        try {
            if (itemStack.getItemMeta() instanceof SkullMeta) {
                final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                meta.setOwner(name);
                itemStack.setItemMeta(meta);
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to activate head.", e);
        }
        return itemStack;
    }

    public static ItemStack activateHeadByPlayer(Player player, ItemStack itemStack) {
        return activateHeadByName(player.getName(), itemStack);
    }

    public static ItemStack activateHeadByURL(String skinUrl, ItemStack itemStack) {
        if (itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            try {
                final Class<?> cls = createClass("org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull");
                final Object real = cls.cast(meta);
                final Field field = real.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(real, getNonPlayerProfile(skinUrl));
                meta = SkullMeta.class.cast(real);
                itemStack.setItemMeta(meta);
                itemStack = new ItemStackBuilder(itemStack).setDisplayName("TMP");
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to set url of itemstack.", e);
            }
        }
        return itemStack;
    }

    public static ItemStack activateHeadByCode(String code, ItemStack itemStack) {
        return activateHeadByURL(Base64Coder.decodeString(code), itemStack);
    }

    public static String getNameFromItemStack(ItemStack itemStack) {
        if (itemStack.getItemMeta() instanceof SkullMeta) {
            final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            return meta.getOwner();
        }
        return null;
    }

    public static String getCodeFromItemStack(ItemStack itemStack) {
        return Base64Coder.decodeString(getURLFromItemStack(itemStack));
    }

    public static String getURLFromItemStack(ItemStack itemStack) {
        final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        try {
            final Class<?> cls = createClass("org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull");
            final Object real = cls.cast(meta);
            final Field field = real.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            final GameProfile profile = (GameProfile) field.get(real);
            final Collection<Property> props = profile.getProperties().get("textures");
            for (final Property property : props) {
                if (property.getName().equals("textures")) {
                    final String text = Base64Coder.decodeString(property.getValue());
                    String s = "";
                    boolean start = false;
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) == '"') {
                            start = !start;
                        } else if (start) {
                            s += text.charAt(i);
                        }
                    }
                    return s;
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to get url from itemstack.", e);
        }
        return null;
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
