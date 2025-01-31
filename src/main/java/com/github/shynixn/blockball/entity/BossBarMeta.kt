package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BossBarFlag
import com.github.shynixn.blockball.enumeration.BossbarColor
import com.github.shynixn.blockball.enumeration.BossbarStyle

class BossBarMeta {
    /** Color of the bossbar. */
    var color: BossbarColor = BossbarColor.WHITE
    /** Is bossbar visible. */
    var enabled: Boolean = true
    /** Displaying message. */
    var message: String = "%blockball_game_redDisplayName% %blockball_game_redScore% : &9%blockball_game_blueScore% %blockball_game_blueDisplayName%"
    /** Percentage filled in the bossbar. */
    var percentage: Double = 100.0
    /** Style of the bossbar. */
    var style: BossbarStyle = BossbarStyle.SOLID
    /** Flags of the bossbar. */
    val flags: MutableList<BossBarFlag> = ArrayList()
}
