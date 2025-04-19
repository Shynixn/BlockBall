package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BossBarFlag
import com.github.shynixn.blockball.enumeration.BossbarColor
import com.github.shynixn.blockball.enumeration.BossbarStyle
import com.github.shynixn.mcutils.common.repository.Comment

class BossBarMeta {
    @Comment("Color of the bossbar.")
    var color: BossbarColor = BossbarColor.WHITE
    @Comment("Should the bossbar be displayed?")
    var enabled: Boolean = true
    @Comment("Message displayed in the bossbar. This value supports placeholders.")
    var message: String = "%blockball_game_redDisplayName% %blockball_game_redScore% : &9%blockball_game_blueScore% %blockball_game_blueDisplayName%"
    @Comment("The percentage how much the bossbar is filled from 0.0 to 100.0")
    var percentage: Double = 100.0
    @Comment("Style of the bossbar. Possible values: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20.")
    var style: BossbarStyle = BossbarStyle.SOLID
    @Comment("List of bossbar flags. Possible values: CREATE_FOG, DARKEN_SKY, PLAY_BOSS_MUSIC")
    val flags: MutableList<BossBarFlag> = ArrayList()
}
