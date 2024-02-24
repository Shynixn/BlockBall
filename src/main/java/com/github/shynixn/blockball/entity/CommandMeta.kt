package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.CommandMode

class CommandMeta {
    /** Mode how the command gets executed. */
    @YamlSerialize(value = "mode", orderNumber = 1)
    var mode: CommandMode = CommandMode.CONSOLE_PER_PLAYER
    /** Command to be executed. */
    @YamlSerialize(value = "command", orderNumber = 2)
    var command: String? = null
}
