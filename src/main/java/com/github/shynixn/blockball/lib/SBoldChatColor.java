package com.github.shynixn.blockball.lib;

import org.bukkit.ChatColor;

public enum SBoldChatColor {
    AQUA(ChatColor.AQUA + "" + ChatColor.BOLD),
    BLACK(ChatColor.BLACK + "" + ChatColor.BOLD),
    BLUE(ChatColor.BLUE + "" + ChatColor.BOLD),
    DARK_AQUA(ChatColor.DARK_AQUA + "" + ChatColor.BOLD),
    DARK_BLUE(ChatColor.DARK_BLUE + "" + ChatColor.BOLD),
    DARK_GRAY(ChatColor.DARK_GRAY + "" + ChatColor.BOLD),
    DARK_GREEN(ChatColor.DARK_GREEN + "" + ChatColor.BOLD),
    DARK_PURPLE(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD),
    DARK_RED(ChatColor.DARK_RED + "" + ChatColor.BOLD),
    GOLD(ChatColor.GOLD + "" + ChatColor.BOLD),
    GRAY(ChatColor.GRAY + "" + ChatColor.BOLD),
    GREEN(ChatColor.GREEN + "" + ChatColor.BOLD),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD),
    RED(ChatColor.RED + "" + ChatColor.BOLD),
    WHITE(ChatColor.WHITE + "" + ChatColor.BOLD),
    YELLOW(ChatColor.YELLOW + "" + ChatColor.BOLD),
    LINEBREAK("\n");

    private final String color;

    SBoldChatColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return this.color;
    }
}
