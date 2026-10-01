package com.github.shynixn.blockball.hooks

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level

/**
 * Soft-depend wrapper around Multiverse-Core (4.x and 5.x).
 *
 * Both MV 4.x (`com.onarandombox.MultiverseCore`) and MV 5.x
 * (`org.mvplugins.multiverse.core`) are supported reflectively, so we
 * never link the plugin at compile time.
 *
 * Public methods:
 *   - [isAvailable]: true iff MV is enabled.
 *   - [isWorldLoaded]: true iff MV knows about [worldName].
 *   - [loadWorld]: ensures [worldName] is loaded; no-op if MV is absent.
 *   - [teleport]: teleports [player] to [location] respecting MV per-world
 *     rules; falls back to Bukkit's [Player.teleport] if MV is absent or
 *     the call fails.
 *
 * All reflection is cached after the first call; reflection failures
 * downgrade gracefully to the Bukkit fallback.
 */
class MultiverseCoreHook(private val plugin: Plugin, private val verbose: Boolean) {

    @Volatile
    private var available: Boolean = false

    @Volatile
    private var core4Instance: Any? = null

    @Volatile
    private var core4MVWorldManager: Any? = null

    @Volatile
    private var core4GetMVWorldMethod: java.lang.reflect.Method? = null

    @Volatile
    private var core4LoadWorldMethod: java.lang.reflect.Method? = null

    @Volatile
    private var core4TeleportMethod: java.lang.reflect.Method? = null

    @Volatile
    private var core5Instance: Any? = null

    @Volatile
    private var core5WorldManager: Any? = null

    @Volatile
    private var core5LoadedWorlds: java.lang.reflect.Method? = null

    @Volatile
    private var core5LoadWorld: java.lang.reflect.Method? = null

    @Volatile
    private var core5Teleporter: Any? = null

    @Volatile
    private var core5Teleport: java.lang.reflect.Method? = null

    @Volatile
    private var safeBukkitTeleporterClass: Class<*>? = null

    @Volatile
    private var safeBukkitTeleportMethod: java.lang.reflect.Method? = null

    fun init() {
        // Try MV 4.x first.
        try {
            val pluginRef = Bukkit.getPluginManager().getPlugin("Multiverse-Core")
            if (pluginRef != null && pluginRef.javaClass.name.startsWith("com.onarandombox")) {
                core4Instance = pluginRef
                core4MVWorldManager = pluginRef.javaClass.getMethod("getMVWorldManager").invoke(pluginRef)
                core4GetMVWorldMethod = core4MVWorldManager?.javaClass?.getMethod("getMVWorld", String::class.java)
                core4LoadWorldMethod = core4MVWorldManager?.javaClass?.getMethod("loadWorld", String::class.java)
                core4TeleportMethod = pluginRef.javaClass.methods.firstOrNull {
                    it.name == "safeTeleport" || it.name == "teleportPlayer"
                }
                available = true
                log(Level.INFO, "Multiverse-Core 4.x detected.")
                return
            }
        } catch (_: Throwable) {
            // Not MV 4.x; fall through.
        }
        // Try MV 5.x.
        try {
            val pluginRef = Bukkit.getPluginManager().getPlugin("Multiverse-Core")
            if (pluginRef != null && pluginRef.javaClass.name.startsWith("org.mvplugins.multiverse")) {
                core5Instance = pluginRef
                // MV 5 uses a service locator; reflect it.
                val servicesClass = Class.forName("org.mvplugins.multiverse.core.MultiverseCoreApi")
                val getInstance = servicesClass.getMethod("get")
                val api = getInstance.invoke(null)
                core5WorldManager = api.javaClass.methods.firstOrNull { it.name == "getWorldManager" }?.invoke(api)
                    ?: api.javaClass.methods.firstOrNull { it.name == "getWorlds" }?.invoke(api)
                core5LoadedWorlds = core5WorldManager?.javaClass?.methods?.firstOrNull { it.name == "getLoadedWorlds" }
                core5LoadWorld = core5WorldManager?.javaClass?.methods?.firstOrNull { it.name == "loadWorld" }
                core5Teleporter = api.javaClass.methods.firstOrNull { it.name == "getTeleporter" }?.invoke(api)
                core5Teleport = core5Teleporter?.javaClass?.methods?.firstOrNull {
                    it.name == "teleport" && it.parameterTypes.size == 2
                }
                available = true
                log(Level.INFO, "Multiverse-Core 5.x detected.")
                return
            }
        } catch (_: Throwable) {
            // Not MV 5.x.
        }
        available = false
        if (verbose) log(Level.INFO, "Multiverse-Core not detected; falling back to Bukkit API.")
    }

