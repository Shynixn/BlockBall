package com.github.shynixn.blockball.entity

class StatsMeta {
    /**
     * Version of the tracking.
     */
    var version: Int = 1

    /**
     * Scored goals in enemy goal.
     */
    var scoredGoals: Int = 0

    /**
     * Scored goals in enemy goal over all win games.
     */
    var scoredGoalsFull: Int = 0

    /**
     * Scored goals in own goal.
     */
    var scoredOwnGoals: Int = 0

    /**
     * Scored goals in own goal over all win games.
     */
    var scoredOwnGoalsFull: Int = 0

    /**
     * Amount of completed played games.
     */
    var playedGames: Int = 0

    /**
     * Amount of games a player has started.
     */
    var joinedGames: Int = 0

    /**
     * Amount of wins.
     */
    var winsAmount: Int = 0

    /**
     * Amount of draws.
     */
    var drawsAmount: Int = 0
}
