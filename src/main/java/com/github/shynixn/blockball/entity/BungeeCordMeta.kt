package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class BungeeCordMeta {
    /**
     * FallBack server when a player executes the leave command.
     */
    @YamlSerialize(orderNumber = 1, value = "fallback-server")
    var fallbackServer: String = ""
}
