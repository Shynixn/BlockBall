@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_10_R1

import com.github.shynixn.blockball.api.business.enumeration.EntityType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.service.EntityRegistrationService
import com.google.inject.Inject

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
class EntityRegistration110R1ServiceImpl @Inject constructor(private val version: Version) : EntityRegistrationService {
    private val classes = HashMap<Class<*>, EntityType>()

    /**
     * Registers a new customEntity Clazz as the given [entityType].
     * Does nothing if the class is already registered.
     */
    override fun <C> register(customEntityClazz: C, entityType: EntityType) {
        if (customEntityClazz !is Class<*>) {
            throw IllegalArgumentException("CustomEntityClass has to be a Class!")
        }

        if (classes.containsKey(customEntityClazz)) {
            return
        }

        val entityTypeClazz = findClass("net.minecraft.server.VERSION.EntityTypes")
        val entityTypeD = this.findMap<Class<*>, String>(entityTypeClazz, "d")
        val entityTypeF = this.findMap<Class<*>, Int>(entityTypeClazz, "f")

        entityTypeD[customEntityClazz] = entityType.saveGame_18
        entityTypeF[customEntityClazz] = entityType.entityId

        classes[customEntityClazz] = entityType
    }

    /**
     * Clears all resources this service has allocated and reverts internal
     * nms changes.
     */
    override fun clearResources() {
        val entityTypeClazz = findClass("net.minecraft.server.VERSION.EntityTypes")
        val entityTypeD = this.findMap<Class<*>, String>(entityTypeClazz, "d")
        val entityTypeF = this.findMap<Class<*>, Int>(entityTypeClazz, "f")

        classes.forEach { (customEntityClazz, _) ->
            entityTypeD.remove(customEntityClazz)
            entityTypeF.remove(customEntityClazz)
        }

        classes.clear()
    }

    /**
     * Finds the given map from [name] and [clazz].
     */
    private fun <T, G> findMap(clazz: Class<*>, name: String): MutableMap<T, G> {
        val field = clazz.getDeclaredField(name)
        field.isAccessible = true
        return field.get(null) as MutableMap<T, G>
    }

    /**
     * Finds the given class by [name].
     */
    private fun findClass(name: String): Class<*> {
        return Class.forName(name.replace("VERSION", version.bukkitId))
    }
}