package com.github.shynixn.blockball.entity

class LeaderBoardStats {
    var topTenGoals = ArrayList<Pair<String, Int>>()
    var topTenGoalsFull = ArrayList<Pair<String, Int>>()

    var topTenOwnGoals = ArrayList<Pair<String, Int>>()
    var topTenOwnGoalsFull = ArrayList<Pair<String, Int>>()

    var topTenTotalGoals = ArrayList<Pair<String, Int>>()
    var topTenTotalGoalsFull = ArrayList<Pair<String, Int>>()

    var topTenGames = ArrayList<Pair<String, Int>>()
    var topTenGamesFull = ArrayList<Pair<String, Int>>()

    var topTenWins = ArrayList<Pair<String, Int>>()
    var topTenLosses = ArrayList<Pair<String, Int>>()
    var topTenDraws = ArrayList<Pair<String, Int>>()
    var topTenWinRate = ArrayList<Pair<String, Float>>()

    var topTenGoalsPerGame = ArrayList<Pair<String, Float>>()
    var topTenGoalsPerGameFull = ArrayList<Pair<String, Float>>()

    var topTenOwnGoalsPerGame = ArrayList<Pair<String, Float>>()
    var topTenOwnGoalsPerGameFull = ArrayList<Pair<String, Float>>()

    var topTenTotalGoalsPerGame = ArrayList<Pair<String, Float>>()
    var topTenTotalGoalsPerGameFull = ArrayList<Pair<String, Float>>()

    fun getNameOrEmpty(leaderBoardStats: List<Pair<String, *>>, index: Int): String {
        if (index >= 0 && index < leaderBoardStats.size) {
            return leaderBoardStats[index].first
        }

        return ""
    }

    fun getValueIntOrEmpty(leaderBoardStats: List<Pair<String, Int>>, index: Int): String {
        if (index >= 0 && index < leaderBoardStats.size) {
            return leaderBoardStats[index].second.toString()
        }

        return ""
    }

    fun getValueFloatOrEmpty(leaderBoardStats: List<Pair<String, Float>>, index: Int): String {
        if (index >= 0 && index < leaderBoardStats.size) {
            return String.format("%.2f", leaderBoardStats[index].second)
        }

        return ""
    }
}
