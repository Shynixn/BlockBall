package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment

class HubLobbyMeta {
    @Comment("If gameType=HUBGAME and if this is enabled then players instantly join a game without getting the join prompt.")
    var instantForcefieldJoin: Boolean = false

    @Comment("If gameType=HUBGAME, should the game reset itself to 0:0 score when nobody is playing?")
    var resetArenaOnEmpty: Boolean = false

    @Comment("If gameType=HUBGAME, should players be teleported to their teamspawnpoint when joining instead of joining at the place where they entered the game?")
    var teleportOnJoin: Boolean = true
}
