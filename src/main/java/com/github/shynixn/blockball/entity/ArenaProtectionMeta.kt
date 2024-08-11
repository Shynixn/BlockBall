package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d


class ArenaProtectionMeta  {
    /** Velocity being applied when an entity enters the arena which is not an player or armorstand. */
    var entityProtection: Vector3d = Vector3d(5.0, 2.0, 5.0)
    /** Should a velocity be applied to entities which are not an player or armorstand. */
    var entityProtectionEnabled: Boolean = true
    /** Velocity being applied when a player rejoins the server into a field of an arena. */
    var rejoinProtection: Vector3d = Vector3d(1.0, 2.0, 0.0)
    /** Should a velocity be applied to players which rejoin on the field of the arena. */
    var rejoinProtectionEnabled: Boolean = true
}
