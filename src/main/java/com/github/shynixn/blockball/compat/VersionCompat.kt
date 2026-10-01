package com.github.shynixn.blockball.compat

import org.bukkit.Bukkit

/**
 * MoonXBall compatibility layer.
 *
 * Detected once at plugin startup; implementations are selected through the
 * [ServerVersion] enum and exposed as small strategy objects (see
 * [MaterialCompat], [SoundCompat], [ParticleCompat], [HandCompat]).
 *
 * No scattered `if (version >= 1.13)` checks should exist outside this package.
 */
object VersionCompat {
    /**
     * Resolved once at onEnable.
     *
     * Uses `Bukkit.getServer().getClass().getPackage().getName()` which on
     * CraftBukkit-derived servers returns `org.bukkit.craftbukkit.v1_8_R3`,
     * `org.bukkit.craftbukkit.v1_21_R7`, etc. Paper 1.20.5+ no longer ships
     * a versioned package; we detect that case via `Bukkit.getBukkitVersion()`.
     */
    fun detect(): ServerVersion {
        val pkg = Bukkit.getServer().javaClass.`package`.name
        val raw = pkg.substringAfterLast('.', "")
        val parsed = parseCraftBukkitTag(raw)
        if (parsed != null) return parsed

        // Paper 1.20.5+ removes the versioned package. Fall back to the
        // bukkit version string ("1.21.6-R0.1-SNAPSHOT").
        val bukkitVer = Bukkit.getBukkitVersion()
        val m = Regex("""(\d+)\.(\d+)(?:\.(\d+))?""").find(bukkitVer) ?: return ServerVersion.UNKNOWN
        val major = m.groupValues[1].toInt()
        val minor = m.groupValues[2].toInt()
        return when {
            major == 1 && minor == 8 -> ServerVersion.v1_8
            major == 1 && minor in 9..12 -> ServerVersion.v1_9_TO_1_12
            major == 1 && minor in 13..16 -> ServerVersion.v1_13_TO_1_16
            major == 1 && minor >= 17 -> ServerVersion.v1_17_PLUS
            major >= 2 -> ServerVersion.v1_17_PLUS
            else -> ServerVersion.UNKNOWN
        }
    }

    private fun parseCraftBukkitTag(tag: String): ServerVersion? {
        if (tag.isBlank() || !tag.startsWith("v")) return null
        val m = Regex("""v(\d+)_(\d+)_R(\d+)""").matchEntire(tag) ?: return null
        val major = m.groupValues[1].toInt()
        val minor = m.groupValues[2].toInt()
        return when {
            major == 1 && minor == 8 -> ServerVersion.v1_8
            major == 1 && minor in 9..12 -> ServerVersion.v1_9_TO_1_12
            major == 1 && minor in 13..16 -> ServerVersion.v1_13_TO_1_16
            major == 1 && minor >= 17 -> ServerVersion.v1_17_PLUS
            major >= 26 -> ServerVersion.v1_17_PLUS
            else -> null
        }
    }
}

/**
 * Coarse-grained server version bucket. Fine-grained per-packet NMS handling
 * is left to the existing `mcutils:packet` NMS layer; this enum only gates
 * plugin-level API differences (Materials, Sounds, hand-related events).
 */
enum class ServerVersion {
    UNKNOWN,
    /** 1.8.0 – 1.8.9: off-hand does not exist, Materials use legacy names. */
    v1_8,

    /** 1.9 – 1.12: off-hand exists, Materials still use legacy names. */
    v1_9_TO_1_12,

    /** 1.13 – 1.16: Materials are flattened (`WOOD_SWORD` -> `WOODEN_SWORD`). */
    v1_13_TO_1_16,

    /** 1.17+: hex colors via `net.md_5.bungee.api.ChatColor.of(...)` are available. */
    v1_17_PLUS;

    val isLegacyMaterial: Boolean get() = this == v1_8 || this == v1_9_TO_1_12
    val hasOffHand: Boolean get() = this != v1_8
    val hasHexColors: Boolean get() = this == v1_17_PLUS || this == v1_13_TO_1_16
    val hasPersistentDataContainer: Boolean get() = this != v1_8 && this != v1_9_TO_1_12
    val isAtLeast1_13: Boolean get() = this == v1_13_TO_1_16 || this == v1_17_PLUS
}
