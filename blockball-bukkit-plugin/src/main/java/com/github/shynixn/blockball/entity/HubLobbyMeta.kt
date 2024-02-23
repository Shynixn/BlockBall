package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class HubLobbyMeta {
    /** Join asking message. */
    @YamlSerialize(orderNumber = 1, value = "join-selection")
    var joinMessage: MutableList<String> = arrayListOf(
        "%blockball_lang_hubGameJoinHeader%",
        "%blockball_lang_hubGameJoinRed%",
        "%blockball_lang_hubGameJoinBlue%"
    )

    /** Allows to instantly play in games by running into the forcefield.*/
    @YamlSerialize(orderNumber = 2, value = "instant-forcefield-join")
    var instantForcefieldJoin: Boolean = false

    /** Should the arena be reset when nobody is playing? */
    @YamlSerialize(orderNumber = 3, value = "reset-arena-on-empty")
    var resetArenaOnEmpty: Boolean = false

    /** Should the player be teleported to the spawnpoint when joining?*/
    @YamlSerialize(orderNumber = 4, value = "teleport-on-join")
    var teleportOnJoin: Boolean = true
}
