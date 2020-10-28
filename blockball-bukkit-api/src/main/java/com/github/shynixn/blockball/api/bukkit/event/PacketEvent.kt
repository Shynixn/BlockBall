package com.github.shynixn.blockball.api.bukkit.event

import org.bukkit.entity.Player

class PacketEvent(val player: Player, val packet: Any) : BlockBallEvent()
