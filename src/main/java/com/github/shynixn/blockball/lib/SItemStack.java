package com.github.shynixn.blockball.lib;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class SItemStack {
    private String displayName;
    private String[] lore;
    private final Material material;
    private int id;
    private String skullName;

    public SItemStack(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.id = itemStack.getDurability();
        if (itemStack.getItemMeta() != null) {
            this.displayName = itemStack.getItemMeta().getDisplayName();
            if (itemStack.getItemMeta().getLore() != null)
                this.lore = itemStack.getItemMeta().getLore().toArray(new String[0]);
            if (itemStack.getItemMeta() instanceof SkullMeta) {
                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                this.skullName = meta.getOwner();
            }
        }
    }

    public SItemStack(Material material) {
        this.material = material;
    }

    public SItemStack(String displayName) {
        this(Material.STICK, displayName);
    }

    public SItemStack(String displayName, String lore) {
        this(Material.STICK, displayName, new String[]{lore});
    }

    public SItemStack(String displayName, String... lore) {
        this(Material.STICK, displayName);
        this.lore = lore;
    }

    public SItemStack(Material material, String displayName) {
        this(material);
        this.displayName = displayName;
    }

    public SItemStack(Material material, String displayName, String lore) {
        this(material, displayName, new String[]{lore});
    }

    public SItemStack(Material material, String displayName, List<String> lore) {
        this(material, displayName);
        this.lore = lore.toArray(new String[0]);
    }

    public SItemStack(Material material, String displayName, String... lore) {
        this(material, displayName);
        this.lore = lore;
    }

    public SItemStack(Material material, int id, String displayName) {
        this.material = material;
        this.displayName = displayName;
        this.id = id;
    }

    public SItemStack(Material material, int id, String displayName, String lore) {
        this(material, displayName, new String[]{lore});
        this.id = id;
    }

    public SItemStack(Material material, int id, String displayName, List<String> lore) {
        this(material, displayName);
        this.lore = lore.toArray(new String[0]);
        this.id = id;
    }

    public SItemStack(Material material, int id, String displayName, String... lore) {
        this(material, displayName);
        this.lore = lore;
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLore(String... lore) {
        this.lore = lore;
    }

    public List<String> getLore() {
        return Arrays.asList(this.lore);
    }

    public Material getType() {
        return this.material;
    }

    public ItemStack getItemStack() {
        final ItemStack itemStack2 = new ItemStack(this.getType(), 1, (short) this.id);
        final ItemMeta meta = itemStack2.getItemMeta();
        meta.setDisplayName(this.getDisplayName());
        if (this.lore != null)
            meta.setLore(this.getLore());
        itemStack2.setItemMeta(meta);
        if (this.skullName != null && itemStack2.getItemMeta() instanceof SkullMeta) {
            final SkullMeta meta2 = (SkullMeta) itemStack2.getItemMeta();
            meta2.setOwner(this.skullName);
            itemStack2.setItemMeta(meta2);
        }
        return itemStack2;
    }

    public String serialize() {
        return SItemStackUtils.serialize(this);
    }
}
