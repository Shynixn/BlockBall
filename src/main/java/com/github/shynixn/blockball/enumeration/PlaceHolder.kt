package com.github.shynixn.blockball.enumeration

enum class PlaceHolder(
    /**
     * Placeholder value.
     */
    val fullPlaceHolder: String
) {
    // Game PlaceHolders
    GAME_DISPLAYNAME("%blockball_game_displayName%"),

    GAME_NAME("%blockball_game_name%"),

    GAME_SUM_MAXPLAYERS("%blockball_game_maxPlayers%"),

    GAME_SUM_CURRENTPLAYERS("%blockball_game_players%"),

    GAME_RED_SCORE("%blockball_game_redScore%"),

    GAME_BLUE_SCORE("%blockball_game_blueScore%"),

    GAME_TEAM_RED_NAME("%blockball_game_redName%"),

    GAME_TEAM_BLUE_NAME("%blockball_game_blueName%"),

    GAME_TEAM_RED_COLOR("%blockball_game_redColor%"),

    GAME_TEAM_BLUE_COLOR("%blockball_game_blueColor%"),

    GAME_TIME("%blockball_game_time%"),

    GAME_LASTHITPLAYER_NAME("%blockball_game_lastHitPlayerName%"),

    GAME_STATE("%blockball_game_state%"),

    GAME_STATE_DISPLAYNAME("%blockball_game_stateDisplayName%"),

    GAME_IS_ENABLED("%blockball_game_isEnabled%"),

    GAME_IS_JOINABLE("%blockball_game_isJoinAble%"),

    GAME_REMAININGPLAYERS_TO_START("%blockball_game_remainingPlayers%"),

    // Team PlaceHolders

    TEAM_NAME("%blockball_team_name%"),

    TEAM_COLOR("%blockball_team_color%"),

    TEAM_MAX_PLAYERS("%blockball_team_maxPlayers%"),

    TEAM_PLAYERS("%blockball_team_players%"),

    // Player PlaceHolders

    PLAYER_IS_INGAME("%blockball_player_isInGame%"),

    PLAYER_IS_IN_TEAM_RED("%blockball_player_isInTeamRed%"),

    PLAYER_IS_IN_TEAM_BLUE("%blockball_player_isInTeamBlue%"),

    PLAYER_STATS_SCOREDGOALS("%blockball_player_goals%"),

    PLAYER_STATS_PLAYEDGAMES("%blockball_player_games%"),

    PLAYER_STATS_WINS("%blockball_player_wins%"),

    PLAYER_STATS_LOSSES("%blockball_player_losses%"),

    PLAYER_STATS_WINRATE("%blockball_player_winrate%"),

    PLAYER_STATS_GOALSPERGAME("%blockball_player_goalsPerGameRate%")
}
