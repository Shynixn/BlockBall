package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.persistence.entity.Arena

class ArenaEntity : SelectionEntity(), Arena {
    /** Unique [name] of the arena. */
    @YamlSerialize(orderNumber = 1, value = "name")
    override var name: String = ""

    /** [displayName] of the arena on signs or messages. */
    @YamlSerialize(orderNumber = 2, value = "displayname")
    override var displayName: String = ""

    /** Is the arena ready to be placed. */
    @YamlSerialize(orderNumber = 3, value = "enabled")
    override var enabled: Boolean = true

    /** [gameType] of the arena */
    @YamlSerialize(orderNumber = 4, value = "gamemode")
    override var gameType: GameType = GameType.HUBGAME

    /** Collection of the arena meta data. */
    @YamlSerialize(orderNumber = 7, value = "meta")
    override val meta: ArenaMetaEntity = ArenaMetaEntity()
}
