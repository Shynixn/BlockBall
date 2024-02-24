package com.github.shynixn.blockball.contract

import org.bukkit.entity.Player

interface ScreenMessageService {
    /**
     * Sets the [title] of the given [player] [P] for the amount of [stay] ticks. Optionally shows a [subTitle] and displays
     * a [fadeIn] and [fadeOut] effect in ticks.
     */
    fun setTitle(player: Player, title: String, subTitle: String = "", fadeIn: Int, stay: Int, fadeOut: Int)

    /**
     * Sets the [message] for the given [player] at the actionbar.
     */
    fun setActionBar(player: Player, message: String)
}
