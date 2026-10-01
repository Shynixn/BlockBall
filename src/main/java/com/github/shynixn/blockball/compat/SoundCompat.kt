package com.github.shynixn.blockball.compat

import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * Cross-version Sound resolver.
 *
 * 1.8 used enum names like `CLICK` and `ORB_PICKUP`. 1.9+ flattened and
 * renamed most sounds to their `ENTITY_*` / `BLOCK_*` / `UI_*` forms.
 * This helper tries the modern name first and falls back to the legacy
 * one. If neither exists (very old server), the play call is a no-op.
 *
 * Usage:
 * ```
 * SoundCompat.play(player, LogicalSound.UI_BUTTON_CLICK, 1f, 1f)
 * ```
 */
object SoundCompat {

    private data class Names(val modern: String, val legacy: String?)

    private val mapping = mapOf(
        LogicalSound.UI_BUTTON_CLICK to Names("UI_BUTTON_CLICK", "CLICK"),
        LogicalSound.ENTITY_EXPERIENCE_ORB_PICKUP to Names("ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP"),
        LogicalSound.BLOCK_ANVIL_LAND to Names("BLOCK_ANVIL_LAND", "ANVIL_LAND"),
        LogicalSound.ENTITY_PLAYER_LEVELUP to Names("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"),
        LogicalSound.BLOCK_NOTE_BLOCK_PLING to Names("BLOCK_NOTE_BLOCK_PLING", "NOTE_PLING"),
        LogicalSound.ENTITY_ARROW_HIT_PLAYER to Names("ENTITY_ARROW_HIT_PLAYER", "SUCCESSFUL_HIT"),
        LogicalSound.ENTITY_GENERIC_EXPLODE to Names("ENTITY_GENERIC_EXPLODE", "EXPLODE"),
    )

    @Volatile
    private var cache: MutableMap<LogicalSound, Sound?> = HashMap()

    fun init() {
        cache = HashMap()
    }

    fun resolve(logical: LogicalSound): Sound? {
        cache[logical]?.let { return it }
        val names = mapping[logical] ?: return null
        val modern = Sound.values().firstOrNull { it.name == names.modern }
        val resolved = modern ?: names.legacy?.let { legacyName ->
            Sound.values().firstOrNull { it.name == legacyName }
        }
        cache[logical] = resolved
        return resolved
    }

    /**
     * Plays [logical] to [player] at their location. Silently no-ops if the
     * sound does not exist on this server.
     */
    fun play(player: Player, logical: LogicalSound, volume: Float, pitch: Float) {
        val sound = resolve(logical) ?: return
        try {
            player.playSound(player.location, sound, volume, pitch)
        } catch (_: Throwable) {
            // Defensive: some Sound enum entries throw on certain server forks.
        }
    }
}

/**
 * Logical sounds used by MoonXBall GUI / messages.
 */
enum class LogicalSound {
    UI_BUTTON_CLICK,
    ENTITY_EXPERIENCE_ORB_PICKUP,
    BLOCK_ANVIL_LAND,
    ENTITY_PLAYER_LEVELUP,
    BLOCK_NOTE_BLOCK_PLING,
    ENTITY_ARROW_HIT_PLAYER,
    ENTITY_GENERIC_EXPLODE,
}
