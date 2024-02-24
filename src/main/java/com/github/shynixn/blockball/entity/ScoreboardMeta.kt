package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize


class ScoreboardMeta {
    /** Title of the scoreboard. */
    @YamlSerialize(orderNumber = 1, value = "title")
    var title: String = "&aBlockBall"

    /** Is the scoreboard visible. */
    @YamlSerialize(orderNumber = 2, value = "enabled")
    var enabled: Boolean = false

    /** Lines of the scoreboard being rendered. */
    @YamlSerialize(orderNumber = 3, value = "lines")
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
