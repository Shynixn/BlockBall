@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.google.inject.Inject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.lang.reflect.Method
import java.util.*

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
class ItemServiceImpl @Inject constructor(private val pluginProxy: PluginProxy) : ItemService {
    private val getMaterialFromIdMethod: Method? = {
        val version = pluginProxy.getServerVersion()

        if (version.isVersionSameOrLowerThan(Version.VERSION_1_12_R1)) {
            Material::class.java.getDeclaredMethod("getMaterial", Int::class.javaPrimitiveType)
        }

        null
    }.invoke()

    private val getIdFromMaterialMethod: Method = { Material::class.java.getDeclaredMethod("getId") }.invoke()

    /**
     * Sets the [skin] of the given [itemStack].
     */
    override fun <I> setSkin(itemStack: I, skin: String) {
        if (itemStack !is ItemStack) {
            throw IllegalArgumentException("ItemStack has to be a BukkitItemStack!")
        }

        val currentMeta = itemStack.itemMeta

        if (currentMeta !is SkullMeta) {
            return
        }

        var newSkin = skin
        if (newSkin.contains("textures.minecraft.net")) {
            if (!newSkin.startsWith("http://")) {
                newSkin = "http://$newSkin"
            }

            val newSkinProfile = GameProfile(UUID.randomUUID(), null)
            val cls = findClazz("org.bukkit.craftbukkit.VERSION.inventory.CraftMetaSkull")
            val real = cls.cast(currentMeta)
            val field = real.javaClass.getDeclaredField("profile")

            newSkinProfile.properties.put("textures", Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"$newSkin\"}}}")))
            field.isAccessible = true
            field.set(real, newSkinProfile)
            itemStack.itemMeta = SkullMeta::class.java.cast(real)
        } else {
            @Suppress("DEPRECATION")
            currentMeta.owner = skin
            itemStack.itemMeta = currentMeta
        }
    }

    /**
     * Creates a new itemStack from the given [materialType] [dataValue] [amount].
     */
    override fun <I> createItemStack(materialType: MaterialType, dataValue: Int, amount: Int): I {
        @Suppress("DEPRECATION")
        return ItemStack(getMaterialFromMaterialType<Material>(materialType), amount, dataValue.toShort()) as I
    }

    /**
     * Gets the material from the material type. Throws a [IllegalArgumentException]
     * if mapping is not possible.
     */
    override fun <M> getMaterialFromMaterialType(materialType: MaterialType): M {
        return getMaterialFromNumericValue(materialType.MinecraftNumericId)
    }

    /**
     * Gets the numeric material value. Throws a [IllegalArgumentException]
     * if the numeric value could not get located.
     */
    override fun <M> getNumericMaterialValue(material: M): Int {
        return getIdFromMaterialMethod.invoke(material) as Int
    }

    /**
     * Gets the material from the numeric value.
     * Throws a [IllegalArgumentException] if the numeric value could
     * not get applied to a material.
     */
    override fun <M> getMaterialFromNumericValue(value: Int): M {
        if (getMaterialFromIdMethod != null) {
            return getMaterialFromIdMethod.invoke(null, value) as M
        } else {
            for (material in Material.values()) {
                if (getNumericMaterialValue(material) == value) {
                    return material as M
                }
            }

            throw IllegalArgumentException("Material of numeric value $value could not be found.")
        }
    }
}