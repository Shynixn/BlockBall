package com.github.shynixn.blockball.enumeration

/**
 * Types of action a ball can execute. Mostly used for events and displaying
 * effects such as particles and sounds.
 */
enum class BallActionType {
    /**
     * SoccerBall Pass.
     */
    ONPASS,

    /**
     * SoccerBall Kick.
     */
    ONKICK,

    /**
     * SoccerBall Touch.
     */
    ONINTERACTION,

    /**
     * SoccerBall Spawn.
     */
    ONSPAWN,

    /**
     * SoccerBall Score.
     */
    ONGOAL,

    /**
     * SoccerBall Move.
     */
    ONMOVE
}
