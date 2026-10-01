package com.github.shynixn.blockball.listeners

import com.github.shynixn.blockball.compat.HandCompat
import com.github.shynixn.blockball.compat.MaterialCompat
import com.github.shynixn.blockball.compat.ServerVersion
import com.github.shynixn.blockball.config.MoonXConfig
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.blockball.gui.GameSelectionGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import java.util.UUID

/**
 * Manages the "Join An Game" wooden sword in lobby players' hotbars.
 *
 * Behaviour:
 *   - Item is placed in hotbar slot [MoonXConfig.joinItemSlot] (default
 *     index 3 = the UI "slot 4").
 *   - Item is restored on join, respawn, world change to a lobby world,
 *     and game leave.
 *   - Item is removed when the player joins a game.
 *   - Item is non-droppable: drop (Q), inventory click/drag, number-key
 *     swap, shift-click, offhand swap (1.9+) are all cancelled.
 *   - Item is removed from death drops.
 *   - Right-click AND left-click (air or block) open the GUI. The event
 *     is cancelled so the sword does not break blocks or hit entities.
 *   - Dual-hand events (1.9+) are de-bounced so the GUI opens only once.
 *
 * Identification:
 *   - On 1.14+ (PersistentDataContainer available): a namespaced key with
 *     [MoonXConfig.joinItemMarker] is stored on the item.
 *   - On 1.8 / 1.9-1.13: a hidden lore string with the marker is used
 *     (less secure but no NMS dependency).
 */
