package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordMeta

class BungeeCordMetaEntity : BungeeCordMeta {
    /**
     * FallBack server when a player executes the leave command.
     */
    @YamlSerialize(orderNumber = 1, value = "fallback-server")
    override var fallbackServer: String = ""
}
