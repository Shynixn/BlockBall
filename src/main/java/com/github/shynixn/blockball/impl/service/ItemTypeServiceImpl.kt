@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.ItemTypeService
import com.github.shynixn.blockball.enumeration.MaterialType
import com.github.shynixn.mcutils.common.Version
import com.google.inject.Inject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

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
            descHint = sourceHint.MinecraftNumericId
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
