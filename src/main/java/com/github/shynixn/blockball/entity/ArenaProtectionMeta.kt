package com.github.shynixn.blockball.entity


class ArenaProtectionMeta  {
    /** Velocity being applied when an entity enters the arena which is not an player or armorstand. */
    var entityProtection: Position = Position(5.0, 2.0, 5.0)
    /** Should a velocity be applied to entities which are not an player or armorstand. */
    var entityProtectionEnabled: Boolean = true
    /** Velocity being applied when a player rejoins the server into a field of an arena. */
    var rejoinProtection: Position = Position(0.0, 2.0, 0.0)
    /** Should a velocity be applied to players which rejoin on the field of the arena. */
    var rejoinProtectionEnabled: Boolean = true
}
