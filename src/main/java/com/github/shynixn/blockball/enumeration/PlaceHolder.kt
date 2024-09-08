package com.github.shynixn.blockball.enumeration

enum class PlaceHolder(
    /**
     * Placeholder value.
     */
    val fullPlaceHolder: String
) {

    // Game PlaceHolders

    GAME_NAME("%blockball_game_name%"),

    GAME_DISPLAYNAME("%blockball_game_displayName%"),

    GAME_SUM_MAXPLAYERS("%blockball_game_maxPlayers%"),

    GAME_SUM_CURRENTPLAYERS("%blockball_game_players%"),

    GAME_RED_SCORE("%blockball_game_redScore%"),

    GAME_BLUE_SCORE("%blockball_game_blueScore%"),

    GAME_TIME("%blockball_game_time%"),

    GAME_LASTHITPLAYER_NAME("%blockball_game_lastHitPlayerName%"),

    GAME_LASTHITPLAYER_TEAM_NAME("%blockball_game_lastHitPlayerTeam%"),

    GAME_STATE("%blockball_game_state%"),

    GAME_STATE_DISPLAYNAME("%blockball_game_stateDisplayName%"),

    GAME_IS_ENABLED("%blockball_game_isEnabled%"),

    GAME_IS_JOINABLE("%blockball_game_isJoinAble%"),

    GAME_REMAININGPLAYERS_TO_START("%blockball_game_remainingPlayers%"),

    // Player PlaceHolders

    PLAYER_NAME("%blockball_player_name%"),

    PLAYER_IS_INGAME("%blockball_player_isInGame%"),

    PLAYER_IS_IN_TEAM_RED("%blockball_player_isInTeamRed%"),

    PLAYER_IS_IN_TEAM_BLUE("%blockball_player_isInTeamBlue%"),

    // Play Stats PlaceHolders

    PLAYER_STATS_GOALS("%blockball_player_goals%"),

    PLAYER_STATS_GOALSFULL("%blockball_player_goalsFull%"),

    PLAYER_STATS_OWNGOALS("%blockball_player_ownGoals%"),

    PLAYER_STATS_OWNGOALSFULL("%blockball_player_ownGoalsFull%"),

    PLAYER_STATS_TOTALGOALS("%blockball_player_totalGoals%"),

    PLAYER_STATS_TOTALGOALSFULL("%blockball_player_totalGoalsFull%"),

    PLAYER_STATS_GAMES("%blockball_player_games%"),

    PLAYER_STATS_GAMESFULL("%blockball_player_gamesFull%"),

    PLAYER_STATS_WINS("%blockball_player_wins%"),

    PLAYER_STATS_LOSSES("%blockball_player_losses%"),

    PLAYER_STATS_DRAWS("%blockball_player_draws%"),

    PLAYER_STATS_WINRATE("%blockball_player_winrate%"),

    PLAYER_STATS_WINRATEFULL("%blockball_player_winrateFull%"),

    PLAYER_STATS_GOALSPERGAME("%blockball_player_goalsPerGame%"),

    PLAYER_STATS_GOALSPERGAMEFULL("%blockball_player_goalsPerGameFull%"),

    PLAYER_STATS_OWNGOALSPERGAME("%blockball_player_ownGoalsPerGame%"),

    PLAYER_STATS_OWNGOALSPERGAMEFULL("%blockball_player_ownGoalsPerGameFull%"),

    PLAYER_STATS_TOTALGOALSPERGAME("%blockball_player_totalGoalsPerGame%"),

    PLAYER_STATS_TOTALGOALSPERGAMEFULL("%blockball_player_totalGoalsPerGameFull%")
}
