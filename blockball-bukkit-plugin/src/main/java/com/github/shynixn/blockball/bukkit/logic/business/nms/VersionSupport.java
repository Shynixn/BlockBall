package com.github.shynixn.blockball.bukkit.logic.business.nms;

import com.github.shynixn.blockball.api.business.enumeration.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Enum to check for supported server versions.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
public enum VersionSupport {
    VERSION_1_8_R1("v1_8_R1", "1.8.0", 1.081),
    VERSION_1_8_R2("v1_8_R2", "1.8.3", 1.082),
    VERSION_1_8_R3("v1_8_R3", "1.8.8", 1.083),
    VERSION_1_9_R1("v1_9_R1", "1.9.0", 1.091),
    VERSION_1_9_R2("v1_9_R2", "1.9.4", 1.092),
    VERSION_1_10_R1("v1_10_R1", "1.10.2", 1.10),
    VERSION_1_11_R1("v1_11_R1", "1.11.2", 1.11),
    VERSION_1_12_R1("v1_12_R1", "1.12.2", 1.12),
    VERSION_1_13_R1("v1_13_R1", "1.13.0", 1.13),
    VERSION_1_13_R2("v1_13_R2", "1.13.1", 1.131),
    VERSION_1_14_R1("v1_14_R1", "1.14.1", 1.141);

    private final String versionText;
    private final String simpleVersionText;
    private final double versionCode;

    /**
     * Initializes a new instance of the enum versionSupport
     *
     * @param versionText versionText
     * @param versionCode versionCode
     */
    VersionSupport(String versionText, String simpleVersionText, double versionCode) {
        this.versionText = versionText;
        this.simpleVersionText = simpleVersionText;
        this.versionCode = versionCode;
    }

    /**
     * Returns the simpleVersionText
     *
     * @return simpleVersionText
     */
    public String getSimpleVersionText() {
        return this.simpleVersionText;
    }

    /**
     * Returns the versionText
     *
     * @return versionText
     */
    public String getVersionText() {
        return this.versionText;
    }

    /**
     * Returns the versionCode
     *
     * @return versionCode
     */
    public double getVersionCode() {
        return this.versionCode;
    }

    /**
     * Checks if this version is same or greater than the given versionSupport by parameter
     *
     * @param versionSupport versionSupport
     * @return isSameOrGreater
     */
    public boolean isVersionSameOrGreaterThan(VersionSupport versionSupport) {
        final int result = Double.compare(this.versionCode, versionSupport.getVersionCode());
        return result == 0 || result == 1;
    }

    /**
     * Checks if this version is same or lower than the given versionSupport by parameter
     *
     * @param versionSupport versionSupport
     * @return isSameOrLower
     */
    public boolean isVersionSameOrLowerThan(VersionSupport versionSupport) {
        final int result = Double.compare(this.versionCode, versionSupport.getVersionCode());
        return result == 0 || result == -1;
    }

    /**
     * Checks if this version is greater than the given versionSupport by parameter
     *
     * @param versionSupport versionSupport
     * @return isGreater
     */
    public boolean isVersionGreaterThan(VersionSupport versionSupport) {
        final int result = Double.compare(this.versionCode, versionSupport.getVersionCode());
        return result == 1;
    }

    /**
     * Checks if this version is lower than the given versionSupport by parameter
     *
     * @param versionSupport versionSupport
     * @return isLower
     */
    public boolean isVersionLowerThan(VersionSupport versionSupport) {
        final int result = Double.compare(this.versionCode, versionSupport.getVersionCode());
        return result == -1;
    }

    /**
     * Returns the greatest version supported by the plugin
     *
     * @return greatest version
     */
    public static VersionSupport getGreatestVersionSupported() {
        VersionSupport version = null;
        for (final VersionSupport versionSupport : VersionSupport.values()) {
            if (version == null || versionSupport.isVersionGreaterThan(version)) {
                version = versionSupport;
            }
        }
        return version;
    }

    /**
     * Returns the lowest version supported by the plugin
     *
     * @return lowest version
     */
    public static VersionSupport getLowestVersionSupported() {
        VersionSupport version = null;
        for (final VersionSupport versionSupport : VersionSupport.values()) {
            if (version == null || versionSupport.isVersionLowerThan(version)) {
                version = versionSupport;
            }
        }
        return version;
    }

    /**
     * Compatibility converter.
     *
     * @param version version.
     * @return support.
     */
    public static VersionSupport fromVersion(Version version) {
        for (final VersionSupport support : VersionSupport.values()) {
            if (support.simpleVersionText.equalsIgnoreCase(version.getId())) {
                return support;
            }
        }

        return null;
    }

    /**
     * Returns if the serverVersion is supported by the plugin
     *
     * @param pluginName pluginName
     * @param prefix     prefix
     * @return isSupported
     */
    public static boolean isServerVersionSupported(String pluginName, String prefix) {
        if (getServerVersion() == null) {
            Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "================================================");
            Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + pluginName + " does not support your server version");
            Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Install v" + getLowestVersionSupported().simpleVersionText + " - v" + getGreatestVersionSupported().simpleVersionText);
            Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "Plugin gets now disabled!");
            Bukkit.getServer().getConsoleSender().sendMessage(prefix + ChatColor.RED + "================================================");
            return false;
        }
        return true;
    }

    /**
     * Returns the versionSupport from the current Server Version.
     * Returns null if the server version cannot be recognized
     *
     * @return serverVersion
     */
    public static VersionSupport getServerVersion() {
        if (Bukkit.getServer() == null || Bukkit.getServer().getClass().getPackage() == null)
            return null;
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        for (final VersionSupport versionSupport : VersionSupport.values()) {
            if (versionSupport.getVersionText().equals(version))
                return versionSupport;
        }
        return null;
    }
}
