@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.service.ItemTypeService
import com.github.shynixn.blockball.api.persistence.entity.Item
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.blockball.core.logic.persistence.entity.ItemEntity
import com.google.inject.Inject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class ItemTypeServiceImpl @Inject constructor(private val version: Version) : ItemTypeService {
    private val cache = HashMap<Any, Any>()

    /**
     * Tries to find the data value of the given hint.
     */
    override fun findItemDataValue(sourceHint: Any): Int {
        if (sourceHint is ItemStack) {
            return sourceHint.durability.toInt()
        }

        if (sourceHint is Int) {
            return sourceHint
        }

        throw IllegalArgumentException("Hint $sourceHint does not exist!")
    }

    /**
     * Converts the given item to an ItemStack.
     */
    override fun <I> toItemStack(item: Item): I {
        val itemStack = ItemStack(findItemType(item.type), 1, item.dataValue.toShort())

        if (itemStack.itemMeta != null) {
            var currentMeta = itemStack.itemMeta

            if (!item.skin.isNullOrEmpty() && currentMeta is SkullMeta) {
                var newSkin = item.skin!!

                if (newSkin.length > 32) {
                    val cls = Class.forName(
                        "org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull".replace(
                            "VERSION",
                            version.bukkitId
                        )
                    )
                    val real = cls.cast(currentMeta)
                    val field = real.javaClass.getDeclaredField("profile")
                    val newSkinProfile = GameProfile(UUID.randomUUID(), null)

                    if (newSkin.contains("textures.minecraft.net")) {
                        if (!newSkin.startsWith("http://")) {
                            newSkin = "http://$newSkin"
                        }

                        newSkin = Base64Coder.encodeString("{textures:{SKIN:{url:\"$newSkin\"}}}")
                    }

                    newSkinProfile.properties.put("textures", Property("textures", newSkin))
                    field.isAccessible = true
                    field.set(real, newSkinProfile)
                    currentMeta = SkullMeta::class.java.cast(real)
                } else {
                    currentMeta.owner = newSkin
                }
            }

            if (item.displayName != null) {
                currentMeta!!.setDisplayName(item.displayName!!.translateChatColors())
            }

            if (item.lore != null) {
                currentMeta!!.lore = item.lore!!.map { l -> l.translateChatColors() }.toMutableList()
            }

            itemStack.itemMeta = currentMeta
        }

        return if (item.unbreakable) {
            val nmsItemStackClass =
                Class.forName("net.minecraft.server.VERSION.ItemStack".replace("VERSION", version.bukkitId))
            val craftItemStackClass =
                Class.forName(
                    "org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack".replace(
                        "VERSION",
                        version.bukkitId
                    )
                )
            val nmsCopyMethod = craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack::class.java)
            val nmsToBukkitMethod = craftItemStackClass.getDeclaredMethod("asBukkitCopy", nmsItemStackClass)

            val nbtTagClass =
                Class.forName("net.minecraft.server.VERSION.NBTTagCompound".replace("VERSION", version.bukkitId))
            val getNBTTag = nmsItemStackClass.getDeclaredMethod("getTag")
            val setNBTTag = nmsItemStackClass.getDeclaredMethod("setTag", nbtTagClass)
            val nbtSetBoolean =
                nbtTagClass.getDeclaredMethod("setBoolean", String::class.java, Boolean::class.javaPrimitiveType)

            val nmsItemStack = nmsCopyMethod.invoke(null, itemStack)
            var nbtTag = getNBTTag.invoke(nmsItemStack)

            if (nbtTag == null) {
                nbtTag = nbtTagClass.newInstance()
            }

            nbtSetBoolean.invoke(nbtTag, "Unbreakable", true)
            setNBTTag.invoke(nmsItemStack, nbtTag)

            return nmsToBukkitMethod.invoke(null, nmsItemStack) as I
        } else {
            itemStack as I
        }
    }

    /**
     * Converts the given itemStack ot an item.
     */
    override fun <I> toItem(itemStack: I): Item {
        require(itemStack is ItemStack)

        val displayName = if (itemStack.itemMeta != null) {
            itemStack.itemMeta!!.displayName
        } else {
            null
        }

        val lore = if (itemStack.itemMeta != null) {
            itemStack.itemMeta!!.lore
        } else {
            null
        }

        val skin = if (itemStack.itemMeta != null && itemStack.itemMeta is SkullMeta) {
            val currentMeta = itemStack.itemMeta as SkullMeta
            val owner = currentMeta.owner

            if (!owner.isNullOrEmpty()) {
                owner
            } else {
                val cls = Class.forName(
                    "org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull".replace(
                        "VERSION",
                        version.bukkitId
                    )
                )
                val real = cls.cast(currentMeta)
                val field = real.javaClass.getDeclaredField("profile")
                field.isAccessible = true
                val profile = field.get(real) as GameProfile?

                if (profile == null) {
                    null
                } else {
                    profile.properties.get("textures").toTypedArray()[0].value
                }
            }
        } else {
            null
        }

        return ItemEntity(
            findItemType<Material>(itemStack).name,
            itemStack.durability.toInt(),
            false,
            displayName,
            lore,
            skin
        )
    }

    /**
     * Tries to find a matching itemType matching the given hint.
     */
    override fun <I> findItemType(sourceHint: Any): I {
        if (cache.containsKey(sourceHint)) {
            return cache[sourceHint]!! as I
        }

        var descHint = sourceHint

        if (descHint is ItemStack) {
            return descHint.type as I
        }

        if (sourceHint is MaterialType) {
            descHint = sourceHint.name
        }

        val intHint: Int? = if (descHint is Int) {
            descHint
        } else if (descHint is String && descHint.toIntOrNull() != null) {
            descHint.toInt()
        } else {
            null
        }

        if (intHint != null) {
            // It is a number.
            val idField = Material::class.java.getDeclaredField("id")
            idField.isAccessible = true

            for (material in Material::class.java.enumConstants) {
                if (idField.get(material) as Int == intHint) {
                    cache[sourceHint] = material
                    return cache[sourceHint]!! as I
                }
            }
        }

        if (descHint is Material) {
            cache[sourceHint] = descHint
            return cache[sourceHint]!! as I
        }

        if (descHint is String) {
            for (material in Material::class.java.enumConstants) {
                try {
                    if (material.name.equals(descHint, true) || ("LEGACY_$descHint" == material.name)) {
                        cache[sourceHint] = material
                        return cache[sourceHint]!! as I
                    }
                } catch (e: Exception) {
                }
            }
        }

        throw IllegalArgumentException("Hint $sourceHint does not exist!")
    }
}