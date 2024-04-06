package com.github.shynixn.blockball.entity

class ScoreboardMeta {
    /** Title of the scoreboard. */
    var title: String = "&aBlockBall"

    /** Is the scoreboard visible. */
    var enabled: Boolean = true

    /** Lines of the scoreboard being rendered. */
    val lines: ArrayList<String> = arrayListOf(
        "",
        "&6Time: ",
        "%blockball_game_time%",
        "",
        "&m           &r",
        "&cTeam Red:",
        "%blockball_game_redScore%&l",
        "",
        "&9Team Blue:",
        "%blockball_game_blueScore%&l",
        "&m           &r"
    )
}
