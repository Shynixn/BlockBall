package com.github.shynixn.blockball.contract

interface CommandService {
    /**
     * Registers a command executor from a pre defined [command] with gets executed by the [commandExecutor].
     */
    fun registerCommandExecutor(command: String, commandExecutor: CommandExecutor)

    /**
     * Registers a command executor from new [commandConfiguration] with gets executed by the [commandExecutor].
     */
    fun registerCommandExecutor(commandConfiguration: Map<String, String>, commandExecutor: CommandExecutor)
}
