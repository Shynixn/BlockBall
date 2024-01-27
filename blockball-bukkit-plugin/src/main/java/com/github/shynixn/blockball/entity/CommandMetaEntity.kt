package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.CommandMode
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta

class CommandMetaEntity : CommandMeta {
    /** Mode how the command gets executed. */
    @YamlSerialize(value = "mode", orderNumber = 1)
    override var mode: CommandMode = CommandMode.CONSOLE_PER_PLAYER
    /** Command to be executed. */
    @YamlSerialize(value = "command", orderNumber = 2)
    override var command: String? = null
}
