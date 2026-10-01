package com.github.shynixn.blockball.compat

import org.bukkit.event.player.PlayerInteractEvent

/**
 * Cross-version adapter for the `PlayerInteractEvent#getHand` and off-hand
 * machinery that was introduced in 1.9. On 1.8 the method does not exist
 * at all (and reflection would throw NoSuchMethodException).
 *
 * The whole pattern here is: detect the method ONCE via reflection, then
 * dispatch via the cached [HandCompat] singleton. No reflection happens
 * in the hot path.
 */
object HandCompat {

    /**
     * Logical hand enum. On 1.8 only [MAIN] exists; [OFF] is reported as [MAIN].
     */
    enum class Hand { MAIN, OFF }

    @Volatile
    private var hasHandParameter: Boolean = false

    @Volatile
    private var cachedHandMethod: java.lang.reflect.Method? = null

    @Volatile
    private var cachedEquipmentMethod: java.lang.reflect.Method? = null

    @Volatile
    private var offHandEquipSlot: Any? = null

    /**
     * Initialises reflection caches. Called once at plugin enable.
     */
    fun init(serverVersion: ServerVersion) {
        if (!serverVersion.hasOffHand) {
            hasHandParameter = false
            return
        }
        try {
            val handClass = Class.forName("org.bukkit.inventory.EquipmentSlot")
            cachedHandMethod =
                PlayerInteractEvent::class.java.getMethod("getHand")
            cachedEquipmentMethod =
                Class.forName("org.bukkit.entity.HumanEntity").getMethod("getEquipment")
            offHandEquipSlot = handClass.enumConstants.firstOrNull { (it as Any).toString() == "OFF_HAND" }
            hasHandParameter = cachedHandMethod != null && offHandEquipSlot != null
        } catch (_: Throwable) {
            hasHandParameter = false
        }
    }

    /**
     * Returns the [Hand] that triggered [event], or [MAIN] on legacy servers.
     *
     * For 1.9+ dual-hand event debouncing, callers should compare the result
     * to [Hand.MAIN] and ignore [Hand.OFF] for events that should fire once
     * per click.
     */
    fun hand(event: PlayerInteractEvent): Hand {
        if (!hasHandParameter) return Hand.MAIN
        val method = cachedHandMethod ?: return Hand.MAIN
        return try {
            val slot = method.invoke(event) ?: return Hand.MAIN
            if (slot == offHandEquipSlot) Hand.OFF else Hand.MAIN
        } catch (_: Throwable) {
            Hand.MAIN
        }
    }

    /**
     * True on 1.9+ where dual-hand `PlayerInteractEvent` events fire twice.
     */
    val hasDualHandFiring: Boolean get() = hasHandParameter
}
