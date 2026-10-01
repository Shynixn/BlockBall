package com.github.shynixn.blockball.scoreboard

import com.github.shynixn.blockball.compat.ViaVersionDetector
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

/**
 * Side-by-side scoreboard adapter for 1.8.x clients connected via ViaVersion.
 *
 * BlockBall delegates scoreboard rendering to the embedded ShyScoreboard
 * library, which assumes modern client capabilities (hex colors, unlimited
 * line length, number-format hiding). On 1.8.x clients that produces
 * garbled / flickering / dropped lines.
 *
 * This adapter:
 *   1. Detects legacy 1.8 clients via [ViaVersionDetector].
 *   2. Sends a REMOVE packet for the ShyScoreboard-managed objective so
 *      the legacy client does not render the broken one.
 *   3. Sends its own objective under a different name (`moonxball_18`),
 *      with each line split via [LegacyScoreboardLineSplitter] and rendered
 *      as a (prefix, entry, suffix) team entry.
 *   4. Updates only changed lines on each refresh tick (no flicker).
 *   5. Cleans up on quit, world change, game leave, and plugin disable.
 *
 * The scoreboard text is read from the ShyScoreboard config and evaluated
 * by the BlockBall placeholder service, then passed in via [render].
 *
 * Threading: all packet I/O happens on the main thread via a synchronous
 * Bukkit scheduler (legacy clients need ordered packets; async packet
 * sending can reorder and produce flicker).
 */
