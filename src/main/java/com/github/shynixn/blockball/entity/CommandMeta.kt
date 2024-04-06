package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.CommandMode

class CommandMeta {
    /** Mode how the command gets executed. */
    var mode: CommandMode = CommandMode.CONSOLE_PER_PLAYER
    /** Command to be executed. */
    var command: String? = null
}
