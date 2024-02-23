package com.github.shynixn.blockball.enumeration

enum class PluginDependency(
        /**
         * Plugin name.
         */
        val pluginName: String) {
    /**
     * PlaceHolderApi plugin.
     */
    PLACEHOLDERAPI("PlaceholderAPI"),

    /**
     * Vault plugin.
     */
    VAULT("Vault"),

    /**
     * BossbarApi plugin.
     */
    BOSSBARAPI("BossBarAPI")
}
