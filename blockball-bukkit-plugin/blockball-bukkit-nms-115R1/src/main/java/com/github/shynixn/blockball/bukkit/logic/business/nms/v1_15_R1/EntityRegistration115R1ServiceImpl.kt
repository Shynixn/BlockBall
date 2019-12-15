package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_15_R1

import com.github.shynixn.blockball.api.business.enumeration.EntityType
import com.github.shynixn.blockball.api.business.service.EntityRegistrationService
import net.minecraft.server.v1_15_R1.*

/**
 * The EntityRegistration114R1ServiceImpl handles registering the custom PetBlocks entities into Minecraft.
 * <p>
 * Version 1.3
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
class EntityRegistration115R1ServiceImpl : EntityRegistrationService {
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

        val entityRegistry = IRegistry.ENTITY_TYPE as RegistryBlocks<EntityTypes<*>>
        val key = entityType.saveGame_11
        val internalRegistry = (entityRegistry.get(MinecraftKey(key)));

        val size =  EntityTypes::class.java.getDeclaredMethod("k").invoke(internalRegistry) as EntitySize

        val entityTypes =
            IRegistry.a(entityRegistry, "petblocks_" + key.toLowerCase(), EntityTypes.a.a<Entity>(EnumCreatureType.CREATURE).b().a().a(size.width, size.height).a(key))

        val registryMaterialsField = RegistryMaterials::class.java.getDeclaredField("b")
        registryMaterialsField.isAccessible = true
        val registryId = registryMaterialsField.get(entityRegistry)

        val dMethod = RegistryID::class.java.getDeclaredMethod("d", Any::class.java)
        dMethod.isAccessible = true
        val dValue = dMethod.invoke(registryId, entityTypes)

        val bMethod = RegistryID::class.java.getDeclaredMethod("b", Any::class.java, Int::class.javaPrimitiveType)
        bMethod.isAccessible = true
        val bValue = bMethod.invoke(registryId, entityTypes, dValue) as Int

        val cField = RegistryID::class.java.getDeclaredField("c")
        cField.isAccessible = true
        val c = cField.get(registryId) as IntArray

        c[bValue] = entityType.entityId

        classes[customEntityClazz] = entityType
    }

    /**
     * Clears all resources this service has allocated and reverts internal
     * nms changes.
     */
    override fun clearResources() {
        classes.clear()
    }
}