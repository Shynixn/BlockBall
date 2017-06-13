package com.github.shynixn.blockball.lib;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@Deprecated
public final class SItemStackUtils {
    public static ItemStack deserialize(String text) {
        if (text != null) {
            final FileConfiguration configuration = new YamlConfiguration();
            try {
                configuration.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot deserialize itemsstack.", e);
            }
            return configuration.getItemStack("dummy");
        }
        return null;
    }

    public static String serialize(SItemStack itemStack) {
        if (itemStack != null)
            return serialize(itemStack.getItemStack());
        return null;
    }

    public static String serialize(ItemStack itemStack) {
        if (itemStack != null) {
            final FileConfiguration configuration = new YamlConfiguration();
            configuration.set("dummy", itemStack);
            return configuration.saveToString();
        }
        return null;
    }

    public static boolean compareDisplayNames(ItemStack itemStack, ItemStack itemStack2) {
        return !(itemStack == null || itemStack2 == null) && !(itemStack.getItemMeta() == null || itemStack2.getItemMeta() == null) && !(itemStack.getItemMeta().getDisplayName() == null || itemStack2.getItemMeta().getDisplayName() == null) && itemStack.getItemMeta().getDisplayName().equals(itemStack2.getItemMeta().getDisplayName());
    }

    public static boolean compareLore(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack == null || itemStack2 == null)
            return false;
        if (itemStack.getItemMeta() == null || itemStack2.getItemMeta() == null)
            return false;
        if (itemStack.getItemMeta().getLore() == null || itemStack2.getItemMeta().getLore() == null)
            return false;
        if (itemStack.getItemMeta().getLore().size() != itemStack2.getItemMeta().getLore().size())
            return false;
        for (int i = 0; i < itemStack.getItemMeta().getLore().size(); i++) {
            if (!itemStack.getItemMeta().getLore().get(i).equals(itemStack2.getItemMeta().getLore().get(i)))
                return false;
        }
        return true;
    }

    public static boolean compareMaterials(ItemStack itemStack, ItemStack itemStack2) {
        return !(itemStack == null || itemStack2 == null) && itemStack2.getType() == itemStack.getType();
    }

    public static boolean compareDisplayNamesMaterials(ItemStack itemStack, ItemStack itemStack2) {
        return compareDisplayNames(itemStack, itemStack2) && compareMaterials(itemStack, itemStack2);
    }

    public static boolean compareDisplayNamesLore(ItemStack itemStack, ItemStack itemStack2) {
        return compareDisplayNames(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    public static boolean compareLoreMaterials(ItemStack itemStack, ItemStack itemStack2) {
        return compareMaterials(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    public static boolean compareDisplayNamesMaterialsLore(ItemStack itemStack, ItemStack itemStack2) {
        return compareDisplayNamesMaterials(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    //

    public static boolean compareDisplayNames(ItemStack itemStack, SItemStack itemStack2) {
        return !(itemStack == null || itemStack2 == null) && !(itemStack.getItemMeta().getDisplayName() == null || itemStack2.getDisplayName() == null) && itemStack.getItemMeta().getDisplayName().equals(itemStack2.getDisplayName());
    }

    public static boolean compareLore(ItemStack itemStack, SItemStack itemStack2) {
        if (itemStack == null || itemStack2 == null)
            return false;
        if (itemStack.getItemMeta().getLore() == null || itemStack2.getLore() == null)
            return false;
        if (itemStack.getItemMeta().getLore().size() != itemStack2.getLore().size())
            return false;
        for (int i = 0; i < itemStack.getItemMeta().getLore().size(); i++) {
            if (!itemStack.getItemMeta().getLore().get(i).equals(itemStack2.getLore().get(i)))
                return false;
        }
        return true;
    }

    public static boolean compareMaterials(ItemStack itemStack, SItemStack itemStack2) {
        return !(itemStack == null || itemStack2 == null) && itemStack2.getType() == itemStack.getType();
    }

    public boolean compareDisplayNamesMaterials(ItemStack itemStack, SItemStack itemStack2) {
        return compareDisplayNames(itemStack, itemStack2) && compareMaterials(itemStack, itemStack2);
    }

    public boolean compareDisplayNamesLore(ItemStack itemStack, SItemStack itemStack2) {
        return compareDisplayNames(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    public boolean compareLoreMaterials(ItemStack itemStack, SItemStack itemStack2) {
        return compareMaterials(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    public boolean compareDisplayNamesMaterialsLore(ItemStack itemStack, SItemStack itemStack2) {
        return this.compareDisplayNamesMaterials(itemStack, itemStack2) && compareLore(itemStack, itemStack2);
    }

    public static ItemStack clone(ItemStack itemStack) {
        if (itemStack != null)
            return itemStack.clone();
        return null;
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setColor(ItemStack itemStack, Color color) {
        final LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setColor(color);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setLore(ItemStack itemStack, String... lore) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack clone(SItemStack itemStack) {
        if (itemStack != null) {
            return itemStack.getItemStack();
        }
        return null;
    }
}
