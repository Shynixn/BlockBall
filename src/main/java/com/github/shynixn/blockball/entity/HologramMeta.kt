package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d

class HologramMeta {
    /** Position of the hologram. */
    var position: Vector3d? = null

    /** Lines of the hologram being rendered. */
    val lines: MutableList<String> = mutableListOf("&cTeam Red %blockball_game_redScore% : &9Team Blue %blockball_game_blueScore%")
}
