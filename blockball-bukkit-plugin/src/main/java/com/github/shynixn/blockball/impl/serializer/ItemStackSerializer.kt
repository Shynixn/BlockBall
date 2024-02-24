@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.serializer

import com.github.shynixn.blockball.deprecated.YamlSerializer
import java.lang.IllegalArgumentException

@Deprecated("No longer being used")
class ItemStackSerializer : YamlSerializer<Any, Map<String, Any?>> {
    /**
     * Gets called on serialization.
     */
    override fun onSerialization(item: Any): Map<String, Any?> {
        val clazz = Class.forName("org.bukkit.inventory.ItemStack")

        if (clazz::class.java.isInstance(item)) {
            throw IllegalArgumentException("Serialization item is not an Itemstack!")
        }

        return clazz.getDeclaredMethod("serialize").invoke(item) as Map<String, Any?>
    }

    /**
     * Gets called on Deserialization.
     */
    override fun onDeserialization(item: Map<String, Any?>): Any {
        val clazz = Class.forName("org.bukkit.inventory.ItemStack")

        return clazz.getDeclaredMethod("deserialize", Map::class.java).invoke(null, item)
    }
}

