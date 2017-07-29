package com.github.shynixn.blockball.lib;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

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
public class ItemStackBuilder extends ItemStack {

    /**
     * Initializes a new itemStackBuilder
     */
    public ItemStackBuilder() {
        super();
    }

    /**
     * Initializes a new itemStackBuilder
     *
     * @param type type
     */
    public ItemStackBuilder(Material type) {
        super(type);
    }

    /**
     * Initializes a new itemStackBuilder
     *
     * @param type   type
     * @param amount amount
     */
    public ItemStackBuilder(Material type, int amount) {
        super(type, amount);
    }

    /**
     * Initializes a new itemStackBuilder
     *
     * @param type   type
     * @param amount amount
     * @param damage damage
     */
    public ItemStackBuilder(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    /**
     * Initializes a new itemstackBuilder from an existing itemStack
     *
     * @param itemStack itemStack
     */
    public ItemStackBuilder(ItemStack itemStack) {
        super();
        this.setType(itemStack.getType());
        this.setAmount(itemStack.getAmount());
        this.setData(itemStack.getData());
        this.setDurability(itemStack.getDurability());
        this.setItemMeta(itemStack.getItemMeta());
    }

    /**
     * Sets the displayName of the itemStack
     *
     * @param name name
     */
    public ItemStackBuilder setDisplayName(String name) {
        final ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the skin of the itemStack. Only works on playerHeads
     *
     * @param skin skin
     */
    public ItemStackBuilder setSkin(String skin) {
        if (this.getItemMeta() instanceof SkullMeta) {
            final SkullMeta skullMeta = (SkullMeta) this.getItemMeta();
            skullMeta.setOwner(skin);
            this.setItemMeta(skullMeta);
        }
        return this;
    }

    /**
     * Sets the color of the itemStack. Only works on leatherItems
     *
     * @param color color
     * @return itemStack
     */
    public ItemStackBuilder setColor(Color color) {
        if (this.getItemMeta() instanceof LeatherArmorMeta) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) this.getItemMeta();
            meta.setColor(color);
            this.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Sets the lore of the itemStack
     *
     * @param lore lore
     */
    public ItemStackBuilder setLore(String... lore) {
        final List<String> data = new ArrayList<>();
        for (final String s : lore) {
            data.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        final ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(data);
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Adds lore to the end of the itemstack lore
     *
     * @param lore lore
     */
    public ItemStackBuilder addLore(String... lore) {
        final ItemMeta itemMeta = this.getItemMeta();
        final List<String> data = new ArrayList<>(itemMeta.getLore());
        for (final String s : lore) {
            data.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(data);
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Adds lore after the index of a lore line
     *
     * @param index index
     * @param lore  lore
     */
    public ItemStackBuilder addLore(int index, String... lore) {
        final ItemMeta itemMeta = this.getItemMeta();
        final List<String> data = new ArrayList<>();
        for (int i = 0; i < itemMeta.getLore().size(); i++) {
            data.add(itemMeta.getLore().get(0));
            if (i == index) {
                for (final String s : lore) {
                    data.add(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
        }
        itemMeta.setLore(data);
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Clears all lore from the itemStack
     */
    public ItemStackBuilder clearLore() {
        final ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setLore(new ArrayList<>());
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Removes the lore matching the lines
     *
     * @param lore lore
     */
    public ItemStackBuilder removeLore(String... lore) {
        final ItemMeta itemMeta = this.getItemMeta();
        final List<String> data = new ArrayList<>();
        for (final String s : itemMeta.getLore()) {
            boolean add = true;
            for (final String k : lore) {
                if (s.equals(k)) {
                    add = false;
                }
            }
            if (add) {
                data.add(s);
            }
        }
        itemMeta.setLore(data);
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Removes the lore from the specific index
     *
     * @param index index
     */
    public ItemStackBuilder removeLore(int index) {
        final List<String> data = new ArrayList<>();
        final ItemMeta itemMeta = this.getItemMeta();
        for (int i = 0; i < itemMeta.getLore().size(); i++) {
            if (i != index) {
                data.add(itemMeta.getLore().get(i));
            }
        }
        itemMeta.setLore(data);
        this.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Creates a new itemStack from this
     *
     * @return itemStack
     */
    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(this.getType());
        itemStack.setAmount(this.getAmount());
        itemStack.setData(this.getData());
        itemStack.setDurability(this.getDurability());
        itemStack.setItemMeta(this.getItemMeta());
        return itemStack;
    }
}