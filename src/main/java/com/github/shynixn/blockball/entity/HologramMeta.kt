package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d

class HologramMeta {
    /** Position of the hologram. */
    var position: Vector3d? = null

    /** Lines of the hologram being rendered. */
    val lines: MutableList<String> = mutableListOf("%blockball_lang_hologramMessage%")
}
