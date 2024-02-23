@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.BlockSelectionService
import com.github.shynixn.blockball.contract.ItemTypeService
import com.github.shynixn.blockball.enumeration.MaterialType
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class BlockSelectionServiceImpl @Inject constructor(
    private val itemTypeService: ItemTypeService,
    private val plugin: Plugin
) : BlockSelectionService {
    private val axeName =
        ChatColor.WHITE.toString() + ChatColor.BOLD + ">>" + ChatColor.YELLOW + "BlockBall" + ChatColor.WHITE + ChatColor.BOLD + "<<"
    private val playerSelection = HashMap<Player, Array<Location?>>()
    private val rightClickSelectionCahe = HashSet<Player>()

    /**
     * Selects the left location internally.
     */
    override fun selectLeftLocation(player: Player, location: Location): Boolean {
        if (selectLocation(player, location, 0)) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Leftclick: " + location.blockX + " " + location.blockY + " " + location.blockZ)
            return true
        }

        return false
    }

    /**
     * Selects the right location internally.
     */
    override fun selectRightLocation(player: Player, location: Location): Boolean {
        if (!rightClickSelectionCahe.contains(player) && selectLocation(player, location, 1)) {
            player.sendMessage(ChatColor.YELLOW.toString() + "Rightclick: " + location.blockX + " " + location.blockY + " " + location.blockZ)

            rightClickSelectionCahe.add(player)
            plugin.launch {
                delay(10.ticks)
                rightClickSelectionCahe.remove(player)
            }

            return true
        }

        return false
    }

    /**
     * Returns the leftclick internal or worledit selection of the given [player].
     */
    override fun getLeftClickLocation(player: Player): Optional<Location> {
        return getCompatibilitySelection(player, 0)
    }

    /**
     * Returns the rightclick internal or worledit selection of the given [player].
     */
    override fun getRightClickLocation(player: Player): Optional<Location> {
        return getCompatibilitySelection(player, 1)
    }

    /**
     * Cleans open resources.
     */
    override fun cleanResources(player: Player) {
        if (playerSelection.containsKey(player)) {
            playerSelection.remove(player)
        }
    }

    /**
     * Select location and returns if success.
     */
    private fun selectLocation(player: Player, location: Location, index: Int): Boolean {
        if (!player.hasPermission(Permission.ADMIN.permission)) {
            return false
        }

        @Suppress("DEPRECATION")
        if (player.itemInHand == null) {
            return false
        }

        val itemStack = player.itemInHand

        if (itemTypeService.findItemType<Any>(itemStack.type) != itemTypeService.findItemType(MaterialType.GOLDEN_AXE)
            || itemStack.itemMeta == null
        ) {
            return false
        }

        val itemMeta = itemStack.itemMeta!!

        if (itemMeta.displayName == null) {
            return false
        }

        if (itemMeta.displayName.stripChatColors() != this.axeName.stripChatColors()) {
            return false
        }

        if (!playerSelection.containsKey(player)) {
            playerSelection[player] = arrayOfNulls(2)
        }

        playerSelection[player]!![index] = location

        return true
    }

    /**
     * Gives the given [player] the selection tool if he does not
     * already have it.
     */
    override fun setSelectionToolForPlayer(player: Player) {
        for (i in 0..player.inventory.contents.size) {
            if (player.inventory.contents[0] != null) {
                val item = player.inventory.contents[0]

                if (itemTypeService.findItemType<Any>(item.type) == itemTypeService.findItemType(MaterialType.GOLDEN_AXE) && item.itemMeta!!.displayName != null
                    && item.itemMeta!!.displayName.stripChatColors() == this.axeName.stripChatColors()
                ) {
                    return
                }
            }
        }

        val itemStack =
            ItemStack(itemTypeService.findItemType<Material>(MaterialType.GOLDEN_AXE.MinecraftNumericId.toString()))
        val meta = itemStack.itemMeta!!
        meta.setDisplayName(axeName.translateChatColors())
        itemStack.itemMeta = meta
        player.inventory.addItem(itemStack)
        player.sendMessage("Take a look into your inventory. Use this golden axe for selection.")
    }

    /**
     * Returns the compaitiblity selection.
     */
    private fun getCompatibilitySelection(player: Player, index: Int): Optional<Location> {
        if (playerSelection.containsKey(player) && playerSelection[player]!![index] != null) {
            return Optional.of(playerSelection[player]!![index]!!)
        }

        return Optional.empty()
    }
}
