package com.github.shynixn.blockball.entity

class HubLobbyMeta {
    /** Allows to instantly play in games by running into the forcefield.*/
    var instantForcefieldJoin: Boolean = false

    /** Should the soccerArena be reset when nobody is playing? */
    var resetArenaOnEmpty: Boolean = false

    /** Should the player be teleported to the spawnpoint when joining?*/
    var teleportOnJoin: Boolean = true
}
