package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.ArenaProtectionMeta
import com.github.shynixn.blockball.api.persistence.entity.Position

class ArenaProtectionMetaEntity : ArenaProtectionMeta {
    /** Velocity being applied when an entity enters the arena which is not an player or armorstand. */
    @YamlSerialize(value = "entity-protection", orderNumber = 2, implementation = PositionEntity::class)
    override var entityProtection: Position = PositionEntity(5.0, 2.0, 5.0)
    /** Should a velocity be applied to entities which are not an player or armorstand. */
    @YamlSerialize(value = "entity-protection-enabled", orderNumber = 1)
    override var entityProtectionEnabled: Boolean = true
    /** Velocity being applied when a player rejoins the server into a field of an arena. */
    @YamlSerialize(value = "rejoin-protection", orderNumber = 4, implementation = PositionEntity::class)
    override var rejoinProtection: Position = PositionEntity(0.0, 2.0, 0.0)
    /** Should a velocity be applied to players which rejoin on the field of the arena. */
    @YamlSerialize(value = "rejoin-protection-enabled", orderNumber = 3)
    override var rejoinProtectionEnabled: Boolean = true
}
