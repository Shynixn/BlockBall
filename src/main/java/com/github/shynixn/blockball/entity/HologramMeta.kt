package com.github.shynixn.blockball.entity

class HologramMeta {
    /** Position of the hologram. */
    var position: Position? = null

    /** Lines of the hologram being rendered. */
    val lines: MutableList<String> = mutableListOf("%blockball_lang_hologramMessage%")
}
