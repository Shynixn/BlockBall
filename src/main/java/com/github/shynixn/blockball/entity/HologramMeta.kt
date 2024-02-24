package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class HologramMeta {
    /** Position of the hologram. */
    @YamlSerialize("location", orderNumber = 1, implementation = Position::class)
    var position: Position? = null

    /** Lines of the hologram being rendered. */
    @YamlSerialize("lines", orderNumber = 1)
    val lines: MutableList<String> = mutableListOf("%blockball_lang_hologramMessage%")
}
