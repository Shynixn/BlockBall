package com.github.shynixn.blockball.entity

class HubLobbyMeta {
    /** Join asking message. */
    var joinMessage: MutableList<String> = arrayListOf(
        "%blockball_lang_hubGameJoinHeader%",
        "%blockball_lang_hubGameJoinRed%",
        "%blockball_lang_hubGameJoinBlue%"
    )

    /** Allows to instantly play in games by running into the forcefield.*/
    var instantForcefieldJoin: Boolean = false

    /** Should the arena be reset when nobody is playing? */
    var resetArenaOnEmpty: Boolean = false

    /** Should the player be teleported to the spawnpoint when joining?*/
    var teleportOnJoin: Boolean = true
}
