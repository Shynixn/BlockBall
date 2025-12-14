package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment

class CloudMeta {
    @Comment("Name of the game when published on the website. Open https://blockball.shynixn.com/ to setup your website.")
    var name: String = "Game"

    @Comment("Should the game be published on the website? Open https://blockball.shynixn.com/ to setup your website.")
    var enabled: Boolean = false

    @Comment("Name of the red team when published on the website. Open https://blockball.shynixn.com/ to setup your website.")
    var redTeamName: String = "Team Red"

    @Comment("Name of the blue team when published on the website. Open https://blockball.shynixn.com/ to setup your website.")
    var blueTeamName: String = "Team Blue"
}