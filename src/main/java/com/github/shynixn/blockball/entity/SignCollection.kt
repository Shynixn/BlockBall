package com.github.shynixn.blockball.entity


class SignCollection {
    /** List of signs which can be clicked to join the game. */
    val joinSigns: MutableList<Position> = ArrayList()
    /** List of signs which can be clicked to leave the game. */
    val leaveSigns: MutableList<Position> = ArrayList()
}
