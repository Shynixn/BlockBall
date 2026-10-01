package com.github.shynixn.blockball.config

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.InputStreamReader

/**
 * Loader for the second MoonXBall config file: `MX_Blocball2.yml`.
 *
 * Lifecycle:
 *   1. On first run: extract the bundled resource (`/MX_Blocball2.yml`)
 *      into the plugin data folder.
 *   2. On subsequent loads: read the on-disk file, then merge any missing
 *      keys from the bundled resource (additive-only; never overwrites
 *      user customisations). This is what `auto-update with missing keys
 *      on upgrade` requires.
 *   3. Reloaded by `/blockball reload` via [reload].
 *
 * The config is cached in memory so reads are O(1) — this is the
 * "cache values" performance patch required by Task 4.
 *
 * All access goes through [get], [getString], [getInt], etc., which
 * validate the path is non-null and provide a typed fallback. No
 * caller should ever touch the underlying [YamlConfiguration] directly.
 */
class MoonXConfig(private val plugin: Plugin) {

    private val fileName: String = "MX_Blocball2.yml"

    @Volatile
    private var cached: YamlConfiguration = YamlConfiguration()

    /**
     * Loads (or creates) `MX_Blocball2.yml` and merges missing keys from
     * the bundled resource. Must be called once on enable and again on
     * every `/blockball reload`.
     */
    fun reload() {
        val dataFolder = plugin.dataFolder
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, fileName)
        val bundled = plugin.getResource("/$fileName") ?: plugin.getResource(fileName)

        if (!file.exists()) {
            // First run: copy the bundled resource verbatim.
            bundled?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
        }

        // Load from disk (preserves user customisations).
        cached = YamlConfiguration.loadConfiguration(file)

