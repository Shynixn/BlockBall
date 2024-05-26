package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d


class SignCollection {
    /** List of signs which can be clicked to join the game. */
    val joinSigns: MutableList<Vector3d> = ArrayList()
    /** List of signs which can be clicked to leave the game. */
    val leaveSigns: MutableList<Vector3d> = ArrayList()
}
