package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.ScoreboardMeta

class ScoreboardEntity : ScoreboardMeta {
    /** Title of the scoreboard. */
    @YamlSerialize(orderNumber = 1, value = "title")
    override var title: String = "&aBlockBall"

    /** Is the scoreboard visible. */
    @YamlSerialize(orderNumber = 2, value = "enabled")
    override var enabled: Boolean = false

    /** Lines of the scoreboard being rendered. */
    @YamlSerialize(orderNumber = 3, value = "lines")
    override val lines: ArrayList<String> = arrayListOf(
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
