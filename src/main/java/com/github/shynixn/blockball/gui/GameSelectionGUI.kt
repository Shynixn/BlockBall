package com.github.shynixn.blockball.gui

import com.github.shynixn.blockball.compat.MaterialCompat
import com.github.shynixn.blockball.config.MoonXConfig
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.compat.LogicalMaterial
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

/**
 * The "Selection Area" GUI that opens when a player right-clicks the
 * Join-An-Game wooden sword.
 *
 * Behavior:
 *   - Lists every arena as a clickable item showing: display name,
 *     state (waiting / running / full / disabled), player count, min/max.
 *   - Refreshes live while open (throttled to [MoonXConfig.guiRefreshTicks]).
 *   - Clicking an arena item attempts to join that arena.
 *   - All inventory clicks are cancelled (players can't take items out).
 *   - Inventory holder is a [GameSelectionHolder] so we can detect our
 *     own GUI reliably.
 *   - Cleanup is automatic on inventory close.
 *
 * Permissions:
 *   - `BlockMxc.join.<arenaname_lowercase>` (new MoonXBall node).
 *   - `blockball.join.<arenaname_lowercase>` (legacy node, kept for
 *     backwards compat).
 *   - Wildcard `BlockMxc.join.*` (declared in plugin.yml, default true).
 *   - A player without permission sees a locked arena (red barrier,
 *     configured in MX_Blocball2.yml) and clicking it sends the
 *     noPermissionArena message.
 */
