import com.github.shynixn.mccoroutine.folia.isFoliaLoaded
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method

var pluginMainThreadId = 0L

fun checkForPluginMainThread() {
    if (Thread.currentThread().id != pluginMainThreadId) {
        throw IllegalArgumentException("Entered method not on plugin thread!")
    }
}

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
    while (i < items.size && i < 36) {
        player.inventory.setItem(i, items[i])
        i++
    }
}