        // Merge missing keys from the bundled resource.
        if (bundled != null) {
            val defaults = YamlConfiguration.loadConfiguration(InputStreamReader(bundled))
            var merged = false
            for (key in defaults.getKeys(true)) {
                if (cached.contains(key)) continue
                if (defaults.isConfigurationSection(key)) continue // only set leaf values
                val value = defaults.get(key)
                if (value != null) {
                    cached.set(key, value)
                    merged = true
                }
            }
            if (merged) {
                try {
                    cached.save(file)
                    plugin.logger.info("[MoonXBall] Added missing keys to $fileName.")
                } catch (e: Throwable) {
                    plugin.logger.warning("[MoonXBall] Could not save merged $fileName: ${e.message}")
                }
            }
        }
    }

    /** Returns the cached YamlConfiguration. Use sparingly; prefer typed getters. */
    fun raw(): YamlConfiguration = cached

    // ---- Typed getters (cached reads) ----------------------------------

    fun getString(path: String, default: String = ""): String = cached.getString(path, default) ?: default
    fun getInt(path: String, default: Int = 0): Int = cached.getInt(path, default)
    fun getBoolean(path: String, default: Boolean = false): Boolean = cached.getBoolean(path, default)
    fun getDouble(path: String, default: Double = 0.0): Double = cached.getDouble(path, default)
    fun getStringList(path: String): List<String> = cached.getStringList(path) ?: emptyList()

    /**
     * Returns the lobby worlds configured by the user. Empty list => use
     * "not in game" condition only.
     */
    fun lobbyWorlds(): List<String> = getStringList("lobby.worlds")

    /** True if a player NOT in a game should receive the sword. */
    fun giveSwordWhenNotInGame(): Boolean = getBoolean("lobby.giveWhenNotInGame", true)

    /** Material name (resolved by MaterialCompat) of the join sword. */
    fun joinItemMaterial(): String = getString("joinItem.material", "WOOD_SWORD")

    fun joinItemDisplayName(): String = getString("joinItem.displayName", "&6&lJoin An Game &7(Right-Click)")
    fun joinItemLore(): List<String> = getStringList("joinItem.lore")
    fun joinItemSlot(): Int = getInt("joinItem.slot", 3).coerceIn(0, 8)
    fun joinItemGlow(): Boolean = getBoolean("joinItem.glow", false)
    fun joinItemMarker(): String = getString("joinItem.marker", "moonxball.joinitem")

    fun guiTitle(): String = getString("gui.title", "&8&lSelection Area")
    fun guiSize(): Int = getInt("gui.size", 27).let { (it / 9) * 9 }.coerceIn(9, 54)
    fun guiFillerMaterial(): String = getString("gui.filler.material", "BLACK_STAINED_GLASS_PANE")
    fun guiFillerDisplayName(): String = getString("gui.filler.displayName", " ")
    fun guiRefreshTicks(): Long = getLong("gui.refreshTicks", 20L).coerceAtLeast(1L)
    fun guiCloseOnJoin(): Boolean = getBoolean("gui.closeOnJoin", true)

    fun guiArenaDisplayNameTemplate(): String = getString("gui.arenaItem.displayName", "&a%arena_display%")
    fun guiArenaWaitingMaterial(): String = getString("gui.arenaItem.waitingMaterial", "LIME_WOOL")
    fun guiArenaRunningMaterial(): String = getString("gui.arenaItem.runningMaterial", "YELLOW_WOOL")
    fun guiArenaFullMaterial(): String = getString("gui.arenaItem.fullMaterial", "RED_WOOL")
    fun guiArenaDisabledMaterial(): String = getString("gui.arenaItem.disabledMaterial", "GRAY_WOOL")
    fun guiArenaLockedMaterial(): String = getString("gui.arenaItem.lockedMaterial", "BARRIER")
    fun guiArenaLore(): List<String> = getStringList("gui.arenaItem.lore")

    fun messageNoPermissionArena(): String = getString("messages.noPermissionArena", "&cYou do not have permission to join arena %arena_name%.")
    fun messageArenaNotFound(): String = getString("messages.arenaNotFound", "&cArena %arena_name% was not found.")
    fun messageArenaFull(): String = getString("messages.arenaFull", "&cArena %arena_name% is full.")
    fun messageArenaDisabled(): String = getString("messages.arenaDisabled", "&cArena %arena_name% is disabled.")
    fun messageJoinSuccess(): String = getString("messages.joinSuccess", "&aJoined arena %arena_display%.")
    fun messageLeaveSuccess(): String = getString("messages.leaveSuccess", "&aLeft the arena.")
    fun messageGuiOpened(): String = getString("messages.guiOpened", "&7Opening Selection Area...")
    fun messageSwordReceived(): String = getString("messages.swordReceived", "&7You received the Join-An-Game sword.")

    fun soundOpenGuiName(): String = getString("sounds.openGui.sound", "UI_BUTTON_CLICK")
    fun soundOpenGuiVolume(): Float = getDouble("sounds.openGui.volume", 1.0).toFloat()
    fun soundOpenGuiPitch(): Float = getDouble("sounds.openGui.pitch", 1.0).toFloat()
    fun soundJoinArenaName(): String = getString("sounds.joinArena.sound", "ENTITY_EXPERIENCE_ORB_PICKUP")
    fun soundJoinArenaVolume(): Float = getDouble("sounds.joinArena.volume", 1.0).toFloat()
    fun soundJoinArenaPitch(): Float = getDouble("sounds.joinArena.pitch", 1.0).toFloat()
    fun soundNoPermissionName(): String = getString("sounds.noPermission.sound", "BLOCK_ANVIL_LAND")
    fun soundNoPermissionVolume(): Float = getDouble("sounds.noPermission.volume", 0.6).toFloat()
    fun soundNoPermissionPitch(): Float = getDouble("sounds.noPermission.pitch", 1.0).toFloat()

    fun multiverseEnabled(): Boolean = getBoolean("hooks.multiverseCore.enabled", true)
    fun multiverseVerbose(): Boolean = getBoolean("hooks.multiverseCore.verbose", true)
    fun decentHologramsEnabled(): Boolean = getBoolean("hooks.decentHolograms.enabled", true)
    fun decentHologramsVerbose(): Boolean = getBoolean("hooks.decentHolograms.verbose", true)
    fun decentHologramsRefreshTicks(): Long = getLong("hooks.decentHolograms.refreshTicks", 40L).coerceAtLeast(5L)

    fun permissionJoinNode(): String = getString("permissions.joinNode", "BlockMxc.join")
    fun permissionLegacyJoinNode(): String = getString("permissions.legacyJoinNode", "blockball.join")
    fun permissionShowLockedArenas(): Boolean = getBoolean("permissions.showLockedArenas", true)

    fun legacyScoreboardAdapter(): Boolean = getBoolean("legacyClient.scoreboardAdapter", true)
    fun legacyScoreNumberBehavior(): String = getString("legacyClient.scoreNumberBehavior", "order")
    fun legacyStripHexColors(): Boolean = getBoolean("legacyClient.stripHexColors", true)

    private fun getLong(path: String, default: Long): Long =
        if (cached.contains(path)) cached.getLong(path, default) else default
}
