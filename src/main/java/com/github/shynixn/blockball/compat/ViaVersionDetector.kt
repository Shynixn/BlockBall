package com.github.shynixn.blockball.compat

/**
 * Soft-depend wrapper around ViaVersion that lets MoonXBall detect a
 * connected player's actual client protocol version (e.g. a 1.8.9 client
 * joining a 1.21.6 server through ViaVersion + ViaBackwards + ViaRewind).
 *
 * Reflection-only: ViaVersion is `compileOnly`-resolved at runtime via
 * `plugin.yml.softdepend`. If ViaVersion is not installed, every query
 * returns [ProtocolVersion.UNKNOWN] and callers should treat that as
 * "modern client" (use the modern scoreboard path).
 */
object ViaVersionDetector {

    /**
     * Coarse-grained protocol version bucket. We only care whether the client
     * is "legacy" (1.8.x), since that gates the scoreboard adapter and
     * hex-color down-sampling.
     */
    enum class ProtocolVersion {
        UNKNOWN,
        LEGACY_1_8,    // 1.8.0 .. 1.8.9 (protocol 47)
        LEGACY_1_9_TO_1_12, // protocols 107..340
        MODERN,        // 1.13+
    }

    @Volatile
    private var viaApi: Any? = null

    @Volatile
    private var cachedGetPlayerVersionMethod: java.lang.reflect.Method? = null

    @Volatile
    private var initialised = false

    @Volatile
    private var viaPresent = false

    fun init() {
        initialised = true
        try {
            val viaClass = Class.forName("us.myles.ViaVersion.api.Via")
            // ViaVersion 4.x+: Via.getAPI(). 3.x: same API.
            val getAPI = viaClass.getMethod("getAPI")
            viaApi = getAPI.invoke(null)
            cachedGetPlayerVersionMethod = viaApi?.javaClass?.getMethod("getPlayerVersion", java.util.UUID::class.java)
            viaPresent = viaApi != null && cachedGetPlayerVersionMethod != null
        } catch (_: Throwable) {
            // ViaVersion 5.x moved packages; try the new location too.
            try {
                val viaClass = Class.forName("com.viaversion.viaversion.api.Via")
                val getAPI = viaClass.getMethod("getAPI")
                viaApi = getAPI.invoke(null)
                cachedGetPlayerVersionMethod = viaApi?.javaClass?.getMethod("getPlayerVersion", java.util.UUID::class.java)
                viaPresent = viaApi != null && cachedGetPlayerVersionMethod != null
            } catch (_: Throwable) {
                viaPresent = false
            }
        }
    }

    /** True iff ViaVersion is installed and its API loaded. */
    val isAvailable: Boolean get() = initialised && viaPresent

    /**
     * Returns the client protocol version bucket for [playerUuid]. Returns
     * [ProtocolVersion.UNKNOWN] if ViaVersion is absent or the player is
     * unknown to it.
     */
    fun protocolVersion(playerUuid: java.util.UUID): ProtocolVersion {
        if (!viaPresent) return ProtocolVersion.UNKNOWN
        val api = viaApi ?: return ProtocolVersion.UNKNOWN
        val method = cachedGetPlayerVersionMethod ?: return ProtocolVersion.UNKNOWN
        return try {
            val proto = method.invoke(api, playerUuid) as? Int ?: return ProtocolVersion.UNKNOWN
            when (proto) {
                47 -> ProtocolVersion.LEGACY_1_8
                in 107..340 -> ProtocolVersion.LEGACY_1_9_TO_1_12
                else -> if (proto > 340) ProtocolVersion.MODERN else ProtocolVersion.UNKNOWN
            }
        } catch (_: Throwable) {
            ProtocolVersion.UNKNOWN
        }
    }

    /**
     * Convenience: is the player on a legacy 1.8.x client?
     */
    fun isLegacy1_8(playerUuid: java.util.UUID): Boolean =
        protocolVersion(playerUuid) == ProtocolVersion.LEGACY_1_8
}
