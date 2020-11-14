package com.github.shynixn.blockball.api.bukkit.event

import org.bukkit.entity.Player

/**
 * Event when a packet from the given player arrives.
 */
class PacketEvent(val player: Player, val packet: Any) : BlockBallEvent()
