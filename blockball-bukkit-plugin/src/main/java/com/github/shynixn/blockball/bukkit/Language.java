package com.github.shynixn.blockball.bukkit;

import org.bukkit.ChatColor;

public class Language {
    public static String PREFIX = ChatColor.BLACK + "" + ChatColor.BOLD + '['
            + ChatColor.WHITE + ChatColor.BOLD + "Ball" + ChatColor.BLACK + "" + ChatColor.BOLD + "] " + ChatColor.GRAY;

    public static String NO_PERMISSION = ChatColor.RED + "You don't have permission to join the game.";

    public static String SIGN_DISABLED = ChatColor.DARK_RED + "" + ChatColor.BOLD + "DISABLED";
    public static String SIGN_ENABLED = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "ENABLED";
    public static String SIGN_RUNNING = ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "RUNNING";

    public static String ARENA_LOBBYFULL_MESSAGE = ChatColor.RED + "Lobby is full.";

    public static String GAME_DRAW_TITLE = ChatColor.AQUA + "Game";
    public static String GAME_DRAW_SUBTITLE = ChatColor.GREEN + "Game ended in a draw";
}
