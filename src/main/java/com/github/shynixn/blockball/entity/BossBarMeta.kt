package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BossBarFlag
import com.github.shynixn.blockball.enumeration.BossbarColor
import com.github.shynixn.blockball.enumeration.BossbarStyle

class BossBarMeta {
    /** Color of the bossbar. */
    var color: BossbarColor = BossbarColor.WHITE
    /** Is bossbar visible. */
    var enabled: Boolean = false
    /** Displaying message. */
    var message: String = "%blockball_lang_bossBarMessage%"
    /** Percentage filled in the bossbar. */
    var percentage: Double = 100.0
    /** Style of the bossbar. */
    var style: BossbarStyle = BossbarStyle.SOLID
    /** Flags of the bossbar. */
    val flags: MutableList<BossBarFlag> = ArrayList()
}