class GameSelectionGUI(
    private val plugin: Plugin,
    private val config: MoonXConfig,
    private val gameService: GameService,
) : Listener {

    /** Currently-open GUIs (player UUID -> inventory), used for refresh. */
    private val openGUIs: MutableMap<UUID, Inventory> = java.util.concurrent.ConcurrentHashMap()

    /** Per-refresh task. */
    private var task: BukkitTask? = null

    /**
     * Starts the periodic refresh task. Call once on enable.
     */
    fun start() {
        stop()
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { refreshAll() }, 20L, config.guiRefreshTicks())
    }

    /** Stops the refresh task. Call on disable. */
    fun stop() {
        task?.cancel()
        task = null
        openGUIs.clear()
    }

    /**
     * Opens the Selection Area GUI for [player].
     */
    fun open(player: Player) {
        val size = config.guiSize()
        val title = ChatColor.translateAlternateColorCodes('&', config.guiTitle())
        val inv = Bukkit.createInventory(GameSelectionHolder(), size, title)
        openGUIs[player.uniqueId] = inv
        renderInto(inv, player)
        player.openInventory(inv)
    }

    /**
     * Called when a player closes a GUI; cleans up our tracking.
     */
    fun onClose(player: Player) {
        openGUIs.remove(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder ?: return
        if (holder !is GameSelectionHolder) return
        // Cancel EVERY click inside our GUI (no item stealing).
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val clicked = event.currentItem ?: return
        val arenaName = readArenaName(clicked) ?: return

        // Resolve the arena: try internal name first, then display name.
        val game0 = gameService.getAll().firstOrNull { it.arena.name == arenaName }
            ?: gameService.getAll().firstOrNull { it.arena.displayName == arenaName }
        if (game0 == null) {
            player.sendMessage(replaceArena(ChatColor.translateAlternateColorCodes('&', config.messageArenaNotFound()), arenaName))
            return
        }
        val arena = game0.arena

        // Check permissions.
        if (!hasJoinPermission(player, arena.name)) {
            player.sendMessage(replaceArena(ChatColor.translateAlternateColorCodes('&', config.messageNoPermissionArena()), arena.name))
            playSound(player, "noPermission")
            return
        }
        // Check state.
        if (!arena.enabled) {
            player.sendMessage(replaceArena(ChatColor.translateAlternateColorCodes('&', config.messageArenaDisabled()), arena.name))
            return
        }
        if (game0.status == GameState.RUNNING || isFull(game0)) {
            player.sendMessage(replaceArena(ChatColor.translateAlternateColorCodes('&', config.messageArenaFull()), arena.name))
            return
        }
        // Join via the command so all existing checks fire (GameListener etc.).
        Bukkit.dispatchCommand(player, "blockball join ${arena.name}")
        if (config.guiCloseOnJoin()) {
            player.closeInventory()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder ?: return
        if (holder !is GameSelectionHolder) return
        val player = event.player as? Player ?: return
        onClose(player)
    }

    /**
     * Re-renders every open GUI with the latest arena state.
     */
    private fun refreshAll() {
        for ((uuid, inv) in openGUIs.toMap()) {
            val player = Bukkit.getPlayer(uuid) ?: continue
            if (!player.isOnline) {
                openGUIs.remove(uuid)
                continue
            }
            renderInto(inv, player)
            player.updateInventory()
        }
    }

    private fun renderInto(inv: Inventory, viewer: Player) {
        inv.clear()
        // Fill background.
        val fillerMat = MaterialCompat.byNameOrAir(config.guiFillerMaterial())
        if (fillerMat != Material.AIR) {
            val filler = ItemStack(fillerMat)
            val meta = filler.itemMeta
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.guiFillerDisplayName()))
                filler.itemMeta = meta
            }
            for (i in 0 until inv.size) inv.setItem(i, filler)
        }
        // Render each arena in its own slot.
        val games = gameService.getAll()
        for ((idx, game) in games.withIndex()) {
            if (idx >= inv.size) break
            val stack = buildArenaItem(game, viewer) ?: continue
            inv.setItem(idx, stack)
        }
    }

    private fun buildArenaItem(game: SoccerGame, viewer: Player): ItemStack? {
        val arena = game.arena
        val display = if (arena.displayName.isNotBlank()) arena.displayName else arena.name
        val state = when {
            !arena.enabled -> "DISABLED"
            game.status == GameState.RUNNING -> "RUNNING"
            isFull(game) -> "FULL"
            else -> "WAITING"
        }
        val locked = !hasJoinPermission(viewer, arena.name)
        val matName = when {
            locked -> config.guiArenaLockedMaterial()
            state == "DISABLED" -> config.guiArenaDisabledMaterial()
            state == "RUNNING" -> config.guiArenaRunningMaterial()
            state == "FULL" -> config.guiArenaFullMaterial()
            else -> config.guiArenaWaitingMaterial()
        }
        val mat = MaterialCompat.byNameOrAir(matName)
        if (mat == Material.AIR) return null
        val stack = ItemStack(mat)
        val meta = stack.itemMeta ?: return stack
        val playerName = ChatColor.translateAlternateColorCodes('&', config.guiArenaDisplayNameTemplate())
            .replace("%arena_display%", ChatColor.translateAlternateColorCodes('&', display))
            .replace("%arena_name%", arena.name)
            .replace("%arena_state%", state)
            .replace("%arena_players%", game.getPlayers().size.toString())
            .replace("%arena_maxplayers%", (arena.meta.blueTeamMeta.maxAmount + arena.meta.redTeamMeta.maxAmount).toString())
            .replace("%arena_minplayers%", (arena.meta.blueTeamMeta.minAmount + arena.meta.redTeamMeta.minAmount).toString())
            .replace("%arena_locked%", if (locked) "yes" else "no")
        meta.setDisplayName(playerName)
        // Lore with placeholders.
        val lore = config.guiArenaLore().map { line ->
            ChatColor.translateAlternateColorCodes('&', line)
                .replace("%arena_display%", ChatColor.translateAlternateColorCodes('&', display))
                .replace("%arena_name%", arena.name)
                .replace("%arena_state%", state)
                .replace("%arena_players%", game.getPlayers().size.toString())
                .replace("%arena_maxplayers%", (arena.meta.blueTeamMeta.maxAmount + arena.meta.redTeamMeta.maxAmount).toString())
                .replace("%arena_minplayers%", (arena.meta.blueTeamMeta.minAmount + arena.meta.redTeamMeta.minAmount).toString())
                .replace("%arena_locked%", if (locked) "yes" else "no")
        }.toMutableList()
        // Hide the arena name on the last line for click-routing (invisible).
        lore.add(ChatColor.translateAlternateColorCodes('&', "&r&8&k${arena.name}"))
        meta.lore = lore
        stack.itemMeta = meta
        return stack
    }

    /** Reads back the hidden arena name from a clicked item. */
    private fun readArenaName(stack: ItemStack): String? {
        val meta = stack.itemMeta ?: return null
        val lore = meta.lore ?: return null
        // The last lore line is "&r&8&k<name>" — strip color codes & markers.
        val last = lore.lastOrNull() ?: return null
        val stripped = ChatColor.stripColor(last) ?: return null
        // Strip the leading invisible-char markers (we used &k which renders
        // as random glyphs; in lore storage it's just the raw string after
        // color codes are stripped).
        return stripped.trim().takeIf { it.isNotEmpty() }
    }

    private fun hasJoinPermission(player: Player, arenaName: String): Boolean {
        val lower = arenaName.lowercase()
        // Wildcard check (declared in plugin.yml with default true).
        if (player.hasPermission("${config.permissionJoinNode()}.*")) return true
        if (player.hasPermission("${config.permissionLegacyJoinNode()}.*")) return true
        // Per-arena check on both nodes.
        if (player.hasPermission("${config.permissionJoinNode()}.$lower")) return true
        if (player.hasPermission("${config.permissionLegacyJoinNode()}.$lower")) return true
        // Legacy enum check (kept for backwards compat).
        if (player.hasPermission(Permission.JOIN.permission.replace("[name]", lower))) return true
        return false
    }

    private fun isFull(game: SoccerGame): Boolean {
        val max: Int = game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount
        return game.getPlayers().size >= max
    }

    private fun replaceArena(message: String, arenaName: String): String =
        message.replace("%arena_name%", arenaName)
            .replace("%arena_display%", arenaName)

    private fun playSound(player: Player, type: String) {
        try {
            val soundName = when (type) {
                "noPermission" -> config.soundNoPermissionName()
                "openGui" -> config.soundOpenGuiName()
                "joinArena" -> config.soundJoinArenaName()
                else -> return
            }
            val vol = when (type) {
                "noPermission" -> config.soundNoPermissionVolume()
                "openGui" -> config.soundOpenGuiVolume()
                "joinArena" -> config.soundJoinArenaVolume()
                else -> 1f
            }
            val pitch = when (type) {
                "noPermission" -> config.soundNoPermissionPitch()
                "openGui" -> config.soundOpenGuiPitch()
                "joinArena" -> config.soundJoinArenaPitch()
                else -> 1f
            }
            // Try direct Sound.valueOf; fall back to compat lookup.
            val soundEnum = try {
                org.bukkit.Sound.valueOf(soundName)
            } catch (_: Throwable) {
                val logical = when (type) {
                    "noPermission" -> com.github.shynixn.blockball.compat.LogicalSound.BLOCK_ANVIL_LAND
                    "openGui" -> com.github.shynixn.blockball.compat.LogicalSound.UI_BUTTON_CLICK
                    "joinArena" -> com.github.shynixn.blockball.compat.LogicalSound.ENTITY_EXPERIENCE_ORB_PICKUP
                    else -> return
                }
                com.github.shynixn.blockball.compat.SoundCompat.resolve(logical) ?: return
            }
            player.playSound(player.location, soundEnum, vol, pitch)
        } catch (_: Throwable) { /* sound errors are non-fatal */ }
    }

    /** Marker holder so we can detect our own GUIs. Returns a 0-size inventory
     *  as the contract requires a non-null Inventory; the actual rendered
     *  inventory is the one we create via Bukkit.createInventory(...). */
    class GameSelectionHolder : org.bukkit.inventory.InventoryHolder {
        override fun getInventory(): org.bukkit.inventory.Inventory {
            // Return a tiny 0-slot inventory so the contract is satisfied
            // without allocating a player-visible one.
            return _empty ?: synchronized(LOCK) {
                _empty ?: org.bukkit.Bukkit.createInventory(null, 0, "").also { _empty = it }
            }
        }
        companion object {
            private val LOCK = Any()
            @Volatile private var _empty: org.bukkit.inventory.Inventory? = null
        }
    }
}
