@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.google.inject.Inject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method

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