package com.github.shynixn.blockball.impl

import com.github.shynixn.mccoroutine.folia.isFoliaLoaded
import com.github.shynixn.mcutils.common.Version
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method

private var teleportMethodRef: Method? = null

fun Player.teleportCompat(plugin: Plugin, location: Location) {
    if (plugin.isFoliaLoaded()) {
        if (teleportMethodRef == null) {
            teleportMethodRef = Entity::class.java.getDeclaredMethod(
                "teleportAsync", Location::class.java
            )
        }

        teleportMethodRef!!.invoke(player, location)
    } else {
        teleport(location)
    }
}

fun Player.setInventoryContentsSecure(items: List<ItemStack?>) {
    val player = this
    // There is a bug in 1.21.6. which returns a too many item array in getContents which causes bugs in setContents.
    var i = 0
    if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
        while (i < items.size && i < 41) {
            player.inventory.setItem(i, items[i])
            i++
        }
    } else {
        while (i < items.size && i < 40) {
            player.inventory.setItem(i, items[i])
            i++
        }
    }
}
