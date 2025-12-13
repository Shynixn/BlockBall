package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment

class CloudMeta {
    @Comment("Name of the game when published on the website. Open https://blockball.shynixn.com/ to setup your website.")
    var name: String = ""

    @Comment("Should the game be published on the website? Open https://blockball.shynixn.com/ to setup your website.")
    var enabled: Boolean = false
}