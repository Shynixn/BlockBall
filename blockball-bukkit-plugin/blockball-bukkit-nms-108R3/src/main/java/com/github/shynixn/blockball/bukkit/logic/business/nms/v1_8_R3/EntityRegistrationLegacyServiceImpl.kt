package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_8_R3

import com.github.shynixn.blockball.api.business.enumeration.EntityType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.service.EntityRegistrationService
import com.google.inject.Inject

class EntityRegistrationLegacyServiceImpl @Inject constructor(private val version: Version) : EntityRegistrationService {
    /**
     * Registers a new customEntity Clazz as the given [entityType].
     * Does nothing if the class is already registered.
     */
    override fun <C> register(customEntityClazz: C, entityType: EntityType) {
        // Does nothing
    }

    /**
     * Clears all resources this service has allocated and reverts internal
     * nms changes.
     */
    override fun clearResources() {
        // Does nothing
    }
}