class LegacyScoreboardController(
    private val plugin: Plugin,
    private val placeholderEvaluator: (Player, String) -> String,
) {

    /** The objective name ShyScoreboard uses for the BlockBall scoreboard. */
    private val shyObjectiveName: String = "blockball_scoreboard"

    /** Our legacy-safe objective name (different so the two don't conflict). */
    private val ourObjectiveName: String = "moonxball_18"

    /** The score number behavior for legacy clients (see MX_Blocball2.yml#legacyClient.scoreNumberBehavior). */
    enum class ScoreNumberBehavior { HIDE, ORDER }

    @Volatile
    private var behavior: ScoreNumberBehavior = ScoreNumberBehavior.ORDER

    @Volatile
    private var stripHex: Boolean = true

    @Volatile
    private var refreshTicks: Long = 5L

    @Volatile
    private var task: BukkitTask? = null

    /** Per-player state: last-sent lines (for diffing). */
    private val playerState: MutableMap<java.util.UUID, PlayerScoreboard> = java.util.concurrent.ConcurrentHashMap()

    /**
     * Lines configured for the BlockBall scoreboard, *before* placeholder
     * resolution. The controller evaluates them per-player on each tick.
     */
    @Volatile
    private var configuredTitle: String = "&aMoonXBall"

    @Volatile
    private var configuredLines: List<String> = emptyList()

    /**
     * Players we are actively rendering to (added by [onJoin] / re-detected
     * on each tick). Players are removed by [onQuit] / [onDisable].
     */
    private val trackedPlayers: MutableSet<java.util.UUID> = java.util.concurrent.newKeySet()

    fun configure(
        title: String,
        lines: List<String>,
        refreshTicks: Long,
        behavior: ScoreNumberBehavior,
        stripHexColors: Boolean,
    ) {
        this.configuredTitle = title
        this.configuredLines = lines
        this.refreshTicks = refreshTicks.coerceAtLeast(1L)
        this.behavior = behavior
        this.stripHex = stripHexColors
    }

    /**
     * Starts the per-tick refresh task. Call once on plugin enable.
     */
    fun start() {
        stop()
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { tick() }, 5L, refreshTicks)
    }

    /**
     * Stops the refresh task and clears all player state. Call on plugin disable.
     */
    fun stop() {
        task?.cancel()
        task = null
        // Snapshot then clear (avoid CME).
        val players = playerState.keys.toList()
        playerState.clear()
        trackedPlayers.clear()
        for (uuid in players) {
            val p = Bukkit.getPlayer(uuid) ?: continue
            removeSafe(p)
        }
    }

    /**
     * Called when a player joins. The controller will auto-detect whether
     * they are a legacy 1.8 client on the next tick.
     */
    fun onJoin(player: Player) {
        trackedPlayers.add(player.uniqueId)
    }

    /**
     * Called when a player quits. Tears down all scoreboard state for them.
     */
    fun onQuit(player: Player) {
        trackedPlayers.remove(player.uniqueId)
        playerState.remove(player.uniqueId)?.let { _ -> removeSafe(player) }
    }

    /**
     * Force a refresh of [player]'s scoreboard on the next tick.
     */
    fun refresh(player: Player) {
        // Just mark them dirty; tick() will re-evaluate.
        playerState[player.uniqueId]?.let { it.dirty = true }
    }

    private fun tick() {
        val toRemove = ArrayList<java.util.UUID>()
        for (uuid in trackedPlayers) {
            val player = Bukkit.getPlayer(uuid)
            if (player == null || !player.isOnline) {
                toRemove.add(uuid)
                continue
            }
            // Only activate the legacy adapter for 1.8.x clients.
            if (!ViaVersionDetector.isLegacy1_8(uuid)) {
                continue
            }
            renderForPlayer(player)
        }
        for (uuid in toRemove) {
            trackedPlayers.remove(uuid)
            playerState.remove(uuid)
        }
    }

    private fun renderForPlayer(player: Player) {
        val state = playerState.computeIfAbsent(player.uniqueId) { PlayerScoreboard() }

        // Resolve placeholders for each configured line.
        val rawLines = configuredLines.map { placeholderEvaluator(player, it) }
        val titleRaw = placeholderEvaluator(player, configuredTitle)

        // Strip hex if requested.
        val safeTitle = if (stripHex) LegacyScoreboardLineSplitter.stripHex(titleRaw) else titleRaw
        val safeLines = rawLines.map { if (stripHex) LegacyScoreboardLineSplitter.stripHex(it) else it }
            .take(15) // 1.8 supports max 15 scoreboard lines.

        // Truncate title to 32 chars.
        val finalTitle = LegacyScoreboardLineSplitter.truncatePreservingColor(safeTitle, 32)

        // Defensive: re-remove the ShyScoreboard-managed objective on every
        // tick. The lib's refresh tick runs every 5 ticks and re-creates
        // its objective; if we don't keep removing it, the 1.8 client will
        // see both objectives (ours + the lib's broken one).
        removeShyObjective(player)

        // First time for this player: create objective.
        if (!state.created) {
            sendObjectiveCreate(player, finalTitle)
            state.created = true
            state.title = finalTitle
        } else if (state.title != finalTitle) {
            sendObjectiveUpdate(player, finalTitle)
            state.title = finalTitle
        }

        // Pad entries to be unique.
        val splitLines = safeLines.map { LegacyScoreboardLineSplitter.split(it) }
        val paddedEntries = LegacyScoreboardLineSplitter.padForUniqueness(splitLines.map { it.entry })

        // Build the new line set.
        val newLines = ArrayList<RenderedLine>(splitLines.size)
        for (i in splitLines.indices) {
            val split = splitLines[i]
            val entry = paddedEntries[i]
            val score = when (behavior) {
                ScoreNumberBehavior.HIDE -> 0
                ScoreNumberBehavior.ORDER -> splitLines.size - i
            }
            newLines += RenderedLine(entry = entry, prefix = split.prefix, suffix = split.suffix, score = score)
        }

        // Diff: remove lines that disappeared, update changed ones, add new ones.
        val oldByEntry = state.lines.associateBy { it.entry }
        val newByEntry = newLines.associateBy { it.entry }

        // Remove lines that are gone.
        for ((entry, _) in oldByEntry) {
            if (entry !in newByEntry) {
                removeScore(player, entry)
                removeTeam(player, entry)
            }
        }
        // Add or update lines.
        for (rendered in newLines) {
            val old = oldByEntry[rendered.entry]
            if (old == null || old != rendered) {
                upsertTeam(player, rendered)
                upsertScore(player, rendered)
            }
        }
        state.lines = newLines
        state.dirty = false
    }

    // ---- Packet sending helpers ----------------------------------------

    private fun sendObjectiveCreate(player: Player, title: String) {
        player.scoreboard.let { sb ->
            try {
                val objective = sb.registerNewObjective(ourObjectiveName, "dummy")
                objective.displayName = title
                objective.displaySlot = org.bukkit.scoreboard.DisplaySlot.SIDEBAR
            } catch (_: Throwable) {
                // If already exists, just retrieve and update.
                val existing = sb.getObjective(ourObjectiveName)
                existing?.displayName = title
                existing?.displaySlot = org.bukkit.scoreboard.DisplaySlot.SIDEBAR
            }
        }
    }

    private fun sendObjectiveUpdate(player: Player, title: String) {
        val sb = player.scoreboard
        val obj = sb.getObjective(ourObjectiveName) ?: return
        obj.displayName = title
    }

    private fun removeShyObjective(player: Player) {
        val sb = player.scoreboard
        val obj = sb.getObjective(shyObjectiveName) ?: return
        try {
            obj.unregister()
        } catch (_: Throwable) {
            // Ignore: may already be unregistered.
        }
    }

    private fun upsertTeam(player: Player, rendered: RenderedLine) {
        val sb = player.scoreboard
        var team = sb.getTeam(rendered.entry)
        if (team == null) {
            team = sb.registerNewTeam(rendered.entry)
            team.addEntry(rendered.entry)
        } else if (!team.hasEntry(rendered.entry)) {
            team.addEntry(rendered.entry)
        }
        team.prefix = rendered.prefix
        team.suffix = rendered.suffix
    }

    private fun removeTeam(player: Player, entry: String) {
        val sb = player.scoreboard
        val team = sb.getTeam(entry) ?: return
        try {
            team.unregister()
        } catch (_: Throwable) { /* ignore */ }
    }

    private fun upsertScore(player: Player, rendered: RenderedLine) {
        val sb = player.scoreboard
        val obj = sb.getObjective(ourObjectiveName) ?: return
        obj.getScore(rendered.entry).score = rendered.score
    }

    private fun removeScore(player: Player, entry: String) {
        val sb = player.scoreboard
        sb.resetScores(entry)
    }

    private fun removeSafe(player: Player) {
        try {
            val sb = player.scoreboard
            sb.getObjective(ourObjectiveName)?.unregister()
            sb.getObjective(shyObjectiveName)?.unregister()
        } catch (_: Throwable) { /* ignore */ }
    }

    // ---- State classes --------------------------------------------------

    private data class RenderedLine(
        val entry: String,
        val prefix: String,
        val suffix: String,
        val score: Int,
    )

    private class PlayerScoreboard {
        var created: Boolean = false
        var title: String = ""
        var lines: List<RenderedLine> = emptyList()
        var dirty: Boolean = true
    }
}
