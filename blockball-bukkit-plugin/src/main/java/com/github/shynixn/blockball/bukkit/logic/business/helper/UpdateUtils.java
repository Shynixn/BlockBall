package com.github.shynixn.blockball.bukkit.logic.business.helper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
public class UpdateUtils {

    private static final String BASE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    /**
     * Checks if the given plugin version and spigot resource Id is the same by doing an webRequest
     *
     * @param resourceId spigot resourceId
     * @param plugin     plugin
     * @return isUpToDate
     * @throws IOException exception
     */
    public static boolean isPluginUpToDate(long resourceId, Plugin plugin) throws IOException {
        return plugin.getDescription().getVersion().equals(getLatestReleaseVersion(resourceId));
    }

    /**
     * Checks if the given plugin version and spigot resource Id is the same by doing and webRequest and printing the result into the console
     *
     * @param resourceId spigot resourceId
     * @param prefix     prefix
     * @param pluginName pluginName
     * @param plugin     plugin
     * @throws IOException exception
     */
    public static void checkPluginUpToDateAndPrintMessage(long resourceId, String prefix, String pluginName, Plugin plugin) throws IOException {
        if (!isPluginUpToDate(resourceId, plugin)) {
            if (plugin.getDescription().getVersion().endsWith("SNAPSHOT")) {
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "================================================");
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "You are using a snapshot of " + pluginName);
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "Please check if there is a new version available");
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "================================================");
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "================================================");
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + pluginName + " is outdated");
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "Please download the latest version from spigotmc.org");
                Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "================================================");
            }
        }
    }

    /**
     * Returns the latest version by a webRequest
     *
     * @param resourceId resource
     * @return version
     * @throws IOException exception
     */
    private static String getLatestReleaseVersion(long resourceId) throws IOException {
        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(BASE_URL + resourceId).openConnection();
        try (final InputStream stream = httpsURLConnection.getInputStream()) {
            try (final InputStreamReader reader = new InputStreamReader(stream)) {
                try (final BufferedReader bufferedReader = new BufferedReader(reader)) {
                    return bufferedReader.readLine();
                }
            }
        }
    }
}
