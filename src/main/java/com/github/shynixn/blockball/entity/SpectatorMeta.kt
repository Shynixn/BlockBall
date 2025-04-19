package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment


class SpectatorMeta  {
    @Comment("OBSOLETE! May not work anymore and will be removed in the future. Should nearby players be messages by title messages, scoreboards and bossbars?")
    var notifyNearbyPlayers: Boolean = false
    @Comment("OBSOLETE! May not work anymore and will be removed in the future Notification radius.")
    var notificationRadius: Int = 50
}
