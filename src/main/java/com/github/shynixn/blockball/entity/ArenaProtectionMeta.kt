package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d


class ArenaProtectionMeta  {
    /** Velocity being applied when a player rejoins the server into a field of an arena. */
    var rejoinProtection: Vector3d = Vector3d(1.0, 2.0, 0.0)
    /** Should a velocity be applied to players which rejoin on the field of the arena. */
    var rejoinProtectionEnabled: Boolean = true
}
