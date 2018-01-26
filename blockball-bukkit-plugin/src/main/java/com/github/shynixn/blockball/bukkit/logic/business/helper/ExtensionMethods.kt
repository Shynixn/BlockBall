package com.github.shynixn.blockball.bukkit.logic.business.helper

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.ball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.entity.BlockBallPlayer
import com.github.shynixn.blockball.api.persistence.entity.Persistenceable
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.logging.Level

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
internal fun ItemStack.setSkin(skin: String) {
    if (this.itemMeta is SkullMeta) {
        val skullMeta = this.itemMeta as SkullMeta
        skullMeta.owner = skin
        this.itemMeta = skullMeta
    }
}

internal fun ItemStack.setColor(color: Color): ItemStack {
    if (this.itemMeta is LeatherArmorMeta) {
        val leatherMeta = this.itemMeta as LeatherArmorMeta
        leatherMeta.color = color
        this.itemMeta = leatherMeta
    }
    return this
}

internal fun String.stripChatColors(): String {
    return ChatColor.stripColor(this)
}

internal fun String.convertChatColors(): String {
    return ChatColor.translateAlternateColorCodes('&', this);
}

internal fun Location.toPosition(): IPosition {
    return LocationBuilder(this)
}

internal fun IPosition.toBukkitLocation(): Location {
    return (this as LocationBuilder).toLocation()
}

internal fun Player.compSetGlowing(enabled: Boolean) {
    if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
        try {
            ReflectionUtils.invokeMethodByObject<Any>(player, "setGlowing", arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!), arrayOf(enabled))
        } catch (e: Exception) {
            Config.Logger!!.log(Level.WARNING, "Failed to set player glowing.", e)
        }
    }
}

internal fun BossBarMeta.Style.getNames(): Array<String?> {
    return arrayOfNulls<String>(5)
}

internal fun <T> Persistenceable.clone(): T where T : Persistenceable {
    try {
        val item = this.javaClass.newInstance() as Persistenceable
        var clazz: Class<*>? = this.javaClass
        while (clazz != null) {
            for (field in clazz.declaredFields) {
                field.isAccessible = true
                field.set(item, field.get(this))
            }
            clazz = clazz.superclass
        }
        return item as T
    } catch (e: InstantiationException) {
        throw RuntimeException(e)
    } catch (e: IllegalAccessException) {
        throw RuntimeException(e)
    }
}

internal fun Player.compsetItemInHand(itemStack: ItemStack, offHand: Boolean) {
    if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
        try {
            if (offHand) {
                ReflectionUtils.invokeMethodByObject<Any>(player.inventory, "setItemInOffHand", arrayOf<Class<*>>(ItemStack::class.java), arrayOf<Any>(itemStack))
            } else {
                ReflectionUtils.invokeMethodByObject<Any>(player.inventory, "setItemInMainHand", arrayOf<Class<*>>(ItemStack::class.java), arrayOf<Any>(itemStack))
            }
        } catch (e: Exception) {
            Config.Logger!!.log(Level.WARNING, "Failed to set item in hand.")
            throw RuntimeException(e)
        }
    } else {
        try {
            ReflectionUtils.invokeMethodByObject<Any>(player, "setItemInHand", arrayOf<Class<*>>(ItemStack::class.java), arrayOf<Any>(itemStack))
        } catch (e: Exception) {
            Config.Logger!!.log(Level.WARNING, "Failed to set item in hand.")
            throw RuntimeException(e)
        }
    }
}

internal fun Player.compGetItemInHand(offHand: Boolean): ItemStack? {
    return if (VersionSupport.getServerVersion().isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
        try {
            if (offHand) {
                ReflectionUtils.invokeMethodByObject(player.inventory, "getItemInOffHand", arrayOf(), arrayOf())
            } else {
                ReflectionUtils.invokeMethodByObject<ItemStack>(player.inventory, "getItemInMainHand", arrayOf(), arrayOf())
            }
        } catch (e: Exception) {
            Config.Logger!!.log(Level.WARNING, "Failed to get item in hand.")
            throw RuntimeException(e)
        }
    } else {
        try {
            ReflectionUtils.invokeMethodByObject<ItemStack>(player, "getItemInHand", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Config.Logger!!.log(Level.WARNING, "Failed to get item in hand.")
            throw RuntimeException(e)
        }
    }
}

