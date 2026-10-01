package com.github.shynixn.blockball.compat

import org.bukkit.Material
import org.bukkit.Server

/**
 * Cross-version Material resolver.
 *
 * On 1.13+ the Bukkit Material enum was flattened; many legacy names
 * (`WOOD_SWORD`, `WOOD_BUTTON`, `STAINED_GLASS_PANE`, ...) were renamed
 * or removed. This helper lets the rest of the plugin reference a logical
 * material ("wood sword") by a single [LogicalMaterial] entry, and the
 * actual [Material] is resolved once at startup.
 *
 * Resolving is intentionally lazy + cached: the first call after a
 * [ServerVersion] is set caches the result.
 */
object MaterialCompat {

    @Volatile
    private var version: ServerVersion = ServerVersion.UNKNOWN

    @Volatile
    private var cache: MutableMap<LogicalMaterial, Material?> = HashMap()

    /**
     * Must be called once on plugin enable with the detected [ServerVersion].
     */
    fun init(serverVersion: ServerVersion) {
        version = serverVersion
        cache = HashMap()
    }

    /**
     * Returns the [Material] for [logical], or null if it does not exist on
     * the current server. Callers should treat null as "feature disabled".
     */
    fun resolve(logical: LogicalMaterial): Material? {
        cache[logical]?.let { return it }
        val resolved = resolveUncached(logical, version)
        cache[logical] = resolved
        return resolved
    }

    /**
     * Returns the [Material] for [logical] or a guaranteed-safe fallback (AIR).
     */
    fun resolveOrAir(logical: LogicalMaterial): Material = resolve(logical) ?: Material.AIR

    /**
     * Returns a properly-constructed [ItemStack] for [logical], applying the
     * legacy data-byte color where appropriate (e.g. black stained glass on
     * 1.8 needs `STAINED_GLASS_PANE:15`). Returns null if the logical
     * material cannot be resolved.
     *
     * Use this method when you need an item that has the correct color
     * (wool, stained clay, stained glass). For materials with no data byte
     * (WOOD_SWORD, BARRIER), `resolveOrAir` + `ItemStack(material)` is enough.
     */
    fun resolveStack(logical: LogicalMaterial, amount: Int = 1): org.bukkit.inventory.ItemStack? {
        val mat = resolve(logical) ?: return null
        val stack = org.bukkit.inventory.ItemStack(mat, amount)
        // On 1.8/1.9-1.12 the data byte matters for wool / stained glass / etc.
        if (version.isLegacyMaterial) {
            val data = when (logical) {
                LogicalMaterial.BLACK_STAINED_GLASS_PANE -> 15
                LogicalMaterial.WHITE_STAINED_GLASS_PANE -> 0
                LogicalMaterial.LIME_WOOL -> 5
                LogicalMaterial.YELLOW_WOOL -> 4
                LogicalMaterial.RED_WOOL -> 14
                LogicalMaterial.GRAY_WOOL -> 7
                else -> return stack
            }
            try {
                @Suppress("DEPRECATION")
                stack.data = org.bukkit.material.MaterialData(mat, data.toByte())
                @Suppress("DEPRECATION")
                stack.durability = data.toShort()
            } catch (_: Throwable) { /* defensive: ignore */ }
        }
        return stack
    }

    private fun resolveUncached(logical: LogicalMaterial, version: ServerVersion): Material? {
        val names = when (logical) {
            LogicalMaterial.WOOD_SWORD ->
                if (version.isLegacyMaterial) listOf("WOOD_SWORD") else listOf("WOODEN_SWORD", "WOOD_SWORD")
            LogicalMaterial.BLACK_STAINED_GLASS_PANE ->
                if (version.isLegacyMaterial) listOf("STAINED_GLASS_PANE:15")
                else listOf("BLACK_STAINED_GLASS_PANE")
            LogicalMaterial.WHITE_STAINED_GLASS_PANE ->
                if (version.isLegacyMaterial) listOf("STAINED_GLASS_PANE:0")
                else listOf("WHITE_STAINED_GLASS_PANE")
            LogicalMaterial.LIME_WOOL ->
                if (version.isLegacyMaterial) listOf("WOOL:5") else listOf("LIME_WOOL", "WOOL")
            LogicalMaterial.YELLOW_WOOL ->
                if (version.isLegacyMaterial) listOf("WOOL:4") else listOf("YELLOW_WOOL", "WOOL")
            LogicalMaterial.RED_WOOL ->
                if (version.isLegacyMaterial) listOf("WOOL:14") else listOf("RED_WOOL", "WOOL")
            LogicalMaterial.GRAY_WOOL ->
                if (version.isLegacyMaterial) listOf("WOOL:7") else listOf("GRAY_WOOL", "WOOL")
            LogicalMaterial.BARRIER -> listOf("BARRIER")
            LogicalMaterial.NETHER_STAR -> listOf("NETHER_STAR")
        }
        for (spec in names) {
            val mat = parseMaterialSpec(spec) ?: continue
            if (mat != Material.AIR || spec == "AIR") return mat
        }
        return null
    }

    private fun parseMaterialSpec(spec: String): Material? {
        val parts = spec.split(":")
        val name = parts[0]
        // The data-byte variant only exists on 1.12 and below; on 1.13+
        // data bytes are baked into the Material enum so we ignore them.
        // (parts[1] is intentionally unused on 1.13+ — kept for diagnostic clarity.)
        val mat = Material.values().firstOrNull { it.name == name } ?: return null
        return mat
    }

    /**
     * Try [Material.getMaterial] for [name] directly, with a fallback to the
     * legacy flattened form. Returns AIR if neither exists.
     */
    fun byNameOrAir(name: String): Material {
        if (name.isBlank()) return Material.AIR
        Material.values().firstOrNull { it.name.equals(name, ignoreCase = true) }?.let { return it }
        return when (name.uppercase()) {
            "WOODEN_SWORD" -> resolveOrAir(LogicalMaterial.WOOD_SWORD)
            "WOOD_SWORD" -> resolveOrAir(LogicalMaterial.WOOD_SWORD)
            else -> Material.AIR
        }
    }
}

/**
 * Logical materials used by MoonXBall features. Add a new entry here when
 * a feature needs a material whose name changes between 1.8 and 1.21+.
 */
enum class LogicalMaterial {
    WOOD_SWORD,
    BLACK_STAINED_GLASS_PANE,
    WHITE_STAINED_GLASS_PANE,
    LIME_WOOL,
    YELLOW_WOOL,
    RED_WOOL,
    GRAY_WOOL,
    BARRIER,
    NETHER_STAR,
}
