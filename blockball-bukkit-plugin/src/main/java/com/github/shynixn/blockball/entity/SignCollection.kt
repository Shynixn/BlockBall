package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class SignCollection {
    /** List of signs which can be clicked to join the game. */
    @YamlSerialize(orderNumber = 1, value = "joining")
    val joinSigns: MutableList<Position> = ArrayList()
    /** List of signs which can be clicked to leave the game. */
    @YamlSerialize(orderNumber = 2, value = "leaving")
    val leaveSigns: MutableList<Position> = ArrayList()
}
