@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.enumeration.Permission
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class BlockSelectionServiceImpl @Inject constructor(
    private val concurrencyService: ConcurrencyService,
    configurationService: ConfigurationService,
    private val itemTypeService: ItemTypeService
) : BlockSelectionService {
    private val axeName =
        ChatColor.WHITE.toString() + ChatColor.BOLD + ">>" + ChatColor.YELLOW + "BlockBall" + ChatColor.WHITE + ChatColor.BOLD + "<<"
    private val playerSelection = HashMap<Player, Array<Location?>>()
    private val prefix = configurationService.findValue<String>("messages.prefix")
    private val rightClickSelectionCahe = HashSet<Player>()

    /**
     * Selects the left location internally.
     */
    override fun <L, P> selectLeftLocation(player: P, location: L): Boolean {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        if (selectLocation(player, location, 0)) {
            player.sendMessage(prefix + ChatColor.YELLOW.toString() + "Leftclick: " + location.blockX + " " + location.blockY + " " + location.blockZ)
            return true
        }

        return false
    }

    /**
     * Selects the right location internally.
     */
    override fun <L, P> selectRightLocation(player: P, location: L): Boolean {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        if (!rightClickSelectionCahe.contains(player) && selectLocation(player, location, 1)) {
            player.sendMessage(prefix + ChatColor.YELLOW.toString() + "Rightclick: " + location.blockX + " " + location.blockY + " " + location.blockZ)

            rightClickSelectionCahe.add(player)
            concurrencyService.runTaskSync(10L) {
                rightClickSelectionCahe.remove(player)
            }

            return true
        }

        return false
    }

    /**
     * Returns the leftclick internal or worledit selection of the given [player].
     */
    override fun <L, P> getLeftClickLocation(player: P): Optional<L> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return getCompatibilitySelection(player, 0) as Optional<L>
    }

    /**
     * Returns the rightclick internal or worledit selection of the given [player].
     */
    override fun <L, P> getRightClickLocation(player: P): Optional<L> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return getCompatibilitySelection(player, 1) as Optional<L>
    }

    /**
     * Cleans open resources.
     */
    override fun <P> cleanResources(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

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
    override fun <P> setSelectionToolForPlayer(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

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

        val itemStack = ItemStack(itemTypeService.findItemType<Material>(MaterialType.GOLDEN_AXE.MinecraftNumericId.toString()))
        val meta = itemStack.itemMeta!!
        meta.setDisplayName(axeName.translateChatColors())
        itemStack.itemMeta = meta
        player.inventory.addItem(itemStack)
        player.sendMessage(prefix + "Take a look into your inventory. Use this golden axe for selection.")
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
