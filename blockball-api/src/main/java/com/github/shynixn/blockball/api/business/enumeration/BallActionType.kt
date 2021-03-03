package com.github.shynixn.blockball.api.business.enumeration

/**
 * Types of action a ball can execute. Mostly used for events and displaying
 * effects such as particles and sounds.
 */
enum class BallActionType {
    /**
     * Ball Pass.
     */
    ONPASS,

    /**
     * Ball Kick.
     */
    ONKICK,

    /**
     * Ball Touch.
     */
    ONINTERACTION,

    /**
     * Ball Spawn.
     */
    ONSPAWN,

    /**
     * Ball Score.
     */
    ONGOAL,

    /**
     * Ball Move.
     */
    ONMOVE,

    @Deprecated("No longer being used. Stays for compatibility reason since v6.22.1")
    ONTHROW,

    @Deprecated("No longer being used. Stays for compatibility reason since v6.22.1")
    ONGRAB,
}