class JoinItemListener(
    private val plugin: Plugin,
    private val config: MoonXConfig,
    private val gui: GameSelectionGUI,
    private val serverVersion: ServerVersion,
) : Listener {

    /** Players currently considered "in a game"; they should NOT hold the sword. */
    private val inGamePlayers: MutableSet<UUID> = java.util.concurrent.newKeySet()

    /** Debounce per-player for dual-hand interact events. */
    private val interactDebounce: MutableMap<UUID, Long> = java.util.concurrent.ConcurrentHashMap()

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        // Slight delay so the player's inventory is fully initialised.
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            giveSword(event.player)
        }, 5L)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onRespawn(event: PlayerRespawnEvent) {
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            giveSword(event.player)
        }, 5L)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.player == null) return
        val player = event.player
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            // Re-evaluate sword eligibility on world change.
            if (event.from.world != event.to?.world) {
                if (shouldHoldSword(player)) giveSword(player) else removeSword(player)
            }
        }, 5L)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        if (!isJoinItem(item)) return

        // Cancel the event so the sword does not break blocks or hit entities.
        event.isCancelled = true

        // Debounce dual-hand firing on 1.9+.
        if (HandCompat.hasDualHandFiring) {
            val now = System.currentTimeMillis()
            val last = interactDebounce[player.uniqueId] ?: 0L
            if (now - last < 200L) return
            interactDebounce[player.uniqueId] = now
        } else {
            // On 1.8 only main hand fires; still debounce once per 200ms.
            val now = System.currentTimeMillis()
            val last = interactDebounce[player.uniqueId] ?: 0L
            if (now - last < 200L) return
            interactDebounce[player.uniqueId] = now
        }

        // Both LEFT and RIGHT click (air or block) open the GUI.
        when (event.action) {
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                player.updateInventory()
                gui.open(player)
            }
            else -> { /* physical / unknown; ignore */ }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDrop(event: PlayerDropItemEvent) {
        if (isJoinItem(event.itemDrop.itemStack)) {
            event.isCancelled = true
            event.player.updateInventory()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val cursor = event.cursor
        val current = event.currentItem
        // Cancel any click that touches our sword (whether as cursor or current).
        if (cursor != null && isJoinItem(cursor)) {
            event.isCancelled = true
            player.updateInventory()
            return
        }
        if (current != null && isJoinItem(current)) {
            // Block number-key swap, shift-click, and any other move.
            event.isCancelled = true
            player.updateInventory()
            return
        }
        // Also block shift-click INTO the sword slot.
        if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT) {
            val slot = config.joinItemSlot()
            if (event.clickedInventory is PlayerInventory) {
                val inv = event.clickedInventory as PlayerInventory
                if (inv.getItem(slot) != null && isJoinItem(inv.getItem(slot)!!)) {
                    event.isCancelled = true
                    player.updateInventory()
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        for (item in event.newItems.values) {
            if (isJoinItem(item)) {
                event.isCancelled = true
                player.updateInventory()
                return
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onItemHeld(event: PlayerItemHeldEvent) {
        // Don't prevent switching TO the sword slot; we just want to make
        // sure the sword stays in its configured slot. If the player tried
        // to swap it via number keys, onInventoryClick already cancelled it.
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(event: PlayerDeathEvent) {
        // Remove our sword from death drops so it doesn't litter the ground.
        event.drops.removeAll { isJoinItem(it) }
    }

    /**
     * BlockBall fires [GameJoinEvent] when a player joins an arena.
     * We mark them in-game so the sword is removed from their hotbar.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onGameJoin(event: GameJoinEvent) {
        org.bukkit.Bukkit.getScheduler().runTask(plugin, Runnable {
            markInGame(event.player)
        })
    }

    /**
     * BlockBall fires [GameLeaveEvent] when a player leaves an arena.
     * We clear the in-game flag so the sword is given back.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onGameLeave(event: GameLeaveEvent) {
        org.bukkit.Bukkit.getScheduler().runTask(plugin, Runnable {
            clearInGame(event.player)
        })
    }

    /**
     * Public API: marks [player] as "in a game" (sword removed).
     * Called by GameListener when the player joins an arena.
     */
    fun markInGame(player: Player) {
        inGamePlayers.add(player.uniqueId)
        removeSword(player)
    }

    /**
     * Public API: clears the "in a game" flag and gives back the sword.
     * Called by GameListener when the player leaves an arena.
     */
    fun clearInGame(player: Player) {
        inGamePlayers.remove(player.uniqueId)
        org.bukkit.Bukkit.getScheduler().runTask(plugin, Runnable { giveSword(player) })
    }

    /**
     * Gives the sword to [player] if they are in a lobby and not in a game.
     * Replaces any existing sword in the configured slot.
     */
    fun giveSword(player: Player) {
        if (inGamePlayers.contains(player.uniqueId)) return
        if (!shouldHoldSword(player)) return
        val stack = buildSword() ?: return
        val slot = config.joinItemSlot()
        val inv = player.inventory
        // Clear any existing sword first (so we don't double up).
        for (i in 0 until inv.size) {
            val existing = inv.getItem(i) ?: continue
            if (isJoinItem(existing)) inv.setItem(i, null)
        }
        inv.setItem(slot, stack)
        player.updateInventory()
    }

    /** Removes any sword currently in the player's inventory. */
    fun removeSword(player: Player) {
        val inv = player.inventory
        var changed = false
        for (i in 0 until inv.size) {
            val existing = inv.getItem(i) ?: continue
            if (isJoinItem(existing)) {
                inv.setItem(i, null)
                changed = true
            }
        }
        if (changed) player.updateInventory()
    }

    /**
     * A player holds the sword iff:
     *   - They are NOT in a MoonXBall game (when [giveWhenNotInGame] is true), AND
     *   - Either `lobbyWorlds` is empty OR they are in one of those worlds.
     */
    private fun shouldHoldSword(player: Player): Boolean {
        if (config.giveSwordWhenNotInGame() && inGamePlayers.contains(player.uniqueId)) return false
        val worlds = config.lobbyWorlds()
        if (worlds.isEmpty()) return true
        return worlds.any { it.equals(player.world.name, ignoreCase = true) }
    }

    private fun buildSword(): ItemStack? {
        val materialName = config.joinItemMaterial()
        val material = MaterialCompat.byNameOrAir(materialName)
        if (material == Material.AIR) {
            // Fallback to legacy WOOD_SWORD resolution.
            MaterialCompat.resolve(com.github.shynixn.blockball.compat.LogicalMaterial.WOOD_SWORD)
                ?.let { return buildStack(it) }
            plugin.logger.warning("[MoonXBall] Could not resolve material '$materialName' for join item.")
            return null
        }
        return buildStack(material)
    }

    private fun buildStack(material: Material): ItemStack {
        val stack = ItemStack(material, 1)
        val meta = stack.itemMeta ?: return stack
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.joinItemDisplayName()))
        val loreRaw = config.joinItemLore()
        val lore = loreRaw.map { ChatColor.translateAlternateColorCodes('&', it) }.toMutableList()
        // On 1.8/1.9-1.13 we don't have PersistentDataContainer, so we hide
        // the marker in the lore (invisible to the player via ChatColor).
        if (!serverVersion.hasPersistentDataContainer) {
            // Append an invisible marker line.
            lore.add(ChatColor.translateAlternateColorCodes('&', "&r&8&k${config.joinItemMarker()}"))
        }
        meta.lore = lore
        if (config.joinItemGlow()) {
            // 1.8 lacks ItemFlag.HIDE_ENCHANTS; we just add an enchantment.
            try {
                meta.addEnchant(Enchantment.DURABILITY, 1, true)
            } catch (_: Throwable) {
                // Enchantment.DURABILITY exists on 1.8+; should be safe.
            }
        }
        // On 1.14+ use PersistentDataContainer for the marker.
        if (serverVersion.hasPersistentDataContainer) {
            try {
                val namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey")
                val keyCtor = namespacedKeyClass.getConstructor(org.bukkit.plugin.Plugin::class.java, String::class.java)
                val key = keyCtor.newInstance(plugin, config.joinItemMarker())
                val pdc = meta.javaClass.getMethod("getPersistentDataContainer").invoke(meta)
                val byteTagClass = Class.forName("org.bukkit.persistence.PersistentDataType")
                val byteType = byteTagClass.getField("BYTE").get(null)
                pdc.javaClass.getMethod("set", namespacedKeyClass, byteTagClass, Any::class.java)
                    .invoke(pdc, key, byteType, 1.toByte())
            } catch (_: Throwable) {
                // PersistentDataContainer API not available at runtime; fallback to lore.
                lore.add(ChatColor.translateAlternateColorCodes('&', "&r&8&k${config.joinItemMarker()}"))
            }
        }
        stack.itemMeta = meta
        return stack
    }

    /**
     * True iff [stack] is the Join-An-Game sword (matches either the
     * PersistentDataContainer marker or the lore marker, depending on
     * the server version).
     */
    fun isJoinItem(stack: ItemStack?): Boolean {
        if (stack == null || stack.type == Material.AIR) return false
        val meta = stack.itemMeta ?: return false
        // Modern: PersistentDataContainer marker.
        if (serverVersion.hasPersistentDataContainer) {
            try {
                val namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey")
                val keyCtor = namespacedKeyClass.getConstructor(org.bukkit.plugin.Plugin::class.java, String::class.java)
                val key = keyCtor.newInstance(plugin, config.joinItemMarker())
                val pdc = meta.javaClass.getMethod("getPersistentDataContainer").invoke(meta) ?: return false
                val byteTagClass = Class.forName("org.bukkit.persistence.PersistentDataType")
                val byteType = byteTagClass.getField("BYTE").get(null)
                val has = pdc.javaClass.getMethod("has", namespacedKeyClass, byteTagClass)
                    .invoke(pdc, key, byteType) as? Boolean ?: false
                if (has) return true
            } catch (_: Throwable) { /* fall through to lore check */ }
        }
        // Legacy: lore marker.
        val lore = meta.lore ?: return false
        val marker = config.joinItemMarker()
        return lore.any { ChatColor.stripColor(it)?.contains(marker) == true }
    }
}
