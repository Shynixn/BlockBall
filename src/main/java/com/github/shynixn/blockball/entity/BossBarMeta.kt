package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.BossBarFlag
import com.github.shynixn.blockball.enumeration.BossbarColor
import com.github.shynixn.blockball.enumeration.BossbarStyle

class BossBarMeta {
    /** Color of the bossbar. */
    @YamlSerialize("color", orderNumber = 4)
    var color: BossbarColor = BossbarColor.WHITE
    /** Is bossbar visible. */
    @YamlSerialize("enabled", orderNumber = 1)
    var enabled: Boolean = false
    /** Displaying message. */
    @YamlSerialize("text", orderNumber = 2)
    var message: String = "%blockball_lang_bossBarMessage%"
    /** Percentage filled in the bossbar. */
    @YamlSerialize("percentage", orderNumber = 3)
    var percentage: Double = 100.0
    /** Style of the bossbar. */
    @YamlSerialize("style", orderNumber = 5)
    var style: BossbarStyle = BossbarStyle.SOLID
    /** Flags of the bossbar. */
    @YamlSerialize("flags", orderNumber = 6)
    val flags: MutableList<BossBarFlag> = ArrayList()
}
