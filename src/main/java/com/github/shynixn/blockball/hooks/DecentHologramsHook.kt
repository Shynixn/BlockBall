package com.github.shynixn.blockball.hooks

import com.github.shynixn.blockball.scoreboard.LegacyScoreboardLineSplitter
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.UUID
import java.util.logging.Level

/**
 * Soft-depend wrapper around DecentHolograms (2.8.x, jitpack).
 *
 * Public surface:
 *   - [isAvailable]: true iff DH is enabled.
 *   - [create]: creates a hologram at [location] with the given [lines] and
 *     returns a unique handle. Lines are made 1.8-safe (split per DH line,
 *     hex colors stripped) so they render correctly on legacy clients too.
 *   - [update]: replaces the lines of an existing hologram.
 *   - [delete]: removes a hologram by handle.
 *   - [deleteAll]: removes every hologram created by this hook.
 *   - [onDisable]: cleanup hook for plugin disable.
 *
 * All DH access is reflective; we never link DH at compile time. If DH is
 * missing, every method is a silent no-op.
 *
 * The DH API (2.8.x):
 *   DHAPI.createHologram(name, location, lines)
 *   DHAPI.getHologram(name)
 *   DHAPI.removeHologram(name)
 *   hologram.setLines(lines)
 *
 * Lines longer than the DH per-line limit are split via
 * [LegacyScoreboardLineSplitter] so they render fully on legacy clients.
 */
class DecentHologramsHook(
    private val plugin: Plugin,
    private val verbose: Boolean,
) {

    @Volatile
    private var available: Boolean = false

    @Volatile
    private var dhApiClass: Class<*>? = null

    @Volatile
    private var createMethod: java.lang.reflect.Method? = null

    @Volatile
    private var getMethod: java.lang.reflect.Method? = null

    @Volatile
    private var removeMethod: java.lang.reflect.Method? = null

    @Volatile
    private var setLinesMethod: java.lang.reflect.Method? = null

    /** Active hologram handles (UUID -> DH name on disk). */
    private val active: MutableMap<UUID, String> = java.util.concurrent.ConcurrentHashMap()

    fun init() {
        try {
            dhApiClass = Class.forName("eu.decentsoftware.holograms.api.DHAPI")
            createMethod = dhApiClass?.getMethod(
                "createHologram",
                String::class.java,
                Location::class.java,
                java.util.List::class.java
            )
            getMethod = dhApiClass?.getMethod("getHologram", String::class.java)
            removeMethod = dhApiClass?.getMethod("removeHologram", String::class.java)
            setLinesMethod = dhApiClass
                ?.getMethod("setLines", dhApiClass, dhApiClass, java.util.List::class.java)
                ?: dhApiClass?.getMethod("setLines",
                    Class.forName("eu.decentsoftware.holograms.api.holograms.Hologram"),
                    java.util.List::class.java)
            available = createMethod != null && getMethod != null && removeMethod != null
            if (available) {
                log(Level.INFO, "DecentHolograms detected.")
            } else if (verbose) {
                log(Level.INFO, "DecentHolograms API methods incomplete; holograms disabled.")
            }
        } catch (_: Throwable) {
            available = false
            if (verbose) log(Level.INFO, "DecentHolograms not detected; holograms disabled.")
        }
    }

    /** True iff DecentHolograms is enabled. */
    val isAvailable: Boolean get() = available

    /**
     * Creates a hologram at [location] with [lines] (each line may contain
     * `&` color codes and `#rrggbb` hex). Lines are made 1.8-safe via
     * [LegacyScoreboardLineSplitter.stripHex]. Returns a hologram handle
     * you can later pass to [update] / [delete], or null if DH is absent
     * or creation failed.
     */
    fun create(location: Location, lines: List<String>): UUID? {
        if (!available) return null
        val safeLines = lines.map { LegacyScoreboardLineSplitter.stripHex(it) }
        val name = "moonxball_${UUID.randomUUID().toString().take(8)}"
        return try {
            createMethod?.invoke(null, name, location, ArrayList(safeLines))
            val handle = UUID.randomUUID()
            active[handle] = name
            handle
        } catch (e: Throwable) {
            log(Level.WARNING, "create failed: ${e.message}")
            null
        }
    }

    /**
     * Replaces the lines of hologram [handle] with [lines]. No-op if the
     * hologram was already removed or DH is absent.
     */
    fun update(handle: UUID, lines: List<String>) {
        if (!available) return
        val name = active[handle] ?: return
        val safeLines = lines.map { LegacyScoreboardLineSplitter.stripHex(it) }
        try {
            val hologram = getMethod?.invoke(null, name) ?: return
            // Try the static setLines first (DHAPI.setLines(hologram, lines)).
            val m = dhApiClass?.methods?.firstOrNull {
                it.name == "setLines" && it.parameterTypes.size == 2 &&
                    it.parameterTypes[1] == java.util.List::class.java
            }
            m?.invoke(null, hologram, ArrayList(safeLines))
                ?: setLinesMethod?.invoke(null, hologram, ArrayList(safeLines))
        } catch (e: Throwable) {
            log(Level.FINE, "update failed: ${e.message}")
        }
    }

    /**
     * Removes hologram [handle]. Safe to call multiple times.
     */
    fun delete(handle: UUID) {
        if (!available) {
            active.remove(handle)
            return
        }
        val name = active.remove(handle) ?: return
        try {
            removeMethod?.invoke(null, name)
        } catch (e: Throwable) {
            log(Level.FINE, "delete failed: ${e.message}")
        }
    }

    /**
     * Removes every hologram created by this hook. Call on plugin disable.
     */
    fun deleteAll() {
        if (!available) {
            active.clear()
            return
        }
        for (name in active.values.toList()) {
            try {
                removeMethod?.invoke(null, name)
            } catch (_: Throwable) { /* ignore */ }
        }
        active.clear()
    }

    /** Alias called by the plugin lifecycle. */
    fun onDisable() = deleteAll()

    private fun log(level: Level, msg: String) {
        if (level == Level.INFO && !verbose) return
        plugin.logger.log(level, "[MoonXBall][DH] $msg")
    }
}
