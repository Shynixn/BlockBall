package com.github.shynixn.blockball.enumeration

enum class GameSubState {
    /**
     * When there is no particular important state.
     */
    FREE,

    /**
     * Close game.
     */
    CLOSED,

    BALL_RESPAWNED,
    BALL_DESTROYED,

    /**
     * When the ball has been shot of out of bounds and a player should be teleported to the throw-in position.
     */
    BALL_OUT_TELEPORT,
    BALL_OUT_THROW_IN_PERFORM,
    BALL_OUT_CORNER_KICK_PERFORM,
    BALL_OUT_GOAL_KICK_PERFORM,
}