    /** True iff Multiverse-Core is enabled. */
    val isAvailable: Boolean get() = available

    /** True iff MV is loaded AND knows about a world named [worldName]. */
    fun isWorldLoaded(worldName: String): Boolean {
        if (!available) return Bukkit.getWorld(worldName) != null
        // 4.x
        try {
            core4GetMVWorldMethod?.let {
                val mvWorld = it.invoke(core4MVWorldManager, worldName)
                if (mvWorld != null) {
                    if (verbose) log(Level.INFO, "MV 4.x reports world '$worldName' as loaded.")
                    return true
                }
            }
        } catch (_: Throwable) { /* fall through */ }
        // 5.x
        try {
            core5LoadedWorlds?.let {
                @Suppress("UNCHECKED_CAST")
                val worlds = it.invoke(core5WorldManager) as? Collection<*> ?: return false
                return worlds.any { w ->
                    try {
                        val nameMethod = w?.javaClass?.getMethod("getName")
                        nameMethod?.invoke(w) == worldName
                    } catch (_: Throwable) {
                        w?.toString()?.contains(worldName) == true
                    }
                }
            }
        } catch (_: Throwable) { /* fall through */ }
        // Fallback: Bukkit.
        return Bukkit.getWorld(worldName) != null
    }

    /**
     * Ensures [worldName] is loaded by MV. If MV is absent or fails, falls
     * back to Bukkit's `WorldCreator(worldName).createWorld()`.
     */
    fun loadWorld(worldName: String): Boolean {
        if (isWorldLoaded(worldName)) return true
        if (!available) {
            return try {
                org.bukkit.WorldCreator.name(worldName).createWorld() != null
            } catch (e: Throwable) {
                log(Level.WARNING, "Bukkit failed to load world '$worldName': ${e.message}")
                false
            }
        }
        try {
            core4LoadWorldMethod?.invoke(core4MVWorldManager, worldName)?.let {
                log(Level.INFO, "MV 4.x loaded world '$worldName'.")
                return true
            }
        } catch (e: Throwable) {
            log(Level.WARNING, "MV 4.x loadWorld failed for '$worldName': ${e.message}")
        }
        try {
            core5LoadWorld?.invoke(core5WorldManager, worldName)
            log(Level.INFO, "MV 5.x loaded world '$worldName'.")
            return true
        } catch (e: Throwable) {
            log(Level.WARNING, "MV 5.x loadWorld failed for '$worldName': ${e.message}")
        }
        return isWorldLoaded(worldName)
    }

    /**
     * Teleports [player] to [location]. Uses MV's safe-teleporter when
     * available (respects MV per-world rules), otherwise Bukkit's
     * [Player.teleport]. Never throws.
     */
    fun teleport(player: Player, location: Location): Boolean {
        if (!available) {
            return try {
                player.teleport(location)
            } catch (e: Throwable) {
                log(Level.WARNING, "Bukkit teleport failed: ${e.message}")
                false
            }
        }
        try {
            core4TeleportMethod?.let {
                // MV 4 signature: safeTeleport(TransportDependant teleporter, Player p, Location loc)
                it.invoke(core4Instance, player, location)
                return true
            }
        } catch (e: Throwable) {
            log(Level.FINE, "MV 4.x teleport failed, falling back to Bukkit: ${e.message}")
        }
        try {
            core5Teleport?.let {
                it.invoke(core5Teleporter, player, location)
                return true
            }
        } catch (e: Throwable) {
            log(Level.FINE, "MV 5.x teleport failed, falling back to Bukkit: ${e.message}")
        }
        return try {
            player.teleport(location)
        } catch (e: Throwable) {
            log(Level.WARNING, "Bukkit teleport failed: ${e.message}")
            false
        }
    }

    private fun log(level: Level, msg: String) {
        if (level == Level.INFO && !verbose) return
        plugin.logger.log(level, "[MoonXBall][MV] $msg")
    }
}
