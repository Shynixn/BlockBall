package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.command.CommandType
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.sign.SignMeta

class TeamMeta {
    @Comment("The display name of the team. Supports Minecraft chat colors.")
    var displayName: String = ""

    @Comment("The amount of points a team gets when a player scores a goal.")
    var pointsPerGoal: Int = 1

    @Comment("The amount of points a team gets when a player of the opposite team dies.")
    var pointsPerEnemyDeath: Int = 0

    @Comment("All signs to join this team. Use the /blockball sign command ingame to configure this field.")
    val teamSigns = ArrayList<SignMeta>()

    @Comment("Min amount of players in this team to start the match for this team.")
    var minAmount: Int = 0

    @Comment("Max amount of players in this team to start the match for this team.")
    var maxAmount: Int = 10

    @Comment("Minimum amount of players in this team to keep the game running.")
    var minPlayingPlayers: Int = 0

    /** Goal properties of the team. */
    @Comment("Goal location properties.")
    val goal: Selection = Selection()

    @Comment("The armor a player receives when joining this team. Use the /blockball armor command ingame to configure this.")
    var armor: Array<String?> = arrayOfNulls(4)

    @Comment("The inventory a player receives when joining this team. Use the /blockball inventory command ingame to configure this.")
    var inventory: Array<String?> = arrayOfNulls(36)

    /** Spawnpoint of the team inside the soccerArena. */
    @Comment("The spawnpoint of this team inside of the game.")
    var spawnpoint: Vector3d? = null

    @Comment("The lobby spawnpoint of this team inside of the game.")
    var lobbySpawnpoint: Vector3d? = null

    @Comment("The commands being executed when a team wins. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var winCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands being executed when a team looses. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var looseCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands being executed when a game ends in a draw. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var drawCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands being executed when a goal is scored. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER. Is not triggered on own goals.")
    var goalCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands being executed when a player joins this team. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var joinCommands: List<CommandMeta> = listOf(
        CommandMeta(
            CommandType.SERVER_PER_PLAYER,
            "/blockballscoreboard add blockball_scoreboard %blockball_player_name%"
        ),
        CommandMeta(
            CommandType.SERVER_PER_PLAYER,
            "/blockballbossbar add blockball_bossbar %blockball_player_name%"
        )
    )

    @Comment("The commands being executed when a player leaves this team. A command always starts with an slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var leaveCommands: List<CommandMeta> = listOf(
        CommandMeta(
            CommandType.SERVER_PER_PLAYER,
            "/blockballscoreboard remove blockball_scoreboard %blockball_player_name%"
        ),
        CommandMeta(
            CommandType.SERVER_PER_PLAYER,
            "/blockballbossbar remove blockball_bossbar %blockball_player_name%"
        )
    )
